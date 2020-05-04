package it.ssc.pl.milp.util;

public enum LPThreadsNumber {
	AUTO(0),
	N_1 (1), 
	N_2 (2),  
	N_4 (4), 
	N_6 (6),  
	N_8 (8),  
	N_10 (10),  
	N_12 (12),  
	N_16 (16),  
	N_20 (20),  
	N_24 (24),  
	N_32 (32),
	N_64 (64),
	N_128 (128);  
	
	private int value;
	
	private LPThreadsNumber(int threads) { 
		this.value=threads;
	}
	
	public int getThread() {
		return this.value;
	}
	
	public String toString() {
		return String.valueOf(this.value);
	}
}
