public class halangan {
	public int x;
	public int y;
	public int type;
	//0 = tembok
	//1 = kucing
	//2 = keju
	//3 = kosong
	public halangan(int x_,int y_){
		x=x_;
		y=y_;
	}
	public int getX(){
		return x;
	}
	public int getY(){
		return y;
	}
	public int getType(){
		return type;
	}
	public void setX(int x_){
		x=x_;
	}
	public void setY(int y_){
		y=y_;
	}
	public void setType(int x_){
		type=x_;
	}
	
}
