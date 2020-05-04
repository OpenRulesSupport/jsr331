package it.ssc.step.writedata;

import it.ssc.pdv.PDVKeep;

public class ManagerMissingValues {
	
	public static byte[] createByteArray(int num_var_keep)  {
		
		//int num_var_keep=pdv.getSizeFieldKeep();
		int size_array_bytes;
		if(num_var_keep <=8) size_array_bytes=1;
		else size_array_bytes=  ((int)( (num_var_keep-1) /8)+1);
		return new byte[size_array_bytes];
		
	}
	
	public static void resetArray(byte[] arrray_missing_values) {
		
		for(int a=0;a< arrray_missing_values.length;a++) {
			arrray_missing_values[a]=0x00;
		}
	}
	
	public static boolean isMissingFromArray(byte[] arrray_missing_values, int index_var) {
		
		int single_byte;
		int index_to_point=0;
		if(index_var <=8) single_byte=arrray_missing_values[index_to_point];
		else {
			index_to_point= (int)((index_var-1)/8);
			single_byte=arrray_missing_values[index_to_point];
		}
		int resto= index_var %8;
		if(resto==0) {
			if( (single_byte & - 128)== -128 ) return true;
		}
		else {
			if( (single_byte & (int)Math.pow(2, resto-1)) ==(int)Math.pow(2, resto-1) ) return true;
		}
		return false;
	}
	
	
	public static void  setMissingToArray(byte[] arrray_missing_values, int index_var) {
		
		int single_byte;
		int index_to_point=0;
		if(index_var <=8) single_byte=arrray_missing_values[index_to_point];
		else {
			index_to_point= (int)((index_var-1)/8);
			single_byte=arrray_missing_values[index_to_point];
		}
		int resto= index_var %8;
		if(resto==0) {
			single_byte=single_byte | - 128;
		}
		else {
			single_byte=single_byte | (int)Math.pow(2, resto-1);
		}
		arrray_missing_values[index_to_point]=(byte)single_byte;
	}
	
}
