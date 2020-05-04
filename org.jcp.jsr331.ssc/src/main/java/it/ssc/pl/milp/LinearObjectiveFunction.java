package it.ssc.pl.milp;

import it.ssc.i18n.RB;

/**
 * Questa classe permette di istanziare oggetti che rappresentano la funzione obiettivo 
 * in problemi di LP espressi nella notazione matriciale 
 * 
 * @author Stefano Scarioli
 * @version 1.0
 * @see <a target="_new" href="http://www.ssclab.org">SSC Software www.sscLab.org</a> 
 *
 */

public final class LinearObjectiveFunction {
	
	private GoalType type;
	private double[] C;
	
	/**
	 * 
	 * @return il tipo di ottimizzazione (MAX o MIN)
	 */
	
	public GoalType getType() {
		return type;
	}
	/**
	 * 
	 * @return il vettore dei coefficienti della funzione obiettivo 
	 */

	public double[] getC() {
		return C;
	}
	/**
	 * Costruttore 
	 * 
	 * @param C Il vettore dei coefficienti della funzione obiettivo
	 * @param type Il tipo di ottimizzazione (MAX o MIN) come istanza della enumerazione GoalType
	 * @throws LPException Se i parametri sono incongruenti con il problema
	 */

	public LinearObjectiveFunction(double[] C, GoalType type) throws  LPException {
		if(type==null) throw new LPException(RB.getString("it.ssc.pl.milp.LinearObjectiveFunction.msg1"));
		this.type=type;
		if(C==null) throw new LPException(RB.getString("it.ssc.pl.milp.LinearObjectiveFunction.msg2"));
		this.C=C;
	}

}
