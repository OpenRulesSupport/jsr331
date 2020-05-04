package it.ssc.formats;

public interface FormatInterface {
	
	 /**
	   * Rinomina un formato con nome con un nuovo nome, il nome puo essere lo stesso 
	   * se pero' hanno spazio dei nomi diversi. Di conseguenza, tale metodo puo' essere 
	   * usato per cambiare anche il solo spazio dei nomi. 
	   * 
	   * @param new_name
	   */
	  
	  public void ranameFormat(String new_name);
	  
}