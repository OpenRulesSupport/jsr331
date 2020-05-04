package it.ssc.i18n;

import java.text.MessageFormat;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;

public class RB {
	public static ResourceBundle msg; 
	
	
	static { 
		msg= ResourceBundle.getBundle(RB.class.getPackage().getName()+".MessagesBundle");
	}
	
	public static void main(String arg[]) {
		System.out.println(RB.getString("it.ssc.library.library.DbLibrary.msg1"));
	}
	
	public static String getString(String key) {
		return msg.getString(key);
	}
	
	public static String format(String key, Object... arguments) {
		return MessageFormat.format(RB.getString(key),arguments);
	}
	
	public static String getHhMmSsMmm(long hmsm) {
		return String.format("%02d:%02d:%02d:%03d", TimeUnit.MILLISECONDS.toHours((hmsm)),
            TimeUnit.MILLISECONDS.toMinutes((hmsm)) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours((hmsm))),
            TimeUnit.MILLISECONDS.toSeconds((hmsm)) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((hmsm))),
            TimeUnit.MILLISECONDS.toMillis(((hmsm)) - TimeUnit.SECONDS.toMillis(TimeUnit.MILLISECONDS.toSeconds((hmsm)))) );
	}	
}
