package it.ssc.io;

import it.ssc.pdv.PDVField;
import it.ssc.util.SSCArrays;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class DataOutputStreamFormat extends DataOutputStream {

	public DataOutputStreamFormat(OutputStream arg0) throws IOException {
		super(arg0);
	}
	/**/
	
	public void writeUTFfromField(PDVField<String> pdv_field)   throws IOException{
		if(pdv_field.lentgh_field - pdv_field.value_generics.length() < 0 ) {
			writeUTF(pdv_field.value_generics.substring(0, pdv_field.lentgh_field));
		}
		else {
			writeUTF(pdv_field.value_generics);
		}
	}
	
	public void writeCharsFromField(PDVField<StringBuffer> pdv_field)   throws IOException {
		if(pdv_field.lentgh_field - pdv_field.value_generics.length() > 0 ) {
			
			StringBuffer value=new StringBuffer(pdv_field.value_generics);
			//pdv_field.value_generics.append(FMTArrays.fill(	new char[pdv_field.lentgh_field - pdv_field.value_generics.length()], '\u0020'));
			value.append(SSCArrays.fill(new char[pdv_field.lentgh_field - pdv_field.value_generics.length()], '\u0000'));
			writeChars(value.toString());
		}
		else if(pdv_field.lentgh_field - pdv_field.value_generics.length() < 0 ) {
			writeChars(pdv_field.value_generics.toString().substring(0,pdv_field.lentgh_field));
		}
		else {
			writeChars(pdv_field.value_generics.toString()); 
		}
	}
}
