package it.ssc.pl.milp;

import it.ssc.pl.milp.util.LPThreadsNumber;
import it.ssc.vector_spaces.Matrix;
import it.ssc.vector_spaces.MatrixException;
import it.ssc.vector_spaces.Vector;


 final class Simplex implements SimplexInterface {
	 
	
	private Matrix A;
	private Vector B;
	private Vector C;
	private int n; 
	private int m; 
	
	private EPSILON epsilon;
	private EPSILON cepsilon;
	
	private boolean isMilp=false;
	private LPThreadsNumber threadsNumber=LPThreadsNumber.N_1; 

	private SolutionType type_solution_phase_one=null;
	private SolutionType type_solution_phase_two=null;
	private Phase1 phase_one;
	private int basis[];
	private double values_basis[];
	private long num_iteration_max;
	private long num_iteration_phase_one;
	private long num_iteration_phase_total;
	
	public void setMilp(boolean isMilp) {
		this.isMilp = isMilp;
	}
	
	public Simplex(double[][] A, double[] B, double[] C,EPSILON epsilon, EPSILON cepsilon) throws SimplexException, MatrixException {
		this(new Matrix(A), new Vector(B), new Vector(C),epsilon,cepsilon);
	}
	
	public Simplex(Matrix A, Vector B, Vector C,EPSILON epsilon,EPSILON cepsilon) throws SimplexException {
		
		this.epsilon=epsilon;
		this.cepsilon=cepsilon;
		
		this.m=A.getNrow();
		this.n=A.getNcolumn();
		
		if(B.getTipo() == Vector.TYPE_VECTOR.ROW) {
			B.traspose();
		}
		if(C.getTipo() == Vector.TYPE_VECTOR.COLUMN) {
			C.traspose();
		}
		if(m!=B.lenght()) {
			throw new SimplexException("Il numero di righe di A (matrice dei coefficienti) non si adatta al numero di componenti del vettore B dei termini noti");
		}
		if(n!=C.lenght()) {
			throw new SimplexException("Il numero di colonne di A (matrice dei coefficienti) non si adatta al numero di componenti del vettore C della funzione obiettivo");
		}
		//matrice dei coefficienti dei vincoli
		this.A=A;
		//vettore dei termini noti
		this.B=B;
		//coefficienti della funzione obiettivo
		this.C=C;
	}
	
	public void setNumIterationMax(long num_iteration_max) {
		this.num_iteration_max = num_iteration_max;
	}
	
	public long getNumIterationPhaseOne() {
		return num_iteration_phase_one;
	}

	public long getNumIterationPhaseTotal() {
		return num_iteration_phase_total;
	}
	
	public void setThreadsNumber(LPThreadsNumber nthreads) {
		 threadsNumber=nthreads;
	}

	public SolutionType runPhaseOne()  throws SimplexException, MatrixException, InterruptedException {
		phase_one=new Phase1(A,B,epsilon,cepsilon);
		phase_one.setMilp(isMilp);
		phase_one.setThreadsNumber(threadsNumber);

		this.type_solution_phase_one=phase_one.resolve(this.num_iteration_max);
		this.num_iteration_phase_one=phase_one.getNumIteration(); //iterazioni fase 1 
		
		/*parte aggiunta 15/10/2018 per recuperare soluzione ammissibile*/
		this.basis=phase_one.getBasisClone();
		this.values_basis=phase_one.getValuesBases();
	
		/*fine parte aggiunta 15/10/2018 per recuperare soluzione ammissibile*/
		
		
		return this.type_solution_phase_one;
	}
	
	public SolutionType runPhaseTwo() throws Exception {
		
		if(this.type_solution_phase_one!=SolutionType.OPTIMUM) {
			throw new SimplexException("Attenzione, la regione ammissibile del problema e' vuota. Non esistono soluzioni !");
		}
		
		Matrix A_phase_one=phase_one.pulish(); //resettta TBEX=null e crea la nuova matrice per fase 2
		this.num_iteration_phase_one=phase_one.getNumIteration(); //N. iterazioni aggiornate fase 1 dopo pulizia (eventuali pivoting)
		
		Phase2 phase_two=new Phase2(A_phase_one,phase_one.getBasis(),C,num_iteration_phase_one,epsilon);
		phase_two.setThreadsNumber(threadsNumber);
		phase_one.resetTBEX();
		type_solution_phase_two=phase_two.resolve(this.num_iteration_max);
		
		this.basis=phase_two.getBasisClone();
		this.values_basis=phase_two.getValuesBases();
		
		this.num_iteration_phase_total=phase_two.getNumIteration();
		return type_solution_phase_two;
	}
	
	public double[] getFinalValuesBasis() {
		return this.values_basis;  //prima era clone
	}
	
	public int[] getFinalBasis() {
		return this.basis;  //prima era clone
	}
}
