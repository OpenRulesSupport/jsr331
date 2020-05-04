package it.ssc.metadata;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;


public class ReadMetaDataFMT {
	
	
	public static MetaDataDatasetFMTInterface readAndClose(File metafile) throws FileNotFoundException, IOException, ClassNotFoundException {
		ObjectInputStream ois=new ObjectInputStream(new FileInputStream(metafile));
		MetaDataDatasetFMTInterface meta=(MetaDataDatasetFMTInterface)ois.readObject();
		ois.close();
		return meta;
	}

}
