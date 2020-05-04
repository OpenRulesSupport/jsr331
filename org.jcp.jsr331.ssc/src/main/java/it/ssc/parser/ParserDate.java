package it.ssc.parser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ParserDate implements DateFormat {

	static boolean parser(String format_date,InputSubDichiarationVar single_input_step) {
		
		
		{
			Pattern pattern = Pattern.compile("object",Pattern.CASE_INSENSITIVE); 
			Matcher matcher = pattern.matcher(format_date);  
			if (matcher.matches()) {
				single_input_step.setFormatDate(DATE_FORMAT.DATA_OBJECT);  
				return true;
			}
		}
		
		
		{
			Pattern pattern = Pattern.compile("hh[.:]mm[.:]ss",Pattern.CASE_INSENSITIVE);  
			Matcher matcher = pattern.matcher(format_date);  
			if (matcher.matches()) {
				single_input_step.setFormatDate(DATE_FORMAT.HH_MM_SS); 
				return true;
			}
		}
		

		{
			Pattern pattern = Pattern.compile("gg[/-]mm[/-]aaaa",Pattern.CASE_INSENSITIVE);
			Matcher matcher = pattern.matcher(format_date);
			if (matcher.matches()) {
				single_input_step.setFormatDate(DATE_FORMAT.GG_MM_AAAA);
				return true;
			}
		}
		
		
		{
			Pattern pattern = Pattern.compile("ggmmaaaa",Pattern.CASE_INSENSITIVE);
			Matcher matcher = pattern.matcher(format_date);
			if (matcher.matches()) {
				single_input_step.setFormatDate(DATE_FORMAT.GGMMAAAA);
				return true;
			}
		}
		
		
		{
			Pattern pattern = Pattern.compile("gg[/-]mm[/-]aa",Pattern.CASE_INSENSITIVE);
			Matcher matcher = pattern.matcher(format_date);
			if (matcher.matches()) {
				single_input_step.setFormatDate(DATE_FORMAT.GG_MM_AA);
				return true;
			}
		}
		
		{
			Pattern pattern = Pattern.compile("ggmmaa",Pattern.CASE_INSENSITIVE);
			Matcher matcher = pattern.matcher(format_date);
			if (matcher.matches()) {
				single_input_step.setFormatDate(DATE_FORMAT.GGMMAA);
				return true;
			}
		}
		
		
		{
			Pattern pattern = Pattern.compile("mmggaa",Pattern.CASE_INSENSITIVE);
			Matcher matcher = pattern.matcher(format_date);
			if (matcher.matches()) {
				single_input_step.setFormatDate(DATE_FORMAT.MMGGAA);
				return true;
			}
		}
		
		
		{
			Pattern pattern = Pattern.compile("mmggaaaa",Pattern.CASE_INSENSITIVE);
			Matcher matcher = pattern.matcher(format_date);
			if (matcher.matches()) {
				single_input_step.setFormatDate(DATE_FORMAT.MMGGAAAA);
				return true;
			}
		}
		

		{
			Pattern pattern = Pattern.compile("mm[/-]gg[/-]aaaa",Pattern.CASE_INSENSITIVE);
			Matcher matcher = pattern.matcher(format_date);
			if (matcher.matches()) {
				single_input_step.setFormatDate(DATE_FORMAT.MM_GG_AAAA);
				return true;
			}
		}

		{
			Pattern pattern = Pattern.compile("mm[/-]gg[/-]aa",	Pattern.CASE_INSENSITIVE);
			Matcher matcher = pattern.matcher(format_date);
			if (matcher.matches()) {
				single_input_step.setFormatDate(DATE_FORMAT.MM_GG_AA);
				return true;
			}
		}

		{
			Pattern pattern = Pattern.compile("gg[/-]mmm[/-]aaaa",Pattern.CASE_INSENSITIVE);
			Matcher matcher = pattern.matcher(format_date);
			if (matcher.matches()) {
				single_input_step.setFormatDate(DATE_FORMAT.GG_MMM_AAAA);
				return true;
			}
		}

		return false;
	}

}
