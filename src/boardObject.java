import java.awt.*;
import java.awt.image.*;
import java.awt.geom.AffineTransform;

public class boardObject {
	static final int O_IMG = 0, O_COL = 1;
	Color objColour;
	Image objImage;
	int style;
	int xcoord, ycoord;
	
	public boardObject(Color c) {
		this.objColour = c;
		this.style = O_COL;
	}

	public boardObject(Image i) {
		this.objImage = i;
		this.style = O_IMG;
	}
	
	public void setImage(Image i){
		this.objImage = i;
		this.style = O_IMG;
	}
	public void setCoords(Dimension d) { xcoord = d.width; ycoord = d.height; }
	public void setCoords(int x, int y) { xcoord = x; ycoord = y; }

	public void drawObject(Graphics g, int w, int h, ImageObserver i) { drawObject(g,xcoord,ycoord,w,h,i); }
	public void drawObject(Graphics g, int w, int h) { drawObject(g,xcoord,ycoord,w,h,null); }
	public void drawObject(Graphics g, int x, int y, int w, int h) { drawObject(g,x,y,w,h,null); }
	public void drawObject(Graphics g, int x, int y, int w, int h, ImageObserver i) {
		if (style == O_IMG) {
			// paint image
			
		//	Graphics2D g2d = (Graphics2D)g;
			//g2d.rotate(Math.toRadians(degrees));
			
		//Graphics2D g2d=(Graphics2D)g; // Create a Java2D version of g.
       //  g2d.translate(w, h); // Translate the center of our coordinates.
       // g2d.rotate(0.1,w/2,h/2);  // Rotate the image by 1 radian.
		 g.drawImage(objImage, x*w, y*h, w, h, i);
		// g2d.translate(-x, -y);
		//  g2d.rotate(-0.1,w/2,h/2);
		  
		  
		  
	
		 
		} else {
			// paint square
			g.setColor(objColour);
			g.fillRect(x*w,y*h,w,h);
		}
	}
	
	public String toString() {return objColour.toString();}
}
