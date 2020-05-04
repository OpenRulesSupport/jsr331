package it.ssc.datasource;

import java.util.GregorianCalendar;
import java.util.function.Function;



public interface DataSource {
	
	public int getNumColunm();
	
	public String getNameColunm(int column_index);
	
	public boolean next() throws Exception;

	public void close() throws Exception; 
	
	public Object getObject(String name) throws DataSourceException;
	
	public Object getObject(int column_index) ;
	
	public Byte getByte(String name) throws DataSourceException ;
	
	public Byte getByte(int column_index) ;
	
	public Short getShort(String name)  throws DataSourceException;
	
	public Short getShort(int column_index) ;
	
	public Integer getInteger(String name) throws DataSourceException;
	
	public Integer getInteger(int column_index) ;
	
	public Long getLong(String name) throws DataSourceException;
	
	public Long getLong(int column_index) ;
	
	public Float getFloat(String name) throws DataSourceException;
	
	public Float getFloat(int column_index) ;
	
	public Double getDouble(String name) throws DataSourceException;
	
	public Double getDouble(int column_index) ;
	
	public Character getCharacter(String name) throws DataSourceException;
	
	public Character getCharacter(int column_index) ;
	
	public String getString(String name) throws DataSourceException;
	
	public String getString(int column_index) ;
	
	public Boolean getBoolean(String name) throws DataSourceException;
	
	public Boolean getBoolean(int column_index); 
	
	public GregorianCalendar getGregorianCalendar(String name) throws DataSourceException;
	
	public GregorianCalendar getGregorianCalendar(int column_index) ;
	
	public <I,R> R getValueReduce(Function<I,R> remapping,String name) throws DataSourceException; 


}
