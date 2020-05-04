package it.ssc.io;

import java.nio.MappedByteBuffer;

public class ReadCharsFromMapped  {
	
	
	public static StringBuffer getStringBuffer(MappedByteBuffer map,int num_char)  {
		StringBuffer stb=new StringBuffer(num_char);
		char char_letto;
		for(int a=0;a<num_char;a++) {
			char_letto=map.getChar();
			if(char_letto!='\u0000') stb.append(char_letto);
		}
		return stb;
	}
}
