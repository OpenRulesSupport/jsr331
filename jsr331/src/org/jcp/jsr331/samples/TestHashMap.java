package org.jcp.jsr331.samples;

import java.util.HashMap;
import java.util.Iterator;

public class TestHashMap {
	
	  public static void main(String[] args) {
	    HashMap<String,String> errors = new HashMap<String,String>();

	    errors.put("404", "A.");
	    errors.put("403", "B.");
	    errors.put("403", "BB.");
	    errors.put("500", "C.");

	    String errorDesc = (String) errors.get("404");
	    System.out.println("Error 404: " + errorDesc);
	    
		String[] values =  (String[]) errors.values().toArray();
		for(int i=0; i < values.length; i++)
			System.out.println("Value[=" + i + "]=" + values[i]);
			

	    Iterator<String> iterator = errors.keySet().iterator();
	    while (iterator.hasNext()) {
	      String key = (String) iterator.next();
	      System.out.println("Error " + key + " means " + errors.get(key));
	    }
	  }
}
