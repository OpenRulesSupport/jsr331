package it.ssc.pl.milp;

/**
 * Questa enumerazione definisce i diversi tipi di risultati che il metodo delle due fasi restituisce
 * 
 * @author Stefano Scarioli 
 * @version 1.0
 * @see <a target="_new" href="http://www.ssclab.org">SSC Software www.sscLab.org</a>
 *
 */

public enum SolutionType { 
	/**
	 * Il problema ammette soluzione ottima
	 */
	OPTIMUM ("excellent solution"), 
	/**
	 * Il problema ha ottimo illimitato
	 */
	ILLIMITATUM ("Solution unlimited/excellent unlimited"), 
	/**
	 * L'algoritmo si &egrave; interrotto in quanto si &egrave; raggiunto il numero massimo 
	 * di iterazioni. 
	 */
	MAX_ITERATIUM ("Reached the maximum number of iterations"), 
	/**
	 * L'algoritmo branch and bound si &egrave; interrotto in quanto si &egrave; raggiunto il numero massimo 
	 * di simplessi eseguibili. 
	 */
	MAX_NUM_SIMPLEX ("reached the maximum number of simplex"), 
	/**
	 * Il problema non ammette soluzioni ammissibili. L'insieme delle soluzioni ammissibili &egrave; 
	 * un insieme vuoto. 
	 */
	VUOTUM ("no Solutions / Empty"),
	/**
	 * Il problema ammette soluzioni ammissibili. 
	 */
	
	FEASIBLE ("Feasible solution")
	;  
	
	public static final SolutionType INFEASIBLE=VUOTUM;
	public static final SolutionType UNLIMITED=ILLIMITATUM;
	public static final SolutionType OPTIMAL=OPTIMUM;
	public static final SolutionType MAX_ITERATIONS=MAX_ITERATIUM;
	
	private String value;
	private SolutionType(String epsilon) { 
		this.value=epsilon;
	}
	
	public String getValue() {
		return this.value;
	}
	
	public String toString() {
		return this.value;
	}
}

