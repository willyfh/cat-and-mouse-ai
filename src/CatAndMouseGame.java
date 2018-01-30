import java.awt.*;
import javax.swing.*;

public class CatAndMouseGame extends Thread {
	long delay;
	SwingApplet a;
	RLPolicy policy;
	CatAndMouseWorld world;
	static final int GREEDY=0, SMART=1; // type of mouse to use
	int mousetype = SMART;
	int episode = 0;
	boolean stopPressed = false;
	
	public boolean gameOn = false, single=false, gameActive, newInfo = false;
	
	public CatAndMouseGame(SwingApplet s, long delay, CatAndMouseWorld w, RLPolicy policy) {
		world = w;		
		a=s;
		this.delay = delay;
		this.policy = policy;
	}
	
	/* Thread Functions */
	public void run() {
		System.out.println("--Game thread started");
		// start game
		try {
			while(true) {
				while(gameOn) {
					gameActive = true;
					//resetGame();
					SwingUtilities.invokeLater(a); // draw initial state
					if (episode == 0) a.totalscore = 0; 
					runGame();
					gameActive = false;
					newInfo = true;
					SwingUtilities.invokeLater(a); // update state
					sleep(delay);
				}
				sleep(delay);
			}
		} catch (InterruptedException e) {
			System.out.println("interrupted.");
		}
		System.out.println("== Game finished.");
	}
	
	public void runGame() {
		while(!world.endGame() && !stopPressed) {
			//System.out.println("Game playing. Making move.");
			int action=-1;
			if (mousetype == GREEDY) {
				action = world.mouseAction();
			} else if (mousetype == SMART) {
				action = policy.getBestAction(world.getState());
			} else {
				System.err.println("Invalid mouse type:"+mousetype);
			}
									
			world.getNextState(action);
			a.mousescore = world.mousescore;
			a.catscore = world.catscore;
			//a.updateBoard();
			SwingUtilities.invokeLater(a);
				
			
			try {
				sleep(delay);
			} catch (InterruptedException e) {
				System.out.println("interrupted.");
			}
			
		}
		a.totalscore=a.totalscore + a.mousescore;
		world.mousescore = 0;
		a.mousescore= world.mousescore;
		episode++;
		a.episode = episode;
		SwingUtilities.invokeLater(a);
		
		// System.out.println("episode " + episode + " baru selesai");
		// turn off gameOn flag if only single game
		
		if (single){
			gameOn = false;
		
		}
		else if (!stopPressed && episode == 10) {
			gameOn = false;			
			episode = 0;
			world.mark=0;
			a.startbutt.setText("Start");
			a.startbutt.setActionCommand("S");
			//resetGame();			
		}
		else if (stopPressed)
		{
			a.mousescore = 0;
			a.catscore = 0;
			a.totalscore = 0;
			world.mark=0;
			episode = 0;			
			//resetGame();
		}
		resetGame();
	}
	
	public void interrupt() {
		super.interrupt();
		System.out.println("(interrupt)");
	}
	
	/* end Thread Functions */

	public void setPolicy(RLPolicy p) {	policy = p; }
	
	public Dimension getMouse() { return new Dimension(world.mx, world.my); }
	public int getMouseOrientation(){ return world.mo;}
	public Dimension getCat(int i) { return new Dimension(world.cx[i], world.cy[i]); }
	public Dimension getCheese(int i) { return new Dimension(world.chx[i], world.chy[i]); }
	public Dimension getHole() { return new Dimension(world.hx, world.hy); }
	public boolean[][] getWalls() { return world.walls; }
	
	/*public void makeMove() {
		world.moveMouse();
		world.moveCat();
	}*/

	public void resetGame() {
		world.resetState();
	}
}

