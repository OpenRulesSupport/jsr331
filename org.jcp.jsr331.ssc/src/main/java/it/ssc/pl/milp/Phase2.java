package it.ssc.pl.milp;


import java.util.logging.Logger;
import it.ssc.log.SscLevel;
import it.ssc.log.SscLogger;
import it.ssc.pl.milp.util.LPThreadsNumber;
import it.ssc.vector_spaces.Matrix;
import it.ssc.vector_spaces.Vector;

/**
 * @author Scarioli
 */

 final class Phase2 extends Phase {
	
	private final int n;
	private final int m;
	private static final Logger logger=SscLogger.getLogger();
	
	
	public Phase2(Matrix matrixPhase1, int[] basis_p1, Vector C1, long prec_num_iteration,EPSILON epsilon) 	throws SimplexException {
		
		super(matrixPhase1.getNrow()-1,matrixPhase1.getNcolumn()-1,epsilon);
		this.iteration=prec_num_iteration;
		this.m = matrixPhase1.getNrow() - 1;
		this.n = matrixPhase1.getNcolumn() - 1;

		this.basis = basis_p1;
		
		double[] C = updateC(matrixPhase1.getMatrix(),C1);
		updateZ(matrixPhase1.getMatrix(),C1);
		
		this.TBEX=this.updateTableExtended(matrixPhase1.getMatrix(),C);
		
	}
	
	public SolutionType resolve(long num_iteration) throws InterruptedException  {
			

		int var_incoming=0,row_var_outgoing=0;		
		while(this.iteration < num_iteration) {
			
			/*
			logger.log(SscLevel.NOTE,"seconda TABELLA:");
			printTable2();
			printBases();
			*/
			
			if(isBaseDegenerate())  var_incoming = test_var_incoming_bland();
			else var_incoming = test_var_incoming();
			
			if (var_incoming == -1) {	
				return SolutionType.OPTIMUM; 
			} 
			
			if ((row_var_outgoing = test_var_outgoing(var_incoming)) == -1) { 
				return SolutionType.ILLIMITATUM;
			}

			if(threadsNumber==LPThreadsNumber.AUTO) pivotingParallel(row_var_outgoing,var_incoming);
			else if(threadsNumber!=LPThreadsNumber.N_1) pivotingParallelCyclic(row_var_outgoing,var_incoming);
			else pivoting(row_var_outgoing,var_incoming);
			
			setBases(row_var_outgoing,var_incoming);
			this.iteration++;
		}
		
		logger.log(SscLevel.WARNING,"Raggiunto il massimo numero di iterazioni "+(num_iteration));
		return SolutionType.MAX_ITERATIUM;
	}
	
	
	private void updateZ(double[][] matrixPhase1,Vector C)  {
		int dim_c=C.lenght();
		double somma_valori=0.0;
		for(int _j=0;_j<dim_c;_j++) {
			int row_bases=isIndexInBase(_j);
			if(row_bases > -1) {
				double value_c=C.getCell(_j);
				double value =matrixPhase1[row_bases][ n] * value_c;
				somma_valori=somma_valori- value;
			}
		}
		//matrixPhase1.setCell(m, n, somma_valori);
		matrixPhase1[m][n]= somma_valori;
	}
	
	
	private double[]  updateC(double[][] matrixPhase1,Vector C1) { 
		int dim_c=C1.lenght();
		double[] somma_valori_C;
		
		//Vector new_C=new Vector(dim_c,Vector.TYPE_VECTOR.ROW);  
		double[] new_C=new double[dim_c];
		double value;
		
		for(int _j=0;_j<dim_c;_j++) {
			int row_bases=isIndexInBase(_j);
			double value_c=C1.getCell(_j);
			somma_valori_C= new double[dim_c];
			
			if(row_bases > -1) {
				if( !(value_c==0.0)) {           //ATTENZIONEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE
					for(int _k=0;_k<dim_c ;_k++) {
						if((isIndexInBase(_k) < 0)) {   //??????????????????????????????????????????????
							//double value =matrixPhase1.getCell(row_bases, _k) * value_c;
							somma_valori_C[_k] = matrixPhase1[row_bases][_k] * value_c * -1.0;
						}
					}
				}
			}
			//se non e' in base
			else {
				somma_valori_C[ _j]= value_c;
			}
			
			for(int _k=0;_k<dim_c;_k++) {
				value=new_C[_k];
				value=value + somma_valori_C[_k];
				new_C[_k]=value;
			}
		}
		return new_C;
	}
	
	private int  isIndexInBase(int index) {
		for (int i = 0; i < basis.length; i++) {
			if(index== basis[i]) {
				return i;
			}
		}
		return -1;
	}

	private double[][] updateTableExtended(double[][] matrixPhase1, double[] C) {
		for (int j = 0; j < n; j++) {
			matrixPhase1[m][j]= C[j]; 
		}
		return matrixPhase1;
	}

	
	public double[] getValuesBases() {
		double[] values=new double[this.m];
		for(int _a=0;_a <this.m;_a++) {
			values[_a]= TBEX[_a][ this.n];
			//System.out.println("VALUESSSE:"+values[_a]);
		}
		return values;
	}
}
