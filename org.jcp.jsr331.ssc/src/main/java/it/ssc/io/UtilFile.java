package it.ssc.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

public class UtilFile {
	
	public static void copyFile(File sourceFile, File destFile) throws IOException { 
	    if(!destFile.exists()) { 
	        destFile.createNewFile(); 
	    } 
	 
	    FileChannel source = null; 
	    FileChannel destination = null; 
	    try { 
	        source = new FileInputStream(sourceFile).getChannel(); 
	        destination = new FileOutputStream(destFile).getChannel(); 
	 
	        // previous code: destination.transferFrom(source, 0, source.size()); 
	        // to avoid infinite loops, should be: 
	        long count = 0; 
	        long size = source.size();               
	        while((count += destination.transferFrom(source, count, size-count))<size); 
	    } 
	    finally { 
	        if(source != null) { 
	            source.close(); 
	        } 
	        if(destination != null) { 
	            destination.close(); 
	        } 
	    } 
	} 
	
	public static String getPathDirWithSeparatorFinal(String path) throws DirectoryNotFound {
		
		String new_path =null;
		File file=new File(path);
		path=file.getAbsolutePath();
		if(file.isDirectory()) { 
			int size = path.length();
			new_path = path;
			char last_char = path.charAt(size - 1);
			if (!(last_char == File.separatorChar)) {
				new_path = new_path + File.separator;
			}
		}
		else {
			throw new DirectoryNotFound("ERRORE. Directory non trovata");
		}
		return new_path;
	}
	
	public static boolean existDirectory(String path_directory) {
		File file=new File(path_directory);
		return file.isDirectory();
	}
}
