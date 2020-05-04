

package it.ssc.pl.milp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import it.ssc.context.Context;
import it.ssc.context.Session;
import it.ssc.context.exception.InvalidSessionException;
import it.ssc.datasource.DataSource;
import it.ssc.i18n.RB;
import it.ssc.log.SscLevel;
import it.ssc.log.SscLogger;
import it.ssc.pl.milp.util.A_DataMatrix;
import it.ssc.pl.milp.util.A_Matrix;
import it.ssc.pl.milp.util.LPThreadsNumber;
import it.ssc.ref.Input;



/**
 * Questa classe permette di eseguire e risolvere formulazioni di problemi di programmazione 
 * lineare. Il metodo utilizzato per la risoluzione di tali problemi di ottimizzazione &egrave; il 
 * metodo del simplesso
 * 
 * @author Stefano Scarioli
 * @version 3.0
 * @see <a target="_new" href="http://www.ssclab.org">SSC Software www.sscLab.org</a>
 */

public final class LP implements FormatTypeInput {
	
	public static double NaN=Double.NaN;
	private static final Logger logger=SscLogger.getLogger();
	private SolutionImpl solution_pl;
	private int num_max_iteration=10_000_000;
	private double[][] A;
	private double[]   B;
	private double[]   C;
	private Session session;
	private final boolean isMilp=false;
	private boolean isParallelSimplex=false;
	private boolean toCloseSessionInternal=true;
	private A_DataMatrix amatrix;
	private PersistensePLProblem persistencePl;
	private LPThreadsNumber threadsNumber=LPThreadsNumber.N_1;
	private boolean isStopPhase2=false;
	private EPSILON epsilon=EPSILON._1E_M10;
	private EPSILON cepsilon=EPSILON._1E_M8;
	
	{
		logger.log(Level.INFO,  "##############################################");
		logger.log(Level.INFO,  RB.getString("it.ssc.context.Session_Impl.msg0"));
		logger.log(Level.INFO,  "##############################################");
	}
	
	
	/*
	 * Costruttore di un oggetto LP per la risoluzione di problemi formulati in formato a disequazioni contenute in stringhe. 
	 * In questo formato le variabili devono necessariamente chiamarsi X<sub>j</sub>, con l'indice j che parte da 1. 
	 * 
	 * @param inequality  Lista dei vincoli di tipo (EQ,GE,LE) sotto forma di disequazioni contenute in stringhe
	 * @param fo Un oggetto LinearObjectiveFunction che rappresenta la funzione obiettivo
	 * @throws Exception Viene generata una eccezione se il problema non &egrave; formulato correttamente 
	 */
	
	/*
	
	public LP(ArrayList<String> inequality,LinearObjectiveFunction fo) throws Exception  { 
		
		if(inequality==null || inequality.isEmpty()) throw new LPException(RB.getString("it.ssc.pl.milp.LP.msg12"));
		this.session=Context.createNewSession();
		int dimension=fo.getC().length;
		ConstraintFromString cfs=new ConstraintFromString(dimension, inequality);
		ArrayList<Constraint> new_constraints=cfs.getConstraint();
		PLProblem pl_original=CreatePLProblem.create(fo,new_constraints,isMilp);
		createStandartProblem(pl_original);
	}
	*/
	
	
	/**
	 * 
	 * @param inequality Un ArrayList (di oggetti String) contenenti la formulazione del problema nel formato 
	 * a disequazioni
	 * @throws Exception Viene generata una eccezione se il problema non &egrave; formulato correttamente 
	 */
	public LP(ArrayList<String> inequality) throws Exception  { 
		if(inequality==null || inequality.isEmpty()) throw new LPException(RB.getString("it.ssc.pl.milp.LP.msg12"));
		this.session=Context.createNewSession();
		ScanLineFOFromString fo_fromm_string=new ScanLineFOFromString(inequality);
		LinearObjectiveFunction fo=fo_fromm_string.getFOFunction();
		ArrayList<String> nomi_var=fo_fromm_string.getListNomiVar();
		ScanConstraintFromString scan_const=new ScanConstraintFromString(inequality,nomi_var);
		ArrayList<InternalConstraint> list_constraints=scan_const.getConstraints();
		PLProblem pl_original=CreatePLProblem.create(fo,list_constraints,nomi_var,scan_const.getArraysProb(),isMilp); 
		createStandartProblem(pl_original);
	}
	
	
	
	/**
	 * 
	 * @param path Path dove &egrave; localizzato il file contenente il problema di PL formulato con il formato a 
	 * disequazioni
	 * @throws Exception Viene generata una eccezione se il problema non &egrave; formulato correttamente o se il file non esiste
	 */
	public LP(String path) throws Exception  { 
		BufferedReader br=null;
		ScanConstraintFromString scan_const;
		ArrayList<String> nomi_var;
		LinearObjectiveFunction fo;
		this.session=Context.createNewSession();
		try {
			File file =new File(path);
			br = new BufferedReader(new FileReader(file));
			ScanLineFOFromString fo_fromm_string=new ScanLineFOFromString(br);
			fo=fo_fromm_string.getFOFunction();
			nomi_var=fo_fromm_string.getListNomiVar();
			scan_const=new ScanConstraintFromString(br,nomi_var);
		}
		finally {
			if (br != null) br.close();
		}
		
		ArrayList<InternalConstraint> list_constraints=scan_const.getConstraints();
		PLProblem pl_original=CreatePLProblem.create(fo,list_constraints,nomi_var,scan_const.getArraysProb(),isMilp); 
		createStandartProblem(pl_original);
	}
	
	
	
	
	
	/*
	 * Costruttore di un oggetto LP per la risoluzione di problemi formulati in formato a disequazioni contenute in stringhe. 
	 * In questo formato le variabili devono necessariamente chiamarsi X<sub>j</sub>, con l'indice j che parte da 1. Il terzo parametro 
	 * &egrave; la lista dei vincoli che non sono di tipo EQ,LE,GE, ma UPPER e LOWER e vanno rappresentati come oggetti Constraint. 
	 * 
	 * @param inequality Lista dei vincoli di tipo EQ,GE,LE sotto forma di disequazioni contenute in stringhe
	 * @param constraints Lista dei vincoli di tipo UPPER e LOWER rappresentati come oggetti Constraint
	 * @param fo  Un oggetto LinearObjectiveFunction che rappresenta la funzione obiettivo
	 * @throws Exception  Viene generata una eccezione se il problema non &egrave; formulato correttamente
	 */
	
	/*
	public LP(ArrayList<String> inequality,ArrayList<Constraint> constraints,LinearObjectiveFunction fo) throws Exception  { 
		
		//qua sarebbe da implementare qualche controllino in piu'.
		if(inequality==null || inequality.isEmpty()) throw new LPException(RB.getString("it.ssc.pl.milp.LP.msg12"));
		if(constraints==null ) throw new LPException(RB.getString("it.ssc.pl.milp.LP.msg13"));
		this.session=Context.createNewSession();
		int dimension=fo.getC().length;
		ConstraintFromString cfs=new ConstraintFromString(dimension, inequality,constraints);
		ArrayList<Constraint> new_constraints=cfs.getConstraint();
		PLProblem pl_original=CreatePLProblem.create(fo,new_constraints,isMilp);
		createStandartProblem(pl_original);
	}
	*/
	
	
	/*
	 * Costruttore di un oggetto LP per la risoluzione di problemi formulati in formato a disequazioni contenute in stringhe. 
	 * In questo formato le variabili devono necessariamente chiamarsi X<sub>j</sub>, con l'indice j che parte da 1. Il terzo parametro 
	 * &egrave; la lista dei vincoli che non sono di tipo EQ,LE,GE, ma UPPER e LOWER e vanno rappresentati come oggetti Constraint. 
	 *  
	 * @param inequality
	 * @param constraints
	 * @param fo
	 * @throws Exception
	 */
	
	
	/*
	public LP(ArrayList<String> inequality,ListConstraints constraints,LinearObjectiveFunction fo) throws Exception  { 
		//qua sarebbe da implementare qualche controllino in piu'.
		if(inequality==null || inequality.isEmpty()) throw new LPException(RB.getString("it.ssc.pl.milp.LP.msg12"));
		this.session=Context.createNewSession();
		int dimension=fo.getC().length;
		ConstraintFromString cfs=new ConstraintFromString(dimension, inequality,constraints.getListConstraint());
		ArrayList<Constraint> new_constraints=cfs.getConstraint(); 
		PLProblem pl_original=CreatePLProblem.create(fo,new_constraints,isMilp);
		createStandartProblem(pl_original);
	}
	*/
	
	
	/**
	 * Costruttore di un oggetto LP per la risoluzione di problemi espressi in formato matriciale.
	 * 
	 * @param fo Un oggetto LinearObjectiveFunction che rappresenta la funzione obiettivo
	 * @param constraints La lista dei vincoli espressa come ArrayList di Oggetti Constraint
	 * @throws Exception Viene generata una eccezione se il problema non &egrave; formulato correttamente 
	 * 
	 */

		
	public LP(LinearObjectiveFunction fo,ArrayList<Constraint> constraints) throws Exception { 
		if(constraints==null ) throw new LPException(RB.getString("it.ssc.pl.milp.LP.msg13"));
		this.session=Context.createNewSession();
		PLProblem pl_original=CreatePLProblem.create(fo,constraints,isMilp);
		createStandartProblem(pl_original);
	}
	
	
	/**
	 * Costruttore di un oggetto LP per la risoluzione di problemi espressi in formato matriciale.
	 * 
	 * @param fo Un oggetto LinearObjectiveFunction che rappresenta la funzione obiettivo
	 * @param constraints La lista dei vincoli sotto forma di oggetto ListConstraints 
	 * @throws Exception Viene generata una eccezione se il problema non &egrave; formulato correttamente 
	 * 
	 */
	
	public LP(LinearObjectiveFunction fo,ListConstraints constraints) throws Exception  { 
		this.session=Context.createNewSession();
		PLProblem pl_original=CreatePLProblem.create(fo,constraints.getListConstraint(),isMilp);
		createStandartProblem(pl_original);
	}
	
	
	
	/**
	 * Costruttore di un oggetto LP per la risoluzione di problemi espressi in formato o sparso o a coefficienti.
	 * @param input Il problema formulato col formato sparso
	 * @param session Una sessione di lavoro SSC 
	 * @param format Costante per esprimere con quale formato &egrave; formulato il problema (FormatType.SPARSE o FormatType.COEFF)
	 * @throws Exception Viene generata una eccezione se il problema &egrave; formulato in modo non corretto
	 */
	
	public LP(Input input,Session session, FormatType format) throws Exception {
		this.session=session;
		this.toCloseSessionInternal=false;
		DataSource milp_data_source=session.createDataSource(input);
		PLProblem pl_original=null;
		if(format==FormatType.SPARSE) pl_original=CreatePLProblem.createFromSparse(milp_data_source,isMilp);
		else if(format==FormatType.COEFF) pl_original=CreatePLProblem.create(milp_data_source, isMilp);
		createStandartProblem(pl_original);
	}
	
	/**
	 * Costruttore di un oggetto LP per la risoluzione di problemi espressi in formato o sparso o a coefficienti.
	 * @param input Il problema formulato col formato sparso
	 * @param format Costante per esprimere con quale formato &egrave; formulato il problema (FormatType.SPARSE o FormatType.COEFF)
	 * @throws Exception Viene generata una eccezione se il problema &egrave; formulato in modo non corretto
	 */
	
	public LP(Input input,FormatType format) throws  Exception {
		
		this(input, Context.createNewSession(),format);
		this.toCloseSessionInternal=true;
	}
	
	/**
	 * Costruttore di un oggetto LP per la risoluzione di problemi espressi in formato a coefficienti.
	 * 
	 * @param input_natural Il problema formulato col formato a coefficienti
	 * @throws Exception Viene generata una eccezione se il problema &egrave; formulato in modo non corretto
	 */

	public LP(Input input_natural) throws Exception {
		
		this(input_natural, Context.createNewSession());
		this.toCloseSessionInternal=true;
		//logger.log(Level.INFO,RB.getString("it.ssc.pl.milp.LP.msg1")); 
		//session.close();
	}
	
	/**
	 * Costruttore di un oggetto LP per la risoluzione di problemi espressi in formato a coefficienti.
	 * 
	 * @param input_natural Il problema formulato col formato a coefficienti
	 * @param session Una sessione di lavoro SSC 
	 * @throws Exception Viene generata una eccezione se il problema &egrave; formulato in modo non corretto
	 */
	
	public LP(Input input_natural,Session session) throws Exception {
		
		this.session=session;
		this.toCloseSessionInternal=false;
		DataSource milp_data_source=session.createDataSource(input_natural);
		PLProblem pl_original=CreatePLProblem.create(milp_data_source, isMilp);
		createStandartProblem(pl_original); 
	}
	
	
	/**
	 * Questo metodo permette di settare il valore epsilon relativo alla tolleranza che interviene in diversi ambiti. &Egrave;  
	 * utilizzato nei seguenti casi : <br>
	 * 
	 * 1) Durante la fase uno, sia nella determinazione delle variabile entrante che in quella della variabile uscente con o senza regola di Bland; 
	 *    sia per determinare se la base &egrave; degenere. Viene anche utilizzata alla fine della fase uno : se esiste una variabile ausiliaria in base, 
	 *    epsilon viene utilizzato per determinare se &egrave; possibile eliminare le righe e le colonne di queste sulla tabella estesa.  <br>
	 * 2) Durante la fase due , sia nella determinazione delle variabile entrante che in quella della variabile uscente con o senza regola di BLand; 
	 *    sia per determinare se la base &egrave; degenere.   
	 * 
	 * @param epsilon Tolleranza utilizzata in diverse fasi del simplesso. Valore default 1-E10
	 */

	
	public void setEpsilon(EPSILON epsilon)   {  
		this.epsilon=epsilon;
	}
	
	/**
	 * Questo metodo permette di settare il valore epsilon relativo alla tolleranza nel 
	 * determinare se una soluzione ottima espressa dalla fase 1  &egrave; prossima o uguale a zero e quindi da origine a 
	 * soluzioni ammissibili per il problema iniziale. 
	 * 
	 * @param epsilon Tolleranza soluzione fase 1 rispetto allo zero. Valore default 1-E8
	 */
	
	public void setCEpsilon(EPSILON epsilon)  { 
		this.cepsilon=epsilon;
	}
	
		
	/**
	 * Questo metodo permette di limitare il numero massimo di iterazioni del simplesso (iterazioni fase 1 + iterazioni fase 2)
	 * 
	 * @param num_max_iteration Il numero di iterazioni che al massimo si vuole far eseguire. 
	 * Valore di default 10,000,000. 
	 * @throws LPException Se si imposta un numero errato (zero o negativo) 
	 */
	public void setNumMaxIteration(int num_max_iteration) throws LPException  { 
		if(num_max_iteration <= 0) throw new LPException("Il numero massimo di iterazioni deve essere un numero positivo");
		this.num_max_iteration=num_max_iteration;
	}
	
	/**
	 * Questo metodo ritorna il numero massimo di iterazioni del simplesso
	 * 
	 * @return Il numero massimo i iterazioni
	 */
	
	public int getNumMaxIteration()   { 
		return this.num_max_iteration;
	}
	
	private void createStandartProblem(PLProblem pl_original) throws InvalidSessionException, Exception {
		
		String path_work=session.getFactoryLibraries().getLibraryWork().getAbsolutePath();
		persistencePl=new PersistensePLProblem(pl_original,path_work);
		pl_original.standardize(); 
				
		B=pl_original.getVectorB();
		C=pl_original.getVectorC();
		//ho messo la creazione della matrice per ultima per svuotare gli internal constraint
		A=pl_original.getMatrixA();  
	
		amatrix=new A_DataMatrix(A,path_work);
	
		/*
		//printTableAm(Amatrix);
		
		System.out.println("--------A");
		printTableA(A);
		System.out.println("--------B");
		printTableV(B);
		System.out.println("--------C");
		printTableV(C);
		 * 
		 */
		
	}
	
	
	
	/**
	 * Esegue il simplesso (fase 1 + fase 2).
	 * 
	 * @return Il tipo di soluzione trovata 
	 * @throws Exception Se il processo di esecuzione genera un errore 
	 */
	
	public SolutionType resolve() throws Exception {
		
		logger.log(SscLevel.INFO,RB.format("it.ssc.pl.milp.LP.msg11")+threadsNumber.getThread());
		logger.log(Level.INFO,  "---------------------------------------------");
		
		SimplexInterface simplex =new Simplex(A, B, C,epsilon,cepsilon);
		simplex.setNumIterationMax(num_max_iteration);
		simplex.setThreadsNumber(threadsNumber) ;
		
		long start_simplex=System.currentTimeMillis();
		SolutionType type_solution=simplex.runPhaseOne();
		long end_phase_one=System.currentTimeMillis();
		long end_phase_two=end_phase_one;
		
		logger.log(SscLevel.TIME,RB.format("it.ssc.pl.milp.LP.msg2", RB.getHhMmSsMmm((end_phase_one-start_simplex))));
		logger.log(SscLevel.INFO,RB.getString("it.ssc.pl.milp.LP.msg3")+simplex.getNumIterationPhaseOne());  
		
		if(isStopPhase2 && type_solution==SolutionType.OPTIMUM) {
			type_solution=SolutionType.FEASIBLE;
			PLProblem pl_original=persistencePl.readObject();
			this.solution_pl=new SolutionImpl(type_solution,
											  pl_original,  //PRIMA PASSAVO UN CLONE ??? tolto .clone()
											  simplex.getFinalBasis(),
											  simplex.getFinalValuesBasis()
											 );
			
		}
		
		
		else if(type_solution==SolutionType.OPTIMUM) {
			type_solution =simplex.runPhaseTwo();
			end_phase_two=System.currentTimeMillis(); 
			logger.log(SscLevel.TIME,RB.format("it.ssc.pl.milp.LP.msg4",RB.getHhMmSsMmm(end_phase_two-end_phase_one)));
			logger.log(SscLevel.INFO,RB.getString("it.ssc.pl.milp.LP.msg5")+simplex.getNumIterationPhaseTotal());
			
			PLProblem pl_original=persistencePl.readObject();
			this.solution_pl=new SolutionImpl(type_solution,
											  pl_original,  //PRIMA PASSAVO UN CLONE ??? tolto .clone()
											  simplex.getFinalBasis(),
											  simplex.getFinalValuesBasis()
											 );
			
		}	
		logger.log(SscLevel.TIME,RB.format("it.ssc.pl.milp.LP.msg6",RB.getHhMmSsMmm(end_phase_two-start_simplex)));
		if(type_solution==SolutionType.FEASIBLE || type_solution==SolutionType.OPTIMUM) {
			loggerAccurancy( amatrix, B, simplex.getFinalBasis(),simplex.getFinalValuesBasis(),isStopPhase2);
		}
		closeAfterResolve() ;
		return type_solution;

	}
	
	
	
	/**
	 * Questo metodo ritorna la matrice A ottenuta in seguito al processo di riduzione in 
	 * forma standart (max z , Ax + s=b, x &ge; 0, b &ge; 0)  del problema di programmazione lineare di partenza.
	 * 
	 * @return La matrice dei coefficienti A 
	 * @throws SimplexException 
	 * @throws IOException se il problema non &egrave; stato ridotto in forma standart
	 */
	public double[][] getStandartMatrixA() throws SimplexException, IOException {
		if(amatrix==null) throw new SimplexException(RB.getString("it.ssc.pl.milp.LP.msg9"));
		return amatrix.getMatrix();
	}

	/**
	 * Questo metodo ritorna il vettore b dei valori rhs ottenuto in seguito al processo di riduzione in 
	 * forma standart (max z , Ax+s=b, x &ge; 0, b &ge; 0)  del problema di programmazione lineare di partenza.
	 * 
	 * @return Il vettore dei coefficienti RHS 
	 */
	
	public double[] getStandartVectorB() {
		return B.clone();
	}

	/**
	 * Questo metodo ritorna il vettore c dei coefficienti della f.o. in seguito al processo di riduzione 
	 * in forma standart (max z , Ax+s=b, x &ge; 0, b &ge; 0) del problema di programmazione lineare di partenza.
	 * 
	 * @return Il vettore c dei coefficienti della f.o.
	 */
	public double[] getStandartVectorC() {
		return C.clone();
	}

	/**
	 * Se il problema ammette soluzione ottima , questo metodo ritorna tale soluzione ottima sotto forma di oggetto della 
	 * classe Solution
	 * @return La soluzione ottima del problema 
	 * @throws SimplexException Se la soluzione ottima non &egrave; presente 
	 */
	
	public Solution getSolution() throws SimplexException  {
		if(this.solution_pl==null)  throw new SimplexException(RB.getString("it.ssc.pl.milp.LP.msg10"));
		return this.solution_pl;
	}
	
	/**
	 * 
	 * @return vero se la parallelizzazione del simplesso &egrave; attiva. 
	 */
	
	public boolean isParallelSimplex() {
		return isParallelSimplex;
	}
	/**
	 * Se il numero di core fisici dell'host su cui viene eseguito SSc  &egrave; maggiore di 4 , pu&ograve; essere 
	 * migliorata la performance del simplesso facendo eseguire i processi di ottimizzazione in parallelo su pi &ugrave; 
	 * thread. 
	 * 
	 * @param isParallelSimplex True per attivare la parallelizzazione
	 */
	
	public void setParallelSimplex(boolean isParallelSimplex) {
		this.isParallelSimplex = isParallelSimplex;
		if(isParallelSimplex==true) threadsNumber=LPThreadsNumber.AUTO;
	}
	
	/**
	 * 
	 * @return il numero di Thread utilizzati nell'esecuzione. Se il valore &egrave; AUTO  &egrave; il sistema a 
	 * decidere il numero di 
	 * Thread da utilizzare. 
	 */
	
	
	public LPThreadsNumber getThreadsNumber() {
		return threadsNumber;
	}
	
	
	/**
	 * 
	 * @param threadsNumber Imposta il numero di Thread da utilizzare nell'esecuzione. 
	 * Se il valore impostato &egrave; AUTO  &egrave; il sistema a decidere il numero di 
	 * Thread da utilizzare.
	 */

	public void setThreadsNumber(LPThreadsNumber threadsNumber) {
		isParallelSimplex=true;
		this.threadsNumber = threadsNumber;
	}
	
	/**
	 * Restituisce true se se &egrave; impostato l'esecuzione della sola fase 1. 
	 * @return
	 */
	
	public boolean isJustTakeFeasibleSolution() {
		return isStopPhase2;
	}

	/**
	 * Impostando a true permette di interrompere il simplesso alla fine della fase 1, in modo da determinare 
	 * non una soluzione ottima ma solamente una soluzione ammissibile del problema. 
	 * @param isStopPhase2 true per interrompere il simplesso prima della fase 2. 
	 */
	public void setJustTakeFeasibleSolution(boolean isStopPhase2) {
		this.isStopPhase2 = isStopPhase2;
	}

	
	private void loggerAccurancy(A_DataMatrix matrix, double[] B,int basis[],double values[],boolean solo_ammissibile) throws IOException {
		double sum_b=0;
		double best_error=0;
		double[] array;
		int nCols=matrix.getnCol();
		double array_solutions[]=getArraySolution(nCols, basis,values);
		for(int i=0;i<matrix.getnRow();i++) {
			double b_=0;
			array=matrix.readArray(i);
			for(int j=0;j<nCols;j++) {
				b_=b_+ array[j]* array_solutions[j];
			}
			if(best_error < Math.abs(b_- B[i])) best_error=Math.abs(b_- B[i]);
			sum_b+=Math.abs(b_- B[i]);
		}
		double errore=sum_b/matrix.getnRow();
		if(solo_ammissibile) logger.log(Level.INFO,RB.getString("it.ssc.pl.milp.LP.msg7bis"));
		else logger.log(Level.INFO,RB.getString("it.ssc.pl.milp.LP.msg7"));
		logger.log(Level.INFO,  "---------------------------------------------");
		if(solo_ammissibile) logger.log(Level.INFO,  RB.getString("it.ssc.pl.milp.LP.msg8bis"));
		else logger.log(Level.INFO,  RB.getString("it.ssc.pl.milp.LP.msg8"));
		logger.log(Level.INFO,  RB.getString("it.ssc.pl.milp.LP.msg8b"));
		logger.log(Level.INFO,  RB.getString("it.ssc.pl.milp.LP.msg8c"));
		logger.log(Level.INFO,  RB.getString("it.ssc.pl.milp.LP.msg8d")+errore);
		logger.log(Level.INFO,  RB.getString("it.ssc.pl.milp.LP.msg8e")+best_error);
		logger.log(Level.INFO,  "---------------------------------------------");
		
		
	}
	
	private double[]  getArraySolution( int dim, int basis[],double value_bases[]) {
		double[] solutions=new double[dim];
		for (int index_var=0;index_var<dim;index_var++) 
			for (int i = 0; i < basis.length; i++) {
				if(index_var== basis[i]) {
					solutions[index_var]= value_bases[i];
				}
			}
		return solutions;
	}
	
	
	private void closeAfterResolve() throws Exception {
		if(toCloseSessionInternal) session.close();
		amatrix.close();
		amatrix=null;
		session=null;
	}
	
	@SuppressWarnings("unused")
	private void printTableAm(A_Matrix tabella) throws IOException {
		for(int _i=0;_i<tabella.getnRow();_i++) {
			System.out.println("");
			for(int _j=0;_j<tabella.getnCol();_j++) {
				double val=tabella.readArray(_i)[_j];
				System.out.printf("\t : %7.14f",val);
			}
		}
		System.out.println("");
	}
	
	@SuppressWarnings("unused")
	private void printTableA(double[][] tabella) {
		for(int _i=0;_i<tabella.length;_i++) {
			System.out.println("");
			for(int _j=0;_j<tabella[0].length;_j++) {
				double val=tabella[_i][_j];
				System.out.printf("\t : %7.14f",val);
			}
		}
		System.out.println("");
	}
	
	@SuppressWarnings("unused")
	private void printTableV(double[] vector) {
		for(int _j=0;_j<vector.length;_j++) {
			double val=vector[_j];
			System.out.printf("\t : %7.14f",val);
		}
		
		System.out.println("");
	}
}
/*
 * */
