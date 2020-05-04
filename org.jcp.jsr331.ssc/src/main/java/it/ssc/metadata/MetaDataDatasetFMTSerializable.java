package it.ssc.metadata;

import it.ssc.step.exception.InvalidDichiarationOptions;

import java.util.ArrayList;
import java.util.HashMap;

public class MetaDataDatasetFMTSerializable implements MetaDataDatasetFMTInterface {
	

	private static final long serialVersionUID = 7693698462052741045L;
	
	private ArrayList<FieldInterface> meta_field;
	private HashMap<NameMetaParameters.NAME_META_PARAMETERS,Object> properties;
	
	public MetaDataDatasetFMTSerializable() {
		this.properties=new HashMap<NameMetaParameters.NAME_META_PARAMETERS,Object>();
		this.meta_field=new ArrayList<FieldInterface>();
	}
	
	public HashMap<NameMetaParameters.NAME_META_PARAMETERS,Object> getMapProperties() {
		return properties;
	}
	
	public void setProperties(NameMetaParameters.NAME_META_PARAMETERS key,Object value) {
		properties.put(key, value);
	}
	
	public void addField(FieldInterface field) {
		this.meta_field.add(field);
	}

	public ArrayList<FieldInterface> getListField() {
		return meta_field;
	}
	
	public void renameInputVarIn(String new_name,String old_name) throws InvalidDichiarationOptions {
		boolean trovato=false;
		for(FieldInterface field:meta_field) {
			if(field.getName().equals(old_name)) {
				field.renameField(new_name);
				trovato=true;
			}
		}
		if(!trovato) throw new InvalidDichiarationOptions("La variabile da rinominare "+old_name +" non esiste");
	}
}
