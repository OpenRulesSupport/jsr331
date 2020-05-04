package it.ssc.step.trasformation;

import it.ssc.log.SscLevel;
import it.ssc.log.SscLogger;
import it.ssc.pdv.PDVAll;
import it.ssc.step.exception.InvalidDichiarationOptions;

import java.util.GregorianCalendar;
import java.util.logging.Logger;

public class BeanEquiJoin {
	
	private static final Logger logger=SscLogger.getLogger();
	
	private String name_var1;
	private String name_var2;
	
	public BeanEquiJoin(String var1,String var2)  {
		this.name_var1=var1;
		this.name_var2=var2;
		if(var1.toUpperCase().equals(var2.toUpperCase())) {
			logger.log(SscLevel.WARNING,"Le variabili per la join hanno lo stesso nome, la condizione non avra' effetto e potrebbe creare un prodotto cartesiano");
		}
	}
	
	public void  testExisteVars(PDVAll pdv) throws InvalidDichiarationOptions  {
		if(!testExisVar(pdv, name_var1)) 
			 throw new InvalidDichiarationOptions("ERRORE. La variabile dichiarata nell'istruzione di join, '"+name_var1 +"' , non esiste");
		if(!testExisVar(pdv, name_var2)) 
			 throw new InvalidDichiarationOptions("ERRORE. La variabile dichiarata nell'istruzione di join, '"+name_var2 +"' , non esiste");
	
	}
	
	//Fare controllo su tipi (isComparable), nomi esistenti nel pdv, ma se ci si riesce
	//vedere se appartengono a tabelle diverse, perche altrimenti fa prodotto cartesiano
	
	public  void isComparable(PDVAll pdv) throws InvalidDichiarationOptions {
		Class<?> cla1=pdv.getField(name_var1).type;
		Class<?> cla2=pdv.getField(name_var2).type;
		boolean is_numeric1=false;
		boolean is_numeric2=false;
	    if(  (cla1==String.class && cla2==String.class) || 
	    	 (cla1==String.class && cla2==StringBuffer.class)  || 
	    	 (cla1==StringBuffer.class && cla2==String.class) || 
	    	 (cla1==StringBuffer.class && cla2==StringBuffer.class))  {
	    	return ;
	    }
	    else   if(  (cla1==Boolean.class && cla2==Boolean.class) )  {
	    	return ;
	    }
	    else   if(  (cla1==GregorianCalendar.class && cla2==GregorianCalendar.class) )  {
	    	return ;
	    }
	    
	    if(cla1==Byte.class || cla1==Short.class || cla1==Integer.class || cla1==Long.class || cla1==Float.class || cla1==Double.class || cla1==Character.class)  {
	    	is_numeric1=true;
	    }
	    if(cla2==Byte.class || cla2==Short.class || cla2==Integer.class || cla2==Long.class || cla2==Float.class || cla2==Double.class || cla2==Character.class)  {
	    	is_numeric2=true;
	    }
	    if(is_numeric1 && is_numeric2) {
	    	return ;
	    }
	    throw new InvalidDichiarationOptions("ERRORE. Le variabili dichiarate nell'istruzione di join non sono comparabili tra di loro : "+cla1.getName() +" con "+cla2.getName());
		
	}
	
	
	private boolean testExisVar(PDVAll pdv, String name1) 	 {
		int nun_filed = pdv.getSize();
		for (int a = 0; a < nun_filed; a++) {
			String name_var = pdv.getField(a).getName();
			if (name1.equals(name_var)) {
				return true;
			}
		}
		return false;
	}
	
	public String getNameVar1() {
		return name_var1;
	}

	public String getNameVar2() {
		return name_var2;
	}
}
