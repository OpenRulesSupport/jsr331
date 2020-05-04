package it.ssc.pl.milp;


interface ObjectiveFunction {
	
	public enum TARGET_FO {MAX, MIN}; 
	
	public double getCj(int j) ;
	
	public TARGET_FO getType() ;
		
	public ObjectiveFunctionImpl clone() ;


}
