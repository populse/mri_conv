package brukerParavision;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class ListPositionFrame {

	private String file;
	private int orientation=1;
	private double[][] listP,listO;
	private int inc=0;
	
	public ListPositionFrame (String file,int orientation) {
		this.file=file;
		this.orientation=orientation;
	}
	
	public double[][] listofPosition () throws IOException {
		
		double[][] f = null ;
		double[] tmp = null;
		
		BufferedReader lecteurAvecBuffer = new BufferedReader(new FileReader(file));
		String ligne;
		int i=0,j=0;
		
		while ((ligne = lecteurAvecBuffer.readLine()) != null) {
       	 	
			if ( ligne.indexOf("##$VisuCorePosition=") != -1){
       	 	inc=Integer.parseInt(ligne.substring(ligne.indexOf("(")+2, ligne.indexOf(",")));
       		f = new double[3][inc];
       		tmp = new double[3*inc];
       		ligne=lecteurAvecBuffer.readLine();
       		
       			while (j<3*inc) {
       			     				
       				for (i=0;i<(ligne.split("\n"))[0].split(" ").length;i++) {
       					tmp[j+i]=Double.parseDouble((ligne.split("\n"))[0].split(" ")[i]);
       				}
       				j=j+i;
       				ligne=lecteurAvecBuffer.readLine();
       			}
       	 	}
   	 	}  
   	 	
		lecteurAvecBuffer.close();
   	 	
   	 	for (int l=0;l<inc;l++) {
   	 		f[0][l]=tmp[3*l];
   	 		f[1][l]=tmp[3*l+1];
   	 		f[2][l]=tmp[3*l+2];
//   	 		System.out.println("Position : (x,y,z) = "+f[0][l]+" : "+f[1][l]+" : "+f[2][l]);
   	 	}
  	 	return f;
}
	
	public double[][] listofOrientation () throws IOException {
		
		double[][] f = null ;
		double[] tmp = null;
		
		BufferedReader lecteurAvecBuffer = new BufferedReader(new FileReader(file));
		String ligne;
		int i=0,j=0;
		
		while ((ligne = lecteurAvecBuffer.readLine()) != null) {
       	 	
			if ( ligne.indexOf("##$VisuCoreOrientation=") != -1){
       	 	inc=Integer.parseInt(ligne.substring(ligne.indexOf("(")+2, ligne.indexOf(",")));
       		f = new double[3][inc*3];
       		tmp = new double[9*inc];
       		ligne=lecteurAvecBuffer.readLine();
       		
       			while (j<9*inc) {
   
       				for (i=0;i<(ligne.split("\n"))[0].split(" ").length;i++) {
       					tmp[j+i]=Double.parseDouble((ligne.split("\n"))[0].split(" ")[i]);
       				}
       				j=j+i;
       				ligne=lecteurAvecBuffer.readLine();
       			}
       	 	}
   	 	}  
   	 	
		lecteurAvecBuffer.close();
   	 	
   	 	for (int l=0;l<3*inc;l++) {
   	 		f[0][l]=tmp[3*l];
   	 		f[1][l]=tmp[3*l+1];
   	 		f[2][l]=tmp[3*l+2];
   	 	}
   	 	return f;
	}
	
	public String[][] listofPositionFrame () throws IOException {
		
		listP = listofPosition();
		listO = listofOrientation();
		String[][] posFr = new String [3][listP[0].length];
					
		for (int i=0;i<listP[0].length;i++){
			
			posFr[0][i]=	String.valueOf((listP[0][i]* listO[0][3*i]+
											listP[1][i]* listO[1][3*i]+
											listP[2][i]* listO[2][3*i])*orientation);
			posFr[1][i]=	String.valueOf((listP[0][i]* listO[0][3*i+1]+
											listP[1][i]* listO[1][3*i+1]+
											listP[2][i]* listO[2][3*i+1])*orientation);
			posFr[2][i]=	String.valueOf((listP[0][i]* listO[0][3*i+2]+
											listP[1][i]* listO[1][3*i+2]+
											listP[2][i]* listO[2][3*i+2])*orientation);
		}
		return posFr;
	}

	public int InstanceNumber() {
		return inc;
	}
}