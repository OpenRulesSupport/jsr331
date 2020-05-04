package it.ssc.ref;

import java.io.File;
import it.ssc.io.DirectoryNotFound;

public class OutputRefFile implements OutputRefInterface {
	
	private static final TYPE_REF type_ref = TYPE_REF.REF_FILE;
	private String path_file;
	
	public OutputRefFile(String path_file) {
		this.path_file=path_file;
	}
	
	public TYPE_REF getTypeRef() {
		return type_ref;
	}
	
	 public File getFile() throws DirectoryNotFound {
		 return new File(path_file);
	 }

}
