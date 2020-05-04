package it.ssc.pl.milp.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Date;
import java.util.Random;


import it.ssc.io.FileNotFound;
import it.ssc.io.UtilFile;

public final class A_Matrix {
	
	/*
	 * Questa matrice deve memorizzare la tabella normalizzata. 
	 * Il nome da dare alla matrice non deve esistere e sara dato da 
	 * A_numero_casuale. LA creazione avverra nel costruttore , per cui per 
	 * accedere alla matrice occorre sempre avere il riferimento dell'oggetto. 
	 * La matrice verra creata nel path, che per adesso e definita come l'area di 
	 * work della sessione.  
	 */
	
	private int nRow;
	private int nCol;
	private double[] tempRowI;
	private RandomAccessFile RAF;
	private int currentRow=-1;
	
	public int getnRow() {
		return nRow;
	}


	public int getnCol() {
		return nCol;
	}


	public A_Matrix(int row, int col, String path) throws FileNotFoundException, IOException {
		this.nCol=col;
		this.nRow=row;
		this.tempRowI = new double[nCol];
		RAF=getRandomAccesFile(createRandomNameFile(path));
	}
	
	public A_Matrix(double[][] A, String path) throws FileNotFoundException, IOException {
		this.nRow=A.length;
		this.nCol=A[0].length;
		this.tempRowI = new double[nCol];
		RAF=getRandomAccesFile(createRandomNameFile(path));
		writeDataFromArray(A);
	}
	
	private void writeDataFromArray(double[][] A) throws IOException {
		for(int i=0;i<this.nRow;i++) 
			for(int j=0;j<this.nCol;j++)  
				RAF.writeDouble(A[i][j]);
		
	}
	
	public static void main(String arg[]) throws FileNotFoundException, IOException  {
		
		double matrix[][]={ {1,2,3,4},{5,6,7,8},{9,10,11,12}};
		A_Matrix A=new A_Matrix(3,4,"C:\\appo\\cd_rom");
		for(int i=0;i<matrix.length;i++) {
			for(int j=0;j<matrix[0].length;j++) {
				A.writeDouble(matrix[i][j]);
			}	
		}
		
		System.out.println(A.readArray(0)[0]);  //1
		System.out.println(A.readArray(1)[0]);  //5
		System.out.println(A.readArray(1)[1]);  //6
		System.out.println(A.readArray(2)[3]);  //12
		System.out.println(A.readArray(1)[1]);  //6
		System.out.println(A.readArray(2)[1]);  //6
		
		System.out.println(A.readValue(2,1));  //6
		
		A.close();
	}
	
	
	private RandomAccessFile getRandomAccesFile(File file) throws FileNotFoundException {
		return new RandomAccessFile(file, "rw");
	}
	
	public void writeDouble(double value) throws IOException {
		RAF.writeDouble(value);
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
	
	
	
	public double[] readArray(int row) throws IOException {
		if(row >= nRow) throw new java.lang.ArrayIndexOutOfBoundsException("Riga della matrice fuori indice");
		if(currentRow==row) return tempRowI;
		else currentRow=row;
		
		long start=(row *nCol) *8;
		RAF.seek(start);
		for (int i = 0; i < tempRowI.length; i++) {
			tempRowI[i] = RAF.readDouble();
		}
		return tempRowI;
	}
	
	
	public double readValue(int row,int col) throws IOException {
		if(row >= nRow) throw new java.lang.ArrayIndexOutOfBoundsException("Riga della matrice fuori indice");
		
		
		long start=(row *nCol) *8 + col*8;
		RAF.seek(start);
		
		return RAF.readDouble();
		
	}
	
	public void close() throws IOException {
		RAF.close();
	}
}
