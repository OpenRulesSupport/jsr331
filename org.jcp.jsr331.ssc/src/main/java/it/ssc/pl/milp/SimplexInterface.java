package it.ssc.pl.milp;

import it.ssc.pl.milp.util.LPThreadsNumber;
import it.ssc.vector_spaces.MatrixException;

interface SimplexInterface {
	
	public SolutionType runPhaseOne()  throws Exception ;
	public long getNumIterationPhaseOne();
	public long getNumIterationPhaseTotal() ;
	public SolutionType runPhaseTwo() throws Exception;
	public double[] getFinalValuesBasis() ;
	public int[] getFinalBasis();
	public void setNumIterationMax(long num_iteration_max);
	public void setMilp(boolean isMilp) ;
	public void setThreadsNumber(LPThreadsNumber isParallelSimplex) ;
	
}
