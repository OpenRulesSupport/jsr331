package it.ssc.ref;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import it.ssc.metadata.FieldInterface;
import it.ssc.metadata.MetaDataDatasetFMTInterface;
import it.ssc.metadata.NameMetaParameters;
import it.ssc.step.exception.InvalidDichiarationOptions;

public class InputRefFmtMemory  implements Input {
	
	private static final TYPE_REF type_ref = TYPE_REF.REF_MEMORY;
	private MetaDataDatasetFMTInterface meta;
	private ByteArrayInputStream memory_input;
	
	public InputRefFmtMemory(byte[] byte_input, MetaDataDatasetFMTInterface metap) {
		this.memory_input=new ByteArrayInputStream(byte_input);
		this.meta=metap;
	}
	
	public TYPE_REF getTypeRef() {
		return type_ref;
	}
	
	public int getColumnCount() {
		return  meta.getListField().size();
	}

	public String getColumnName(int index_column) {
		return meta.getListField().get(index_column-1).getName();
	}

	public FieldInterface getField(int index_column) {
		return meta.getListField().get(index_column-1);
	}
	
	public void renameVarToLoad(String new_name,String old_name) throws InvalidDichiarationOptions {
		meta.renameInputVarIn(new_name, old_name);
	}
	
	public long getNumObsDs() {
		Long num_records=(Long)meta.getMapProperties().get(NameMetaParameters.NAME_META_PARAMETERS.NOBS_LONG);
		return num_records;
	}
	
	public ArrayList<FieldInterface> getListField() { 
		return meta.getListField();
	}

	public ByteArrayInputStream getByteArrayInput() {
		memory_input.reset();
		return memory_input;
	}
	
	public void close() {
		memory_input=null;
	}
}
