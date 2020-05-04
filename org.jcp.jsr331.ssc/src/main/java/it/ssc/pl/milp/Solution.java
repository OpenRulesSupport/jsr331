package it.ssc.pl.milp;

/**
 * 
 * 
 * Questa interfaccia permette di accedere  
 * ai valori assunti dalle n variabili della soluzione ottima. 
 * 
 * @author Stefano Scarioli 
 * @version 1.0 
 * @see <a target="_new" href="http://www.ssclab.org">SSC Software www.sscLab.org</a>
 *
 */

public interface Solution {
	
	/**
	 * 
	 * @return Un array di oggetti Variable da cui prelevare le caratteristiche 
	 *          ed il valore ottimo assunto da ogni variabile. 
	 *         
	 */
	public Variable[] getVariables();
	/**
	 * 
	 * @return Il valore ottimo assunto dalla funzione obiettivo. 
	 */
	public double getOptimumValue();
	/**
	 * 
	 * @return Il tipo di soluzione ottenuta 
	 */
	public SolutionType getTypeSolution(); 
	/**
	 * 
	 * @return Un array di oggetti vincolo, da cui ricavare il valore che 
	 *         ciascun vincolo assume sostituendo alla variabili incognite 
	 *         la soluzione ottima. 
	 */
	public SolutionConstraint[] getSolutionConstraint();
	
	/**
	 * 
	 * @return Il valore assunto dalla funzione obiettivo. 
	 */
	public double getValue();
	
}
