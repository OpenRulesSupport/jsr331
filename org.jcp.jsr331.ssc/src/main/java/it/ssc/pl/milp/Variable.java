package it.ssc.pl.milp;

/**
 * Questa interfaccia permette di accedere al valore  ottimo assunto dalla j-esima variabile 
 * del problema di LP (j=1..N).  Oltre al valore ottimo, &egrave; possibile recuperare anche 
 * altre caratteristiche .  
 * 
 * @author Stefano Scarioli
 * @version 1.0
 * @see <a target="_new" href="http://www.ssclab.org">SSC Software www.sscLab.org</a>
 *
 */

public interface Variable {
	/**
	 * 
	 * @return il nome della variabile 
	 */
	
	public String getName() ;
	/**
	 * 
	 * @return il tipo di variabile (intera , binaria , reale )
	 */

	public TYPE_VAR getType() ;
	/**
	 * 
	 * @return il suo upper bound se valorizzato (di default &egrave; + &infin;)
	 */

	public double getUpper() ;
	
	/**
	 * 
	 * @return il suo lower bound 
	 */

	public double getLower() ;
	
	/**
	 * 
	 * @return ritorna true se la variabile &egrave; libera. Per rendere una variabile libera 
	 * occorre che sia stato definito per la variabile o un upper  bound negativo, o un lower bound negativo o
	 * indefinito (- &infin;)
	 */

	public boolean isFree() ;
	
	/**
	 * 
	 * @return il valore ottimo assunto dalla variabile nell'ambito della soluzione ottima determinata
	 */
	
	public double getValue() ;
	/**
	 * Definisce il tipo di variabile : intera, binaria o reale  
	 * 
	 * @author Scarioli Stefano 
	 *
	 */
	
	public enum TYPE_VAR {
		REAL, 
		INTEGER, 
		BINARY}; 

}
