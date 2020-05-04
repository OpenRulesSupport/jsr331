package it.ssc.metadata;


public interface MetaDataTableInterface {

  public String getLabel(int index_column);

  public String getName(int index_column);
  
  public void getFormat(int index_column);
  
  public FieldInterface getField(int index_column);

  public void getLabel(String name_field);

  public void getFormat(String name_field);

  public FieldInterface getField(String name_column);
  
  public Integer getColumnsCount();

}