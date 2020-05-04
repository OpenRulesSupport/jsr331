package it.ssc.util;

import it.ssc.dataset.exception.InvalidNameDataset;
import it.ssc.library.FmtLibrary;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ParseLibDotDs {
	private String library;
	private String dataset;
	private String lib_dot_ds; 
	
	public ParseLibDotDs(String lib_dot_ds) throws InvalidNameDataset {
		
		if(lib_dot_ds==null) throw new InvalidNameDataset("ERRORE. Nome libreria.dataset  e' a NULL");
		this.lib_dot_ds=lib_dot_ds;
		this.library=FmtLibrary.NAME_LIBRARY_WORK;
		this.dataset="";
	}
	
	public String getLibrary()  {
		return library;
	}
	public String getNameDs() {
		return dataset;
	}
	
	public  void parse() throws InvalidNameDataset {
		
		//LIBRERIA.DATASET 
		
		Pattern pattern_azioni = Pattern.compile("(([^\\.]*)\\.{1})?([^\\.]+)");
		Matcher matcher_azioni = pattern_azioni.matcher(this.lib_dot_ds);
		
		//LIBRERIA.1LEVEL.DATASET
		//SAS
		Pattern pattern_azioni2 = Pattern.compile("(([^\\.]*)\\.{1})([_\\p{Alnum}]+\\.[_\\p{Alnum}]+)");
		Matcher matcher_azioni2 = pattern_azioni2.matcher(this.lib_dot_ds);

		if (matcher_azioni.matches()) {
			if (matcher_azioni.group(2) != null) {
				String appo=matcher_azioni.group(2); 
				this.library=appo.toUpperCase(); 
			}
			if (matcher_azioni.group(3) != null) {
				String appo=matcher_azioni.group(3);
				this.dataset=appo;
			}
		}
		else if (matcher_azioni2.matches()) {
			if (matcher_azioni2.group(2) != null) {
				String appo=matcher_azioni2.group(2); 
				//System.out.println("LIB:"+appo);
				this.library=appo.toUpperCase(); 
			}
			if (matcher_azioni2.group(3) != null) {
				String appo=matcher_azioni2.group(3);
				//System.out.println("DS:"+appo);
				this.dataset=appo;
			}
		}
		
		
		
		else {
			throw new InvalidNameDataset("ERRORE. Nome libreria.dataset non valido :"+this.lib_dot_ds);
		}
	}
}

