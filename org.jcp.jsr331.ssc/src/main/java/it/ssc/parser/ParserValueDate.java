package it.ssc.parser;


import it.ssc.parser.exception.InvalidDateFormatException;
import java.util.GregorianCalendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ParserValueDate implements DateFormat {
	
	private static final Pattern pattern_mmggaaaa;
	private static final Pattern pattern_mm_gg_aaaa;
	private static final Pattern pattern_mmggaa;
	private static final Pattern pattern_mm_gg_aa;
	private static final Pattern pattern_ggmmaa;
	private static final Pattern pattern_gg_mm_aa;
	private static final Pattern pattern_ggmmaaaa;
	private static final Pattern pattern_gg_mm_aaaa;
	private static final Pattern pattern_hh_mm_ss;
	
	
	static {
		// 010499     test OK
		pattern_mmggaa =   Pattern.compile("\\s*(\\d{6})\\s*");
		
		// 12-03-05  2/11/15   23 3 12     TESTATO 
		pattern_mm_gg_aa = Pattern.compile("\\s*(\\d{1,2})(([-/])|(\\s))(\\d{1,2})(([-/])|(\\s))(\\d{2})\\s*");
		
		// 01041999   test OK 
		pattern_mmggaaaa =  Pattern.compile("\\s*(\\d{8})\\s*");
		
		//02-31-2015   02/31/2015   02 31 2015  TEST OK 
		pattern_mm_gg_aaaa = Pattern.compile("\\s*(\\d{1,2})(([-/])|(\\s))(\\d{1,2})(([-/])|(\\s))(\\d{4})\\s*");
		
		//310314
		pattern_ggmmaa =  Pattern.compile("\\s*(\\d{6})\\s*");
		
		// 31-04-12   31/04/12  31 04 12    TEST OK 
		pattern_gg_mm_aa = Pattern.compile("\\s*(\\d{1,2})(([-/])|(\\s))(\\d{1,2})(([-/])|(\\s))(\\d{2})\\s*");
		
		//31042015         TEST ok 
		pattern_ggmmaaaa = Pattern.compile("\\s*(\\d{8})\\s*");
		
		// 31-04-2012   31/04/2012  31 04 2012   test ok 
		pattern_gg_mm_aaaa = Pattern.compile("\\s*(\\d{1,2})(([-/])|(\\s))(\\d{1,2})(([-/])|(\\s))(\\d{4})\\s*");
		
		
		pattern_hh_mm_ss=Pattern.compile("\\s*(\\d{1,2})(([.:])|(\\s))(\\d{1,2})(([.:])|(\\s))(\\d{1,2})\\s*");
		
		
	}
	
	
	public static GregorianCalendar parser(String value, DATE_FORMAT date_format_input) throws InvalidDateFormatException  {
		
		if(date_format_input==DATE_FORMAT.DATA_OBJECT ) 	{
			 throw new InvalidDateFormatException("ERROR. Formato data errato. Valore passato :"+value+ ". Richiesto un oggetto da utilizzare solo con input di tipo InputObject");
		}
		
		if(date_format_input==DATE_FORMAT.MM_GG_AA ) 	{
			Matcher matcher1 = pattern_mm_gg_aa.matcher(value);
			if (matcher1.matches()) {
				/*for(int a=0;a<=matcher.groupCount();a++) {
					System.out.println("KKKKK>>>>>" + matcher.group(a) + "<-:"+a);
				}*/
				int anno=Integer.parseInt(matcher1.group(9));
				int giorno=Integer.parseInt(matcher1.group(5));
				int mese=Integer.parseInt(matcher1.group(1));
				if(mese > 12 )  throw new InvalidDateFormatException("Formato data errato. Valore mese :"+mese+ ", valore richiesto <= 12");
				if(giorno > 31 ) throw new InvalidDateFormatException("Formato data errato. Valore giorno :"+giorno+ ", valore richiesto <= 31");
				if(anno >= 70 ) anno+=1900;
				else  anno+=2000;
				
				return	new GregorianCalendar(anno, mese-1, giorno);
			}
			else {
				 throw new InvalidDateFormatException("WARNING. Formato data errato. Valore passato :"+value+ ", formato richiesto MM-GG-AA");
			}
		}
		
		//TEST OK
		if(date_format_input==DATE_FORMAT.MMGGAA ) 	{
			Matcher matcher1 = pattern_mmggaa.matcher(value);
			if (matcher1.matches()) {
				String group1=matcher1.group(1);
				int anno=Integer.parseInt(group1.substring(4, 6));
				int giorno=Integer.parseInt(group1.substring(2, 4));
				int mese=Integer.parseInt(group1.substring(0, 2));
				if(mese > 12 )  throw new InvalidDateFormatException("Formato data errato. Valore mese :"+mese+ ", valore richiesto <= 12");
				if(giorno > 31 ) throw new InvalidDateFormatException("Formato data errato. Valore giorno :"+giorno+ ", valore richiesto <= 31");
				if(anno >= 70 ) anno+=1900;
				else  anno+=2000;
				
				return	new GregorianCalendar(anno, mese-1, giorno);
			}
			else {
				 throw new InvalidDateFormatException("ERRORE. Formato data errato. Valore passato :"+value+ ", formato richiesto MMGGAA");
			}
		}
		
		
		
		if(date_format_input==DATE_FORMAT.GG_MM_AA ) 	{
			Matcher matcher1 = pattern_gg_mm_aa.matcher(value);
			
			if (matcher1.matches()) {
				/*for(int a=0;a<=matcher.groupCount();a++) {
					System.out.println("KKKKK>>>>>" + matcher.group(a) + "<-:"+a);
				}*/
				int anno=Integer.parseInt(matcher1.group(9));
				int mese=Integer.parseInt(matcher1.group(5));
				int giorno=Integer.parseInt(matcher1.group(1));
				if(mese > 12 )  throw new InvalidDateFormatException("Formato data errato. Valore mese :"+mese+ ", valore richiesto <= 12");
				if(giorno > 31 ) throw new InvalidDateFormatException("Formato data errato. Valore giorno :"+giorno+ ", valore richiesto <= 31");
				if(anno >= 70 ) anno+=1900;
				else  anno+=2000;
				// new GregorianCalendar(anno, mese -1, giorno)
				return	new GregorianCalendar(anno, mese-1, giorno);
			}
			else {
				 throw new InvalidDateFormatException("Formato data errato. Valore passato :"+value+ ", formato richiesto GG-MM-AA");
			}
		}
		
	
		
		if(date_format_input==DATE_FORMAT.GGMMAA ) 	{
			Matcher matcher1 = pattern_ggmmaa.matcher(value);
			if (matcher1.matches()) {
				
				String group1=matcher1.group(1);
				int anno=Integer.parseInt(group1.substring(4, 6));
				int mese=Integer.parseInt(group1.substring(2, 4));
				int giorno =Integer.parseInt(group1.substring(0, 2));
				if(mese > 12 )  throw new InvalidDateFormatException("Formato data errato. Valore mese :"+mese+ ", valore richiesto <= 12");
				if(giorno > 31 ) throw new InvalidDateFormatException("Formato data errato. Valore giorno :"+giorno+ ", valore richiesto <= 31");
				if(anno >= 70 ) anno+=1900;
				else  anno+=2000;
				
				return	new GregorianCalendar(anno, mese-1, giorno);
			}
			else {
				 throw new InvalidDateFormatException("ERRORE. Formato data errato. Valore passato :"+value+ ", formato richiesto GGMMAA");
			}
		}
		
			
		else if(date_format_input==DATE_FORMAT.GG_MM_AAAA) 	{
			Matcher matcher2 = pattern_gg_mm_aaaa.matcher(value);
			if (matcher2.matches()) {
				int mese=Integer.parseInt(matcher2.group(5));
				int giorno=Integer.parseInt(matcher2.group(1));
				if(mese > 12 )  throw new InvalidDateFormatException("Formato data errato. Valore mese :"+mese+ ", valore richiesto <= 12");
				if(giorno > 31 ) throw new InvalidDateFormatException("Formato data errato. Valore giorno :"+giorno+ ", valore richiesto <= 31");
				return	new GregorianCalendar(Integer.parseInt(matcher2.group(9)), mese-1, giorno);
			}
			else {
				 throw new InvalidDateFormatException("Formato data errato. Valore passato :"+value+ ", formato richiesto GG-MM-AAAA");
			}
		}
		
		
		if(date_format_input==DATE_FORMAT.GGMMAAAA ) 	{
			Matcher matcher1 = pattern_ggmmaaaa.matcher(value);
			if (matcher1.matches()) {
				
				String group1=matcher1.group(1);
				int anno=Integer.parseInt(group1.substring(4, 8));
				int mese=Integer.parseInt(group1.substring(2, 4));
				int giorno=Integer.parseInt(group1.substring(0, 2));
				if(mese > 12 )  throw new InvalidDateFormatException("Formato data errato. Valore mese :"+mese+ ", valore richiesto <= 12");
				if(giorno > 31 ) throw new InvalidDateFormatException("Formato data errato. Valore giorno :"+giorno+ ", valore richiesto <= 31");
				
				return	new GregorianCalendar(anno, mese-1, giorno);
			}
			else {
				 throw new InvalidDateFormatException("ERRORE. Formato data errato. Valore passato :"+value+ ", formato richiesto GGMMAAAA");
			}
		}
		
		
		else if(date_format_input==DATE_FORMAT.MM_GG_AAAA) 	{
			Matcher matcher2 = pattern_mm_gg_aaaa.matcher(value);
			if (matcher2.matches()) {
				int giorno=Integer.parseInt(matcher2.group(5));
				int mese=Integer.parseInt(matcher2.group(1));
				if(mese > 12 )  throw new InvalidDateFormatException("Formato data errato. Valore mese :"+mese+ ", valore richiesto <= 12");
				if(giorno > 31 ) throw new InvalidDateFormatException("Formato data errato. Valore giorno :"+giorno+ ", valore richiesto <= 31");
				return	new GregorianCalendar(Integer.parseInt(matcher2.group(9)), mese-1, giorno);
			}
			else {
				 throw new InvalidDateFormatException("ERRORE. Formato data errato. Valore passato :"+value+ ", formato richiesto MM-GG-AAAA");
			}
		}
		// TEST OK 
		if(date_format_input==DATE_FORMAT.MMGGAAAA ) 	{
			Matcher matcher1 = pattern_mmggaaaa.matcher(value);
			if (matcher1.matches()) {
				
				String group1=matcher1.group(1);
				int anno=Integer.parseInt(group1.substring(4, 8));
				int giorno=Integer.parseInt(group1.substring(2, 4));
				int mese=Integer.parseInt(group1.substring(0, 2));
				if(mese > 12 )  throw new InvalidDateFormatException("Formato data errato. Valore mese :"+mese+ ", valore richiesto <= 12");
				if(giorno > 31 ) throw new InvalidDateFormatException("Formato data errato. Valore giorno :"+giorno+ ", valore richiesto <= 31");
				
				return	new GregorianCalendar(anno, mese-1, giorno);
			}
			else {
				 throw new InvalidDateFormatException("Formato data errato. Valore passato :"+value+ ", formato richiesto MMGGAAAA");
			}
		}
		
		
		else if(date_format_input==DATE_FORMAT.HH_MM_SS) 	{
			Matcher matcher2 = pattern_hh_mm_ss.matcher(value);
			if (matcher2.matches()) {
				return	new GregorianCalendar(1970, 0, 1,Integer.parseInt(matcher2.group(1)), Integer.parseInt(matcher2.group(5)), Integer.parseInt(matcher2.group(9)));
			}
			else {
				 throw new InvalidDateFormatException("Formato ora errato. Valore passato :"+value+ ", formato richiesto HH-MM-SS");
			}
		}
		return null;
	}
	
	
	public static boolean isPatternDdMmYyyy(String value) {
		
		Matcher matcher2 = pattern_gg_mm_aaaa.matcher(value);
		if (matcher2.matches()) return true;
		return false;
	}
	
	
	

	public static boolean isPatternHhMmSs(String value) {

		Matcher matcher2 = pattern_hh_mm_ss.matcher(value);
		if (matcher2.matches())	return true;
		return false;
	}
}
