package it.ssc.pdv;

import java.util.GregorianCalendar;

public class Wrapper {

	
	public static String unwrapper(String value) {
		return value;
	}
	
	public static long unwrapper(GregorianCalendar value) {
		return value.getTimeInMillis();
	}
	
	public static String unwrapper(StringBuffer value) {
		return value.toString();
	}
	
	public static byte unwrapper(Byte value) {
		return value.byteValue();
	}

	public static short unwrapper(Short value) {
		return value.shortValue();
	}
	
	public static int unwrapper(Integer value) {
		return value.intValue();
	}
	
	public static long unwrapper(Long value) {
		return value.longValue();
	}
	
	public static float unwrapper(Float value) {
		return value.floatValue();
	}
	
	public static double unwrapper(Double value) {
		return value.doubleValue();
	}
	
	public static char unwrapper(Character value) {
		return value.charValue();
	}
	
	public static boolean unwrapper(Boolean value) {
		return value.booleanValue();
	}
}
