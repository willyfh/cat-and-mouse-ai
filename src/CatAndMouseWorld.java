import java.awt.*;
import java.util.ArrayList;
import java.util.Random;
public class CatAndMouseWorld implements RLWorld{
	public int bx, by;

	public int mx, my;
	public int mo;
	
	public int[] cx, cy;
	public int[] chx, chy;
	public int hx, hy;
	public boolean gotCheese = false;
	
	public int catscore = 0, mousescore = 0;
	public int cheeseReward=50, deathPenalty=100,melangkahPenalty=1,menabrakPenalty=2;

	
	public int batas_penglihatan = 10;
	public ArrayList<Integer> setPos;
	public int jumlah_kucing = 1;
	public int jumlah_keju = 1;	
	public int mark = 0;
	static final int NUM_ACTIONS=3, WALL_TRIALS=100;
	static final double INIT_VALS=0;
		
	int[] stateArray;
	double waitingReward;
	public boolean[][] walls;
	
	public int muter=0;

	public CatAndMouseWorld(int x, int y, int numWalls,int jum_kej, int jum_kuc,ArrayList<Integer> setPos_) {
		bx = x;
		by = y;
		cheeseReward=bx+by;
		deathPenalty=bx+by;
		Reader rd = new Reader();
		rd.ReadDataKoordinat();
		batas_penglihatan = rd.batas_penglihatan;
		jumlah_kucing = jum_kuc;
		jumlah_keju = jum_kej;
		chx =  new int[jumlah_keju];
		chy = new int[jumlah_keju];
		cx =  new int[jumlah_kucing];
		cy = new int[jumlah_kucing];
		setPos = setPos_;
		//System.out.println("train : "+setPos.size());
		makeWalls(x,y,numWalls);
		
		resetState();
	}
	
	public CatAndMouseWorld(int x, int y, boolean[][] newwalls,int jum_kej, int jum_kuc,ArrayList<Integer> setPos_) {
		bx = x;
		by = y;
		cheeseReward=bx+by;
		deathPenalty=bx+by;	
		Reader rd = new Reader();
		rd.ReadDataKoordinat();
		batas_penglihatan = rd.batas_penglihatan;
		jumlah_kucing = jum_kuc;
		jumlah_keju = jum_kej;
		chx =  new int[jumlah_keju];
		chy = new int[jumlah_keju];
		cx =  new int[jumlah_kucing];
		cy = new int[jumlah_kucing];
		setPos = setPos_;
		//System.out.println("play : "+setPos.size());
		walls = newwalls;
		
		resetState();
	}

	/*** kucing keju ****/
	public void setJumlahKucing(int jkc){
		jumlah_kucing=jkc;
	}
	public int getJumlahKucing(){
		return jumlah_kucing;
	}
	public void setJumlahKeju(int jkj){
		jumlah_keju=jkj;
	}
	public int getJumlahKeju(){
		return jumlah_keju;
	}
	/******* RLWorld interface functions ***********/
	public int[] getDimension() { 
		int[] retDim = new int[2+1];//obstacle, jarak, + action
		int i;
		for (i=0; i<2;) {//isi state cuman 2
			retDim[i++] = 4;//jenis halangan ada 4
			retDim[i++] = batas_penglihatan;
			
		}
		retDim[i] = NUM_ACTIONS;
		
		return retDim;
	}
		
	// given action determine next state
	public int[] getNextState(int action) {
		// action is mouse action:  0=u 1=ur 2=r 3=dr ... 7=ul
		Dimension d = getCoords(action);
		int ax=d.width, ay=d.height;
		boolean nabrakTembok = false;
		if (legal(ax,ay)) {
			// move agent
			mx = ax; my = ay;
			
		} else {
			
			nabrakTembok = true;
			
		//	System.err.println("nabrak tembok");
			//mousescore-=2;
		}
		// update world
		//moveCat();
		if (nabrakTembok){
			double newReward = 0;
			newReward-=menabrakPenalty;
			mousescore-=menabrakPenalty;
			waitingReward=newReward;
		}
		else{
			waitingReward = calcReward();
			//System.err.println("ga nabrak");
		}
		
		// if mouse has cheese, relocate cheese
		for (int z =0 ;z<jumlah_keju;z++){
			if ((mx==chx[z]) && (my==chy[z])) {
				//d = getRandomPos();
				chx[z] = -1;
				chy[z] = -1;
			}
		}
		
		/*// if cat has mouse, relocate mouse
		if ((mx==cx) && (my==cy)) {
			d = getRandomPos();
			mx = d.width;
			my = d.height;
		}*/
		return getState();
	}
	
	public double getReward(int i) { return getReward(); }
	public double getReward() {	return waitingReward; }
	
	public boolean validAction(int action) {
		Dimension d = getCoords(action);
		return legal(d.width, d.height);
	}
	
	Dimension getCoords(int action) {
		int ax=mx, ay=my;
		switch(action) {
			case 0:
				//ay = my - 1;
				//System.err.println("maju");
				switch(mo) {
					case 0: ay=my -1; break;
					case 1: ay = my - 1; ax = mx + 1; break;
					case 2: ax = mx + 1; break;
					case 3: ay = my + 1; ax = mx + 1; break;
					case 4: ay = my + 1; break;
					case 5: ay = my + 1; ax = mx - 1; break;
					case 6: ax = mx - 1; break;
					case 7: ay = my - 1; ax = mx - 1; break;
				}
			break;
			case 1:
				mo--;
				if (mo<0) mo=7;
				//System.err.println("hadap kiri");
			break;
			case 2:
				mo++;
				if (mo>7) mo=0;
				//System.err.println("hadap kanan");
			break;
			
			default: System.err.println("Invalid action: "+action);break;
		}
		return new Dimension(ax, ay);
	}

	// find action value given x,y=0,+-1
	int getAction(int x, int y) {// buat greedy
		int[][] vals={{7,0,1},
		              {6,0,2},
					  {5,4,3}};
		if ((x<-1) || (x>1) || (y<-1) || (y>1) || ((y==0)&&(x==0))) return -1;
		int retVal = vals[y+1][x+1];
		return retVal;
	}

	public boolean endState() { return endGame(); }
	public int[] resetState() { 
		catscore = 0;
		mousescore = 0;
		mo =0;
		setPos();
		return getState();
	}
		
	public double getInitValues() { return INIT_VALS; }
	/******* end RLWorld functions **********/
	
	public int[] getState() {
		// translates current state into int array
		stateArray = new int[2];//disini cuman dua, obstacle, dan jaraknya
		int deltaX =0;
		int deltaY=0;
		int posObsX = mx;
		int posObsY = my;
		int obs = 4;
		switch(mo){
			case 0 : deltaX = 0;deltaY=-1;break;
			case 1 : deltaX=1;deltaY=-1;break;
			case 2 : deltaX=1;deltaY=0;break;
			case 3 : deltaX=1;deltaY=1;break;
			case 4 : deltaX=0;deltaY=1;break;
			case 5 : deltaX=-1;deltaY=1;break;
			case 6 : deltaX=-1;deltaY=0;break;
			case 7 : deltaX=-1;deltaY=-1;break;
			default : System.err.println("Invalid orientation : "+mo);break;
		}
		posObsX+=deltaX;
		posObsY+=deltaY;
		boolean adakucingataukeju = false;
		int jarak = 0;
		for (int i =0;i<batas_penglihatan;i++){	
			//System.out.println(mo+" "+mx+" "+my+" "+posObsX+" "+posObsY);
			if (posObsX<0 || posObsX>=bx ||posObsY<0 || posObsY>=by){
				obs =0;
				break;
			}
			else{
				
				obs=3;
				if (walls[posObsX][posObsY]){
					obs = 0;break;
				}				
					
				for (int z=0;z<jumlah_kucing;z++){
					if(posObsX==cx[z] && posObsY==cy[z]){
						obs =1;
						adakucingataukeju =true;
						break;
						}
				}
				for (int z=0;z<jumlah_keju;z++){
					if (posObsX==chx[z] && posObsY==chy[z]){
						obs=2;
						adakucingataukeju =true;
						break;
						}
				}
				if (adakucingataukeju)
					break;
			}
			jarak++;
			posObsX+=deltaX;
			posObsY+=deltaY;
		}
		stateArray[0] = obs;
		stateArray[1] = jarak;
		if (obs==3)
			stateArray[1] = batas_penglihatan-1;
				//for (int i =0;i<batas_penglihatan;i++){	
					//System.out.print(stateArray[i]+",");
				//}
				//System.out.println();
		return stateArray;
	}

	public double calcReward() {
		double newReward = 0;
		boolean kejukucing = false;
		for (int z=0;z<jumlah_keju;z++){
			if ((mx==chx[z])&&(my==chy[z])) {
				mousescore+=cheeseReward;
				newReward += cheeseReward;
				kejukucing=true;
			}
		}
		for (int z=0;z<jumlah_kucing;z++){
			if ((cx[z]==mx) && (cy[z]==my)) {
				catscore++;
				mousescore-=deathPenalty;
				newReward -= deathPenalty;
				kejukucing=true;
			}
		}
		
		if (!kejukucing){
			mousescore-=melangkahPenalty;
			//
			//decreament rewardnya juga ato ga kalo ngelangkah?
			//
			newReward-=melangkahPenalty;
		}
		//if ((mx==hx)&&(my==hy)&&(gotCheese)) newReward += 100;
		return newReward;		
	}
	
	public void setPos() {
//		System.out.println("markBefore : "+setPos.size());
		
		// Bikin boolean berukuran besar map
		boolean[][] terisi = new boolean[bx][by];
//		
//		// Inisiasi dengan false kecuali yang sudah terisi tembok
		for (int i=0; i<bx; i++) {
			for (int j=0; j<by; j++) {
				terisi[i][j] = walls[i][j];
			}
		}
//		
	//	Random randomizer = new Random();
//		mx = randomizer.nextInt(bx);
//		my = randomizer.nextInt(by);
//		while (terisi[mx][my])
//		{
//			mx = randomizer.nextInt(bx);
//			my = randomizer.nextInt(by);
//		}
//		terisi[mx][my] = true;
		int x, y;
		
		if (mark >= setPos.size()) 
			mark = 0;
			
		int tempe = mark;
		x = setPos.get(mark)-1;
		mark++;
		y = setPos.get(mark)-1;
		mark++;
		if (mark >= setPos.size()) 
			mark = 0;
		while (x>=bx || y>=by || terisi[x][y])
		{
			x = setPos.get(mark)-1;
			mark++;
			y = setPos.get(mark)-1;
			mark++;
			if (mark >= setPos.size()) 
				mark = 0;
			if (tempe==mark){
				break;
			}
				
		}
		while (x>=bx || y>=by || terisi[x][y]){
			x=(int)(Math.random() * bx);
			y=(int)(Math.random() * by);
		}
		mx = x;
		my = y;
		terisi[mx][my] = true;
		
			
		for (int z = 0;z<jumlah_kucing;z++){
		
			tempe = mark;
			x = setPos.get(mark)-1;
			mark++;
			y = setPos.get(mark)-1;
			mark++;
			if (mark >= setPos.size()) 
				mark = 0;
//			cx[z] = randomizer.nextInt(bx);
//			cy[z] = randomizer.nextInt(by);
		
			while ( x>=bx || y>=by || terisi[x][y])
			{
				x = setPos.get(mark)-1;
				mark++;
				y = setPos.get(mark)-1;
				mark++;
				if (mark >= setPos.size()) 
					mark = 0;
				if (tempe==mark){
					break;
				}
			}
			while (x>=bx || y>=by || terisi[x][y]){
				x=(int)(Math.random() * bx);
				y=(int)(Math.random() * by);
			}
			cx[z] = x;
			cy[z] = y;
			terisi[cx[z]][cy[z]] = true;
		}
		for (int z = 0;z<jumlah_keju;z++){	
			tempe = mark;		
			x = setPos.get(mark)-1;
			mark++;
			y = setPos.get(mark)-1;
			mark++;
			if (mark >= setPos.size()) 
				mark = 0;
//			chx[z] = randomizer.nextInt(bx);
//			chy[z] = randomizer.nextInt(by);
			while (x>=bx || y>=by || terisi[x][y])
			{
				x = setPos.get(mark)-1;
				mark++;
				y = setPos.get(mark)-1;
				mark++;
				if (mark >= setPos.size()) 
					mark = 0;
					
				if (tempe==mark){
					break;
				}
			}
			while (x>=bx || y>=by || terisi[x][y]){
				x=(int)(Math.random() * bx);
				y=(int)(Math.random() * by);
			}
			chx[z] = x;
			chy[z] = y;
			terisi[chx[z]][chy[z]] = true;
		}
	//	System.out.println("markAfter : "+mark);
		
	}

	boolean legal(int x, int y) {
		return ((x>=0) && (x<bx) && (y>=0) && (y<by)) && (!walls[x][y]);
	}

	boolean endGame() {
		
		//return (((cx[0]==mx) && (cy[0]==my))||((chx[0]==-1) && (chy[0]==-1)));
		return tikusDimakanKucing() || semuaKejuDimakanTikus();
		// harusnya return true kalau tikus kena salah satu kucing (mx==cx[i]) && my==cy[i]), atau semua keju habis, semua chx[i] & chy[i] nilainya -1
	}
	
	boolean tikusDimakanKucing() {
		for (int i=0; i<jumlah_kucing; i++)
			if (mx == cx[i] && my == cy[i])
				return true;
		return false;
	}
	
	boolean semuaKejuDimakanTikus() {
		for (int i=0; i<jumlah_keju; i++)
			if (chx[i] != -1 || chy[i] != -1)
				return false;
		return true;
	}

	Dimension getRandomPos() {
		int nx, ny;
		nx = (int)(Math.random() * bx);
		ny = (int)(Math.random() * by);
		for(int trials=0; (!legal(nx,ny)) && (trials < WALL_TRIALS); trials++){
			nx = (int)(Math.random() * bx);
			ny = (int)(Math.random() * by);
		}
		return new Dimension(nx, ny);
	}
	
	/******** heuristic functions ***********/
/*	Dimension getNewPos(int x, int y, int tx, int ty) {// buat greedy
		int[] stateTemp = new int[batas_penglihatan];

		stateTemp=getState();
		if (stateTemp[0]==0 || stateTemp[0]==1); //kalo depannya 
		
		int ax=x, ay=y;		
		if (tx==x) ax = x;
 		else ax += (tx - x)/Math.abs(tx-x); // +/- 1 or 0
		if (ty==y) ay = y;
 		else ay += (ty - y)/Math.abs(ty-y); // +/- 1 or 0
		
		// check if move legal	
		if (legal(ax, ay)) return new Dimension(ax, ay);
		
		// not legal, make random move
		while(true) {
			// will definitely exit if 0,0
			ax=x; ay=y;
			ax += 1-(int) (Math.random()*3);
			ay += 1-(int) (Math.random()*3);
			
			//System.out.println("old:"+x+","+y+" try:"+ax+","+ay);
			if (legal(ax,ay)) return new Dimension(ax,ay);
		}
	}

	void moveCat() { //belum
		Dimension newPos = getNewPos(cx[0], cy[0], mx, my);
		cx[0] = newPos.width;
		cy[0]= newPos.height;	
		//System.out.println("kucing : "+ cx +","+cy+" tikus : "+mx+","+my);
	}

	void moveMouse() {
		Dimension newPos = getNewPos(mx, my, chx[0], chy[0]);
		mx = newPos.width;
		my = newPos.height;
	}*/
	
	
	int mouseAction() { // buat greedy
		int act;
		int i;
		boolean adaKeju=false;
		int[] stateTemp = new int[2];//cuman 2, obstacle sama jaraknya
		stateTemp = getState();
		
		if (stateTemp[0]==2){
			adaKeju=true;
		}	
	
		if(adaKeju){
			act=0;
		}else{
			if (muter>=8){
				if (stateTemp[1]!=0){
					act = 0;
					muter=0;
				}
				else {
					act = (int)(Math.random() * 3);
					while (act==0 ){
						act = (int)(Math.random() * 3);
					}
				}
			}else{
				act=2; // muter ke kanan terus
				muter++;
			}
		}
		
		
		return act;
	}
	/******** end heuristic functions ***********/


	/******** wall generating functions **********/
	void makeWalls(int xdim, int ydim, int numWalls) {
		walls = new boolean[xdim][ydim];
		
		// loop until a valid wall set is found
		for(int t=0; t<WALL_TRIALS; t++) {
			// clear walls
			for (int i=0; i<walls.length; i++) {
				for (int j=0; j<walls[0].length; j++) walls[i][j] = false;
			}
			
			float xmid = xdim/(float)2;
			float ymid = ydim/(float)2;
			
			// randomly assign walls.  
			for (int i=0; i<numWalls; i++) {
				Dimension d = getRandomPos();
				
				// encourage walls to be in center
				double dx2 = Math.pow(xmid - d.width,2);
				double dy2 = Math.pow(ymid - d.height,2);
				double dropperc = Math.sqrt((dx2+dy2) / (xmid*xmid + ymid*ymid));
				if (Math.random() < dropperc) {
					// reject this wall
					i--;
					continue;
				}
				
				//System.out.println("w & h = " + d.width + " " + d.height);
				walls[d.width][d.height] = true;
			}
			
			// check no trapped points
			if (validWallSet(walls)) break;
			
		}
		
	}
	
	boolean validWallSet(boolean[][] w) {
		// copy array
		boolean[][] c;
		c = new boolean[w.length][w[0].length];
		
		for (int i=0; i<w.length; i++) {
			for (int j=0; j<w[0].length; j++) c[i][j] = w[i][j];
		}
		
		// fill all 8-connected neighbours of the first empty
		// square.
		boolean found = false;
		search: for (int i=0; i<c.length; i++) {
			for (int j=0; j<c[0].length; j++) {
				if (!c[i][j]) {
					// found empty square, fill neighbours
					fillNeighbours(c, i, j);
					found = true;
					break search;
				}
			}
		}
		
		if (!found) return false;
		
		// check if any empty squares remain
		for (int i=0; i<c.length; i++) {
			for (int j=0; j<c[0].length; j++) if (!c[i][j]) return false;
		}
		return true;
	}
	
	void fillNeighbours(boolean[][] c, int x, int y) {
		c[x][y] = true;
		for (int i=x-1; i<=x+1; i++) {
			for (int j=y-1; j<=y+1; j++)
				if ((i>=0) && (i<c.length) && (j>=0) && (j<c[0].length) && (!c[i][j])) 
					fillNeighbours(c,i,j);
		}
	}
	/******** wall generating functions **********/

}
