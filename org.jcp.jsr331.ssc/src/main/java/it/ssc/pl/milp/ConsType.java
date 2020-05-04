package it.ssc.pl.milp;

/**
 * Enumerazione utilizzata per definire il tipo di relazione in un oggetto Constraint
 * 
 * @author Stefano Scarioli
 * @version 1.0
 * @see <a target="_new" href="http://www.ssclab.org">SSC Software www.sscLab.org</a>
 */

public enum  ConsType {
	/**
	 * Per definire un vincolo del tipo = 
	 */
	EQ, 
	/**
	 * Per definire un vincolo del tipo &lt; = 
	 */
	LE, 
	/**
	 * Per definire un vincolo del tipo &gt; = 
	 */
	GE, 
	/**
	 * Per definire variabili intere
	 */
	INT, 
	/**
	 * Per definire variabili binarie
	 */
	BIN, 
	/**
	 * Per la definizione degli Upper bound 
	 */
	UPPER, 
	/**
	 * Per la definizione dei Lower bound
	 */
	LOWER,
	/**
	 * Per definire variabili semicontinue
	 */
	SEMICONT
	
}
