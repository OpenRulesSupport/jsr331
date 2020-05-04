package it.ssc.pl.milp;

import it.ssc.i18n.RB;

/**
 * Questa classe permette di costruire oggetti ognuno dei quali rappresenta un vincolo per  
 * un problema di LP espresso nella notazione matriciale
 * 
 * @author Stefano Scarioli 
 * @version 1.0
 * @see <a target="_new" href="http://www.ssclab.org">SSC Software www.sscLab.org</a>
 */



public final class Constraint {
	private double[] Aj;
	private ConsType rel;
	private double bi;
	
	/**
	 * 
	 * @param Aj La parte LHS del j-esimo vincolo del problema
	 * @param rel Il tipo  di vincolo/relazione (EQ,LE,GE,UPPER,LOWER,INT,BIN,SEMICONT)
	 * @param rhs La parte RHS del vincolo o coefficiente bj 
	 * @throws SimplexException Se il vincolo non &egrave; congruente 
	 */
	
	public Constraint(double[] Aj, ConsType rel, double rhs) throws SimplexException {
		if(Aj==null) throw new SimplexException(RB.getString("it.ssc.pl.milp.Constraint.msg1"));
		this.Aj=Aj;
		if(rel==null) throw new SimplexException(RB.getString("it.ssc.pl.milp.Constraint.msg2"));
		this.rel=rel;
		this.bi=rhs;
	}
	
	/**
	 * 
	 * @return La parte LHS del j-esimo vincolo del problema
	 */

	public double[] getAj() {
		return Aj;
	}
	/**
	 * 
	 * @return Il tipo  di vincolo (relazione) definito (EQ,LE,GE)
	 */

	public ConsType getRel() {
		return rel;
	}
	/**
	 * 
	 * @return La parte RHS del vincolo o coefficiente bj 
	 */

	public double getRhs() {
		return bi;
	}
}
