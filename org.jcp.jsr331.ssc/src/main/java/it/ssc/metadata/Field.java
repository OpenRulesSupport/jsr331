package it.ssc.metadata;

public class Field implements FieldInterface {
	
	
	private static final long serialVersionUID = -1078129626512796835L;
	
	
	@SuppressWarnings("rawtypes")
	private Class type_fiels;
	private int lenght_field;
	private String name_field;
	private String label;
	private String format;
	private int precision;
	private int scale;
	private int type_sql;
	
	@SuppressWarnings("rawtypes")
	public Field(Class type, int lenght, String name,int precision,int scale,int type_sql) {
		this.type_fiels=type;
		this.type_sql=type_sql;
		this.lenght_field=lenght;
		this.name_field=name;
		this.precision=precision;
		this.scale=scale;
	}
	
	public int getTypeSql() {
		return type_sql;
	}
	
	@SuppressWarnings("rawtypes")
	public Class getType() {
		return type_fiels;
	}

	public int getLenght() {
		return lenght_field;
	}

	public String getName() {
		return name_field;
	}
	
	public void renameField(String new_name) {
		this.name_field=new_name;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	public int getPrecision() {
		return precision;
	}

	public int getScale() {
		return scale;
	}
}