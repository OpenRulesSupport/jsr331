package it.ssc.pl.milp;

import it.ssc.i18n.RB;
import it.ssc.log.SscLogger;

import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;

final class Var implements Cloneable, Variable, Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final Logger logger=SscLogger.getLogger();
	
	private String name;
	private TYPE_VAR type=TYPE_VAR.REAL;
	private double upper;
	private double lower;
	private boolean is_free;
	private double value;
	private boolean is_lower_modified=false;
	private boolean isZeroSemicontVar=false;
	
	public boolean isZeroSemicontVar() {
		return isZeroSemicontVar;
	}


	public void setZeroSemicontVar(boolean isZeroSemicontVar) {
		this.isZeroSemicontVar = isZeroSemicontVar;
	}

	//parte per variabili semi-continue
	private double upperSemicon; 
	private double lowerSemicon; 
	private boolean   isSemicon;
	
	public Var() {
		lower=0.0;   //IMPORTANTE
		upper=Double.NaN;
		lowerSemicon=0.0;
		is_free=false;
	}
	
	
	public void resetUpperLower() {
		lower=0.0;   //IMPORTANTE
		upper=Double.NaN;
		is_free=false;
		is_lower_modified=false;
	}
	
	public double getUpperSemicon() {
		return upperSemicon;
	}

	public void setUpperSemicon(Double upperSemicon) {
		this.upperSemicon = upperSemicon;
	}

	public double getLowerSemicon() {
		return lowerSemicon;
	}

	public void setLowerSemicon(Double lowerSemicon) {
		this.lowerSemicon = lowerSemicon;
	}

	public boolean isSemicon() {
		return isSemicon;
	}

	public void setSemicon(boolean isSemicon) {
		this.isSemicon = isSemicon;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public TYPE_VAR getType() {
		return type;
	}

	public void setType(TYPE_VAR type) {
		this.type = type;
	}

	public double getUpper() {
		return upper;
	}
	
	public boolean getUpperIsNaN() {
		return Double.isNaN(upper);
	}

	public void setUpper(double upper) throws LPException {
		if(!Double.isNaN(upper) && upper < 0.0) {
			//se l'upper bound e' minore di zero la variabile e' libera
			this.is_free=true;
			if(is_lower_modified) {
				if(!Double.isNaN(lower) && lower > upper) {
					throw new LPException(RB.format("it.ssc.pl.milp.Var.msg1", lower,upper));
				}
				//Modificato per considerare non più i lower come un vincolo ma sostituiti da una nuova variabile X =Xi -lower
				if(!Double.isNaN(lower)) this.is_free=false;
			}
		}
		
		else if(!Double.isNaN(upper) && !Double.isNaN(lower))  {
			if(lower > upper) throw new LPException(RB.format("it.ssc.pl.milp.Var.msg1", lower,upper));
		}
		this.upper = upper;
	}
	

	public double getLower() {
		return lower;
	}
	
	
	public boolean getLowerIsNaN() {
		return Double.isNaN(lower);
	}
	

	public void setLower(double lower) throws LPException {
		//if(lower==null || lower < 0.0) is_free=true;   __OLDDD
		if(Double.isNaN(lower) ) is_free=true;  //solo se lower = -infinito -> free. Altrimentio facccio trasformazione con x-l >=0  
		else is_free=false;
		if(!Double.isNaN(upper) && !Double.isNaN(lower))  {
			if(lower > upper) throw new LPException("Errore definizione degli upper e lower bound. Il lower ("+lower+") non puo' essere maggiore dell'upper ("+upper+")");
		}
		this.lower = lower;
		this.is_lower_modified=true;
	}
	
	
	public boolean isFree() {
		return is_free;
	}

	public Var clone() {
	
		Var clone=null;
		try {
			clone=(Var)super.clone();
		} 
		catch (CloneNotSupportedException e) {
			logger.log(Level.SEVERE,"Clonazione it.ssc.pl.milp.Var",e);
		}
		return clone;
	}
	
	public double getValue() {
		return this.value;
	}
	
	public void setValue(double val) {
		this.value=val;
	}
}
