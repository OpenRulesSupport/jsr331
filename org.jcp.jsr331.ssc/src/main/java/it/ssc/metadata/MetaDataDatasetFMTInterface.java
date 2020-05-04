package it.ssc.metadata;

import it.ssc.step.exception.InvalidDichiarationOptions;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public interface MetaDataDatasetFMTInterface extends Serializable  {
		
	public HashMap<NameMetaParameters.NAME_META_PARAMETERS,Object> getMapProperties() ;
	public ArrayList<FieldInterface> getListField() ; 
	public void renameInputVarIn(String new_name,String old_name) throws InvalidDichiarationOptions;

}
