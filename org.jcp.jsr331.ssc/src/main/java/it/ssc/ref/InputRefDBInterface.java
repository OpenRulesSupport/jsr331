package it.ssc.ref;

import it.ssc.metadata.FieldInterface;

import java.util.ArrayList;

public interface InputRefDBInterface extends Input {
	public ArrayList<FieldInterface> getListField() ;
	public java.sql.ResultSet getResultSet() ;
	public String getSql();
	public void executeQuery() throws Exception ;
	
	//public void setResultSetToNull() ;
}
