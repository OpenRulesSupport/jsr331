package it.ssc.log;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import it.ssc.context.Config;


public final class SscFormatter extends Formatter { 
	
	private static final DateFormat sdf = new SimpleDateFormat("dd/MM/yy kk:mm:ss"); 
	private static final String LINE_SEPARATOR = Config.NL;
	
	@Override
	public String format(LogRecord arg0) {
		StringBuffer buffer=new StringBuffer();
		buffer.append(sdf.format(new Date()));
		buffer.append(" - ");
		buffer.append(replaceSevereWhitError(arg0));
		buffer.append(": ");
		buffer.append(arg0.getMessage());
		buffer.append(LINE_SEPARATOR); 
		return buffer.toString();
	}
	
	private static String replaceSevereWhitError(LogRecord arg0)  {
		Level level=arg0.getLevel();
		if(level.intValue()==Level.SEVERE.intValue()) return "ERROR SEVERE";
		return level.getName();
	}
}
