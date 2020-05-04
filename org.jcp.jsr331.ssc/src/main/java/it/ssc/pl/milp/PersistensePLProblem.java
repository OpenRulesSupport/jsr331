package it.ssc.pl.milp;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Date;
import java.util.Random;
import it.ssc.io.FileNotFound;
import it.ssc.io.UtilFile;


 class PersistensePLProblem {
	private File file;
	
	public PersistensePLProblem(PLProblem pl , String path) throws IOException {
		file=createRandomNameFile(path);
		ObjectOutputStream out=getObjectOutputStream(file);
		out.writeObject(pl);
		out.close();
	}
	
	public PLProblem readObject() throws IOException, ClassNotFoundException {
		
		ObjectInputStream in=getObjectInputStream(file);
		PLProblem pl=(PLProblem)in.readObject();
		in.close();
		return pl;
	}
	
	
	private static synchronized File createRandomNameFile(String path_work_root) throws IOException {
	    File file;
		do {
			Random ra = new Random(new Date().getTime());
			String path_work=UtilFile.getPathDirWithSeparatorFinal(path_work_root)+"PL_" + Math.abs(ra.nextInt());
			file=new File(path_work);
		}	
		while(file.exists());
		if(!file.createNewFile()) throw new FileNotFound("ERRORE ! Impossibile creare "+file.getAbsolutePath());
	
		return file;
	}
	
	private ObjectOutputStream getObjectOutputStream(File file) throws IOException {
		BufferedOutputStream buff = new BufferedOutputStream(new FileOutputStream(file));
		ObjectOutputStream file_out_data = new ObjectOutputStream(buff);
		return file_out_data;
	}
	
	private ObjectInputStream getObjectInputStream(File file) throws IOException {
		BufferedInputStream buff = new BufferedInputStream(new FileInputStream(file));
		ObjectInputStream file_out_data = new ObjectInputStream(buff);
		return file_out_data;
	}
	

}
