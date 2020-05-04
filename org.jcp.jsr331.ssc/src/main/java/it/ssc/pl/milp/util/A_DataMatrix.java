package it.ssc.pl.milp.util;



import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Random;
import it.ssc.io.FileNotFound;
import it.ssc.io.UtilFile;

public class A_DataMatrix {
	
	/*
	 * Questa matrice deve memorizzare la tabella standard. 
	 * Il nome da dare alla matrice non deve esistere e sara dato da 
	 * A_numero_casuale. La creazione avverra nel costruttore , per cui per 
	 * accedere alla matrice occorre sempre avere il riferimento dell'oggetto. 
	 * La matrice verra creata nel path, che per adesso e definita come l'area di 
	 * work della sessione.  
	 */
	
	private int nRow;
	private int nCol;
	private File file;
	private double[] tempRowI;
	private DataInputStream input;
	public int getnRow() {
		return nRow;
	}

	public int getnCol() {
		return nCol;
	}


	public A_DataMatrix(double[][] A, String path) throws FileNotFoundException, IOException {
		this.nRow=A.length;
		this.nCol=A[0].length;
		tempRowI = new double[nCol];
		file=createRandomNameFile(path);
		DataOutputStream out=getDataOutputStream(file);
		writeDataFromArray(A,out);
		out.close();
	}
	
	private void writeDataFromArray(double[][] A,DataOutputStream out) throws IOException {
		for(int i=0;i<this.nRow;i++) 
			for(int j=0;j<this.nCol;j++)  
				out.writeDouble(A[i][j]);
		
	}
	
	public static void main(String arg[]) throws FileNotFoundException, IOException  {
		
		double matrix[][]={ {1,2,3,4},{5,6,7,8},{9,10,11,12}};
		A_DataMatrix A=new A_DataMatrix(matrix,"C:\\appo\\cd_rom");
		
		System.out.println(A.readArray(0)[0]);  //1
		System.out.println(A.readArray(1)[0]);  //5
		System.out.println(A.readArray(2)[3]);  //12	
		
		double matrix2[][]=A.getMatrix(); 
		for(int i=0;i<matrix2.length;i++) {
			System.out.println("\n:");
			for(int j=0;j<matrix2[0].length;j++)  
				System.out.println("VALORE:"+matrix2[i][j]);
		}
		
		A.close();
	}
	
	
	private DataOutputStream getDataOutputStream(File file) throws FileNotFoundException {
		BufferedOutputStream buff = new BufferedOutputStream(new FileOutputStream(file));
		DataOutputStream file_out_data = new DataOutputStream(buff);
		return file_out_data;
	}
	
	private DataInputStream getDataInputStream(File file) throws FileNotFoundException {
		BufferedInputStream buff = new BufferedInputStream(new FileInputStream(file));
		DataInputStream file_out_data = new DataInputStream(buff);
		return file_out_data;
	}
	

	private static synchronized File createRandomNameFile(String path_work_root) throws IOException {
	    File file;
		do {
			Random ra = new Random(new Date().getTime());
			String path_work=UtilFile.getPathDirWithSeparatorFinal(path_work_root)+"A_" + Math.abs(ra.nextInt());
			file=new File(path_work);
		}	
		while(file.exists());
		if(!file.createNewFile()) throw new FileNotFound("ERRORE ! Impossibile creare "+file.getAbsolutePath());
	
		return file;
	}
	
	
	
	public double[][] getMatrix() throws IOException {
		double[][] matrice=new double[this.nRow][this.nCol] ;
		input=getDataInputStream(file);
		
		for(int i=0;i<this.nRow;i++) 
			for(int j=0;j<this.nCol;j++)  
				matrice[i][j]=input.readDouble();
		
		input.close();
		return matrice;
	}
	
	public double[] readArray(int row) throws IOException {
		if(row >= nRow) throw new java.lang.ArrayIndexOutOfBoundsException("Riga della matrice fuori indice");
		if(row==0) input=getDataInputStream(file);
		
		for (int i = 0; i < tempRowI.length; i++) {
			tempRowI[i] = input.readDouble();
		}
		if(row==(nRow-1)) input.close();
		return tempRowI;
	}
	
	public void close() throws IOException {
		if(input!=null) input.close();
		this.tempRowI=null;
	}
	
}
