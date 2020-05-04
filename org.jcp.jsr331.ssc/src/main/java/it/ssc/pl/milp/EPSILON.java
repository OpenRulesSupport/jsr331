package it.ssc.pl.milp;

/**
 * Enumerazione per valori di epsilon che vanno da 1E-16 a 1E-4.  
 * 
 * @author Stefano Scarioli
 * @version 1.0
 * @see <a target="_new" href="http://www.ssclab.org">SSC Software www.sscLab.org</a> 
 *
 */

public enum EPSILON {
	_ZERO (0),
	_1E_M16 (1E-16),
	_1E_M15 (1E-15),
	_1E_M14 (1E-14),
	_1E_M13 (1E-13),
	_1E_M12 (1E-12),
	_1E_M11 (1E-11),
	_1E_M10 (1E-10),
	_1E_M9 (1E-9),
	_1E_M8 (1E-8),
	_1E_M7 (1E-7),
	_1E_M6 (1E-6),
	_1E_M5 (1E-5),
	_1E_M4 (1E-4);
	
	private double eps_value;
	
	private EPSILON(double epsilon) {
		this.eps_value=epsilon;
	}
	
	public double getValue() {
		return this.eps_value;
	}
}
