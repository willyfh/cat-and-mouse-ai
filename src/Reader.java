import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author FAJAR
 */
public class Reader {
	

    /**
     * @param args the command line arguments
     */
	 
	     
	boolean[][] fileMap;
	public ArrayList<Integer> set_posisi_play;
	public ArrayList<Integer> set_posisi_train;
	
	
	public int batas_penglihatan;
	public int jumlah_kucing;
	public int jumlah_keju;
	

		
	public Reader(){

	}
   
    public void ReadDataKoordinat() {
        
        try {
            
        Scanner fileIn;
        fileIn = new Scanner(new File("Koordinat.txt"));
        String temp;
        String delims = "[(\\,\\)]+";
        int batas, nkucing, nkeju;
        //int xtikusplay, ytikusplay, xtikustrain, ytikustrain;
		
        set_posisi_play = new ArrayList<>();
        set_posisi_train = new ArrayList<>();
        ArrayList<Integer> arraytemp = new ArrayList<>();
       
		
        
        
        batas = Integer.parseInt(fileIn.next());
		batas_penglihatan = batas;
        jumlah_keju = Integer.parseInt(fileIn.next());
        jumlah_kucing = Integer.parseInt(fileIn.next());
		temp = fileIn.nextLine();
		while(fileIn.hasNext()) {
		temp = fileIn.nextLine();
        String[] temp2 = temp.split(delims);
		for (int i = 1; i < temp2.length; i+=3) {
		set_posisi_play.add(Integer.parseInt(temp2[i]));
		set_posisi_play.add(Integer.parseInt(temp2[i+1]));
		}
		
		temp = fileIn.nextLine();
		temp2 = temp.split(delims);
		for (int i = 1; i < temp2.length; i+=3) {
		set_posisi_train.add(Integer.parseInt(temp2[i])); 
		set_posisi_train.add(Integer.parseInt(temp2[i+1])); 
		}
		}
		//System.out.print(set_posisi_train.get(5));
		
        }
        
         catch (FileNotFoundException ex) {
          //  Logger.getLogger(CatMouse.class.getName()).log(Level.SEVERE, null, ex);
        } 
        }
        
        

    
    public void ReadDataMap() {
        
        try {
        int k;    
        Scanner fileIn;
        fileIn = new Scanner(new File("Map.txt"));
        //mapnya masih satu dimensi, tar kalo mau dijadiin 2 dimensi gampang sih
        ArrayList<Integer> map = new ArrayList<>();
		int i =0;
		int j =0;
		String temp;
		String delims = "[ ]+";
		String[] temp2;

		ArrayList<Boolean> coba = new ArrayList<Boolean>();;
		ArrayList<ArrayList<Boolean>> mapcoba = new ArrayList<ArrayList<Boolean>>();



        while(fileIn.hasNext()) {
        	temp = fileIn.nextLine();
		temp2 = temp.split(delims);
				coba = new ArrayList<Boolean>();
		i=0;
		for (i = 0;i<temp2.length;i++){
			if (temp2[i].equals("1")){
				coba.add(true);
			}else{
				coba.add(false);
			}
		}
		mapcoba.add(coba);

		
		//map.add(Integer.parseInt(fileIn.next()));
		//System.out.println(map.get(i));	
		
        }
	fileMap = new boolean[mapcoba.size()][coba.size()];
	for (i = 0 ; i<mapcoba.size();i++){
		for(j=0;j<coba.size();j++){
			fileMap[i][j]=mapcoba.get(i).get(j);
			
		}
		//System.out.println();
	}	
		
        
        } catch (FileNotFoundException ex) {
          //  Logger.getLogger(CatMouse.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
        }

	

	public boolean[][] getFileMap(){
		return fileMap;
	}
		
		/*public static void main(String [] args){
			Reader rd = new Reader();
			boolean[][] tes;
			rd.ReadDataMap();
			tes=rd.getFileMap();
			

			
		}*//*
		public static void main(String[] args) {
		  Reader2 rd = new Reader2();
		  rd.ReadDataKoordinat();
		}*/
}


