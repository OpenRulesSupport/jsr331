package it.ssc.ref;

import it.ssc.metadata.FieldInterface;

/*
 */
public interface Input {

	public enum TYPE_REF {REF_FILE, REF_DB, REF_SSC, REF_MEMORY, REF_STRING, REF_OBJECT};

	public TYPE_REF getTypeRef();
	
	public int getColumnCount() throws Exception;
	
	public String getColumnName(int index_column) throws Exception;

	public FieldInterface getField(int index_column);
	
	/**
	 * Rinomina la variabile letta dall'input. Non la rinomina sull'input. 
	 * Sul dataset di origine o sula tabella la variabile riname sempre con lo stesso nome
	 * 
	 * @param new_name
	 * @param old_name
	 * @throws Exception
	 */
	public void renameVarToLoad(String new_name,String old_name) throws Exception;
	
	public void close() ;
	
	

}