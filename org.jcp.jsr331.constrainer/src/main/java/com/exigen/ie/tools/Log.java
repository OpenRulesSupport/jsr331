/*
 * Created on May 14, 2003
 *
 * Developed by Intelligent ChoicePoint Inc. 2003
 */
 
package com.exigen.ie.tools;

import org.slf4j.LoggerFactory;

//import org.apache.commons.logging.LogFactory;

/**
 * @author snshor
 * 
 * Switched from commons.logging to slf4j ==JF
 *
 */
public class Log
{

	//static org.apache.commons.logging.Log logger = LogFactory.getLog(Log.class);
	private static org.slf4j.Logger logger = LoggerFactory.getLogger(Log.class);
	

	public static boolean isTraceEnabled()
	{
		return logger.isTraceEnabled();
	}	

	public static boolean isDebugEnabled()
	{
		return logger.isDebugEnabled();
	}	

	public static boolean isErrorEnabled()
	{
		return logger.isErrorEnabled();
	}	

	public static boolean isInfoEnabled()
	{
		return logger.isInfoEnabled();
	}	

	public static boolean isWarnEnabled()
	{
		return logger.isWarnEnabled();
	}	


	
	public static void debug(Object message)
	{
		logger.debug(String.valueOf(message));
	}
	
	
	public static void debug(Object message, Throwable t)
	{
		logger.debug(String.valueOf(message), t);
	}

	public static void info(Object message)
	{
		logger.info(String.valueOf(message));
	}

	public static void info(Object message, Throwable t)
	{
		logger.info(String.valueOf(message), t);
	}
	
	public static void trace(Object message)
	{
		logger.trace(String.valueOf(message));
	}

	public static void trace(Object message, Throwable t)
	{
		logger.trace(String.valueOf(message), t);
	}


	public static void warn(Object message)
	{
		logger.warn(String.valueOf(message));
	}

	public static void warn(Object message, Throwable t)
	{
		logger.warn(String.valueOf(message), t);
	}

	public static void error(Object message)
	{
		logger.error(String.valueOf(message));
	}

	public static void error(Object message, Throwable t)
	{
		logger.error(String.valueOf(message), t);
	}


}
