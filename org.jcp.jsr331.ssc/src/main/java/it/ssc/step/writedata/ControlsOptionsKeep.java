package it.ssc.step.writedata;

import java.util.logging.Level;
import java.util.logging.Logger;

import it.ssc.log.SscLogger;
import it.ssc.pdv.PDVAll;
import it.ssc.step.exception.InvalidDichiarationOptions;


public class ControlsOptionsKeep {
	
	private static final Logger logger=SscLogger.getLogger();
	
	public static void controls(PDVAll pdv, OptionsWrite opt_write) throws InvalidDichiarationOptions {
		boolean k=controlsKeep(pdv, opt_write);
		boolean d=controlsDrop(pdv, opt_write);
		if(k && d) throw new InvalidDichiarationOptions("ERRORE. Non possono essere dichiarate contemporaneamente sia l'opzione KEPP che DROP");
	}
	
	public static void controlsKeepPrintf(PDVAll pdv, OptionsWrite opt_write) throws InvalidDichiarationOptions {
		String[] var_print=opt_write.getVarToPrint();
		if(var_print==null) return;
		pdv.resetAllVarToDropValue(true);
		for (String var:var_print) {
			if(pdv.getField(var)==null) { 
				throw new InvalidDichiarationOptions("ERRORE. La variabile dichiarata nella printf, '"+var +"' , non esiste");
			}
			else { 
				pdv.getField(var).drop=false;
			}
		}	
	}
	
	private static boolean controlsKeep(PDVAll pdv, OptionsWrite opt_write) throws InvalidDichiarationOptions {
		String[] var_keep=opt_write.getKeepOutput();
		if(var_keep.length > 0) pdv.resetAllVarToDropValue(true);
		for (String var:var_keep) {
			
			if(pdv.getField(var)==null) { 
				throw new InvalidDichiarationOptions("ERRORE. La variabile dichiarata nell'opzione KEEP, '"+var +"' , non esiste");
			}
			else { 
				logger.log(Level.INFO,"Keep variabile: "+var+" ");
				pdv.getField(var).drop=false;
			}
		}	
		return var_keep.length > 0 ? true :false;
	}
	
	private static boolean  controlsDrop(PDVAll pdv, OptionsWrite opt_write) throws InvalidDichiarationOptions {
		String[] var_drop=opt_write.getDropOutput();
		if(var_drop.length > 0) pdv.resetAllVarToDropValue(false);
		for (String var:var_drop) {
			
			if(pdv.getField(var)==null)  { 
				throw new InvalidDichiarationOptions("ERRORE. La variabile dichiarata nell'opzione DROP, '"+var +"' , non esiste");
			}
			else { 
				logger.log(Level.INFO,"Drop variabile: "+var+" ");
				pdv.getField(var).drop=true;
			}
		}	
		return var_drop.length > 0 ? true :false;
	}
}
