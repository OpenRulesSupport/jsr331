package it.ssc.pl.milp;

class ArrayProblem {
	 Double array_upper[];
	 Double array_lower[];
	 double array_sec[];
	 double array_bin[];
	 double array_int[];
	 boolean isMilp;
	
	ArrayProblem(int dim) {
		 array_upper=new Double[dim];
		 array_lower=new Double[dim];
		 array_sec=new double[dim];
		 array_bin=new double[dim];
		 array_int=new double[dim];
		 isMilp=false;
	}
}
