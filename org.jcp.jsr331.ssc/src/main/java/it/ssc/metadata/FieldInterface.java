package it.ssc.metadata;

import java.io.Serializable;

public interface FieldInterface extends Serializable {

	
	public Class getType();
	public int getLenght();
	public String getName();
	public String getLabel();
	public String getFormat();
	public int getPrecision();
	public int getScale();
	public int getTypeSql();
	public void renameField(String new_name);

}