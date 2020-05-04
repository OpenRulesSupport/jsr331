package it.ssc.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TestValidName {
	
	public static boolean isValidNameLibrary(String name) {
		
		Pattern pattern_azioni = Pattern.compile("\\p{Alpha}+[_\\p{Alnum}]*");
		Matcher matcher_azioni = pattern_azioni.matcher(name);

		if (matcher_azioni.matches()) {
			return  true;
		}
		return  false;
	}
	

	public static boolean isValidNameDataset(String name) {
		
		Pattern pattern_azioni = Pattern.compile("\\p{Alpha}+[_\\p{Alnum}]*");
		Matcher matcher_azioni = pattern_azioni.matcher(name);
		
		//SAS
		Pattern pattern_azioni2 = Pattern.compile("\\p{Alpha}+[_\\p{Alnum}]*\\.\\p{Alpha}+[_\\p{Alnum}]*");
		Matcher matcher_azioni2 = pattern_azioni2.matcher(name);

		if (matcher_azioni.matches()) {
			return  true;
		}
		else if (matcher_azioni2.matches()) {
			return  true;
		}
		return  false;
	}
}

