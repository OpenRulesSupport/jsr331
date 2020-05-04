package it.ssc.pl.milp;

import it.ssc.log.SscLogger;

import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;

 class ObjectiveFunctionImpl implements Cloneable, ObjectiveFunction,Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final Logger logger=SscLogger.getLogger();
	private double[] C;
	private TARGET_FO type= TARGET_FO.MAX;
	
	public ObjectiveFunctionImpl(int dimension) {
		C=new double[dimension];
	}
	
	public void setCj(int j, double cj) {
		//System.out.println("VALUE Cj "+cj);
		C[j]=cj;
	}
	
	public double getCj(int j) {
		return C[j];
	}
	
	
	public TARGET_FO getType() {
		return type;
	}
	public void setType(TARGET_FO type) {
		this.type = type;
	}
	
	
	public void standardize() {
		if(this.type==TARGET_FO.MIN) {
			this.type=TARGET_FO.MAX;
			for(int j=0;j<C.length;j++) {
				if(!Double.isNaN(C[j]) && C[j]!=0) C[j]=-C[j];
			}
		}
	}
	
	
	public ObjectiveFunctionImpl clone() {
		
		ObjectiveFunctionImpl clone=null;
		try {
			clone=(ObjectiveFunctionImpl)super.clone();
			clone.C=(double[])C.clone();
		} 
		catch (CloneNotSupportedException e) {
			logger.log(Level.SEVERE,"Clonazione it.ssc.pl.milp.ObjFunction",e);
		}
		return clone;
	}
}
