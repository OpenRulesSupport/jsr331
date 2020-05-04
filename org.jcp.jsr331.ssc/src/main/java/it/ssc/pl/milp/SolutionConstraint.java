package it.ssc.pl.milp;

/**
 * Questa interfaccia permette di accedere ad un vincolo del problema di LP dove ad ogni variabile incognita &egrave;  stato assegnato 
 * il  valore ottimo. &Egrave; possibile ricavare il valore LHS del vincolo in funzione della soluzione ottima.   
 * Per componente LHS intendiamo l'equazione linerare che occupa la parte sinistra 
 * del vincolo. Dato un vincolo X1 + 3X2 &#x2265; 7, la componente 
 * LHS &egrave; data dala parte X1 + 3X2. 
 * 
 * @author Stefano Scarioli 
 * @version 1.0 
 * @see <a target="_new" href="http://www.ssclab.org">SSC Software www.sscLab.org</a>
 *
 */

public interface SolutionConstraint {
	
	/**
	 * 
	 * @return La componente fissa Rhs del vincolo 
	 */
	public double getRhs() ;
	/**
	 * 
	 * @return Il valore che la componente LHS assume sostituendo alle variabili 
	 *         incognite  la soluzione ottima determinata
	 * 
	 */
	public double getValue() ;
	
	/**
	 * 
	 * @return Il tipo di vincolo (GE,LE,EQ)
	 */
	public ConsType getRel() ;
	/**
	 * 
	 * @return Il nome del vincolo 
	 */
	public String getName() ;

}
