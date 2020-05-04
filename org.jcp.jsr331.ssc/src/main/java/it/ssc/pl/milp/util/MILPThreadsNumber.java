package it.ssc.pl.milp.util;

public enum MILPThreadsNumber {
	
	N_1 (1,1), 
	N_2 (2,1),  
	N_4 (4,2), 
	N_6 (6,3),  
	N_8 (8,4),  
	N_10 (10,5),  
	N_12 (12,6),  
	N_16 (16,8),  
	N_20 (20,10),  
	N_24 (24,12),  
	N_32 (32,16),
	N_64 (64,32),
	N_128 (128,64);  
	
	private int value;
	private int num;
	private MILPThreadsNumber(int threads,int num_root_lp) { 
		this.value=threads;
		this.num=num_root_lp;
	}
	
	public int getThread() {
		return this.value;
	}
	
	public int getNumLp() {
		return this.num;
	}
	
	public String toString() {
		return String.valueOf(this.value);
	}
}
