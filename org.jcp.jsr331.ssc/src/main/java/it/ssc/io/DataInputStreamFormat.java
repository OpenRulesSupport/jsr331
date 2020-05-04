package it.ssc.io;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

public class DataInputStreamFormat extends DataInputStream {
	private StringBuffer sbuffer;
	private int char_letti;
	private char char_letto;
	
	public DataInputStreamFormat(InputStream arg0) throws IOException {
		super(arg0);
		// TODO Auto-generated constructor stub
	}
	
	
	public String readChars(int size)   throws IOException {
		
		sbuffer=new StringBuffer(size);
		char_letti=0;
		
		while(char_letti < size)  {
			char_letto=readChar();
			char_letti++;
			if(char_letto!='\u0000') sbuffer.append(char_letto);
		}
		
		return sbuffer.toString();
	}
}
