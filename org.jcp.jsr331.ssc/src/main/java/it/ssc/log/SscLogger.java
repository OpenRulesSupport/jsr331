package it.ssc.log;

import java.io.IOException;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SscLogger {
	
	private static Logger ssc_logger=null;
	
	static {
		initLogger();
	}
	
	public static Logger getLogger() {
		return ssc_logger;
	}
	
	public static void log(String message) {
		ssc_logger.log(SscLevel.LOG,message);
	}
	
	
	public static void logFormatted(String message,Object... obj) {
		String new_message=String.format(message, obj);
		ssc_logger.log(SscLevel.LOG,new_message);
	}
	
	
	private static void  initLogger() {
		
		ssc_logger=Logger.getLogger("SSC");
		ssc_logger.setLevel(Level.INFO);
		/*rimuove gli handler per il logger SSC*/
		removeHandler();
		setLogToConsole();
		
	}
	
	public static void setLogToFile(String file_name) {
		setLogToFile(file_name,false);
	}
	
	public static void setLogToFile(String file_name, boolean append) {
		removeHandler();
		try {
			FileHandler new_handler=new FileHandler(file_name,append);
			new_handler.setLevel(Level.ALL);
			new_handler.setFormatter(new SscFormatter());
			ssc_logger.setUseParentHandlers(false);
			ssc_logger.addHandler(new_handler);
		} 
		
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public static void setLogToConsole() {
		Handler new_handler=new ConsoleHandler();
		new_handler.setLevel(Level.ALL);
		new_handler.setFormatter(new SscFormatter());
		ssc_logger.setUseParentHandlers(false);
		ssc_logger.addHandler(new_handler);
		
	}
	
	private static void removeHandler() {
		Handler[] handlers = ssc_logger.getHandlers();
		for(Handler handler : handlers) {
			ssc_logger.removeHandler(handler);
		}
	}
}