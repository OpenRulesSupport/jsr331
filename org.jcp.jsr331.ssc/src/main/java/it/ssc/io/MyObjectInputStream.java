package it.ssc.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;

public class MyObjectInputStream extends ObjectInputStream {
	private Class my_class;
	
	 public Class resolveClass(ObjectStreamClass desc) throws IOException,  ClassNotFoundException {
		return my_class;
	 }

	 public MyObjectInputStream(InputStream in, Class my_class) throws IOException {
		 super(in);
		 this.my_class=my_class;
	 }
}

