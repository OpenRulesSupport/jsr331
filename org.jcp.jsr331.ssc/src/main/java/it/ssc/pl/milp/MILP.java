package it.ssc.pl.milp;

import  it.ssc.i18n.RB;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.concurrent.CyclicBarrier;
import java.util.logging.Level;
import java.util.logging.Logger;
import it.ssc.context.Context;
import it.ssc.context.Session;
import it.ssc.log.SscLevel;
import it.ssc.log.SscLogger;
import it.ssc.pl.milp.ObjectiveFunction.TARGET_FO;
import it.ssc.pl.milp.util.MILPThreadsNumber;
import it.ssc.ref.Input;
import it.ssc.step.parallel.Task;



/**
 * Questa classe permette di eseguire e risolvere formulazioni di problemi di programmazione 
 * lineare misto intera , binaria o semicontinua. Il metodo utilizzato per la risoluzione di tali problemi di ottimizzazione 
 * &egrave; il metodo del simplesso abbinato a quello del Branch and Bound. 
 * 
 * @author Stefano Scarioli
 * @version 3.0
 * @see <a target="_new" href="http://www.ssclab.org">SSC Software www.sscLab.org</a> 
 */

public final class MILP implements FormatTypeInput {
	
	public static double NaN=Double.NaN;
	
	private static final EPSILON epsilon=EPSILON._1E_M10;
	private static final  EPSILON iepsilon=EPSILON._1E_M10; 
	private static final  EPSILON cepsilon=EPSILON._1E_M8;
	private static final Logger logger=SscLogger.getLogger(); 
	
	private MilpManager milp_initiale;
	private LB lb;
	private int num_max_simplex  =1_000_000;
	private int num_max_iteration=10_000_000;
	private boolean isJustTakeFeasibleSolution;
	private MILPThreadsNumber threadNumber=MILPThreadsNumber.N_1;
	private double stepDisadvantage=0.0;

	{
		lb=new LB();
		logger.log(Level.INFO,  "##############################################");
		logger.log(Level.INFO,  RB.getString("it.ssc.context.Session_Impl.msg0"));
		logger.log(Level.INFO,  "##############################################");
	}
	
	/*
	 * Costruttore di un oggetto MILP per la risoluzione di problemi formulati in formato a disequazioni contenute in stringhe. 
	 * In questo formato le variabili devono necessariamente chiamarsi X<sub>j</sub>, con l'indice j che parte da 1. Il terzo parametro 
	 * &egrave; la lista dei vincoli che non sono di tipo EQ,LE,GE, ma UPPER e LOWER e vanno rappresentati come oggetti Constraint. 
	 * 
	 * @param inequality Lista dei vincoli di tipo EQ, GE, LE sotto forma di disequazioni contenute in stringhe
	 * @param constraints Lista dei vincoli di tipo UPPER e LOWER rappresentati come oggetti Constraint
	 * @param fo  Un oggetto LinearObjectiveFunction che rappresenta la funzione obiettivo
	 * @throws Exception  Viene generata una eccezione se il problema non &egrave; formulato correttamente
	 */
	
	/*
	
	public MILP(ArrayList<String> inequality,ArrayList<Constraint> constraints,LinearObjectiveFunction fo) throws Exception  { 
		if(constraints==null || constraints.isEmpty()) throw new LPException(RB.getString("it.ssc.pl.milp.MILP.msg5"));
		int dimension=fo.getC().length;
		ConstraintFromString cfs=new ConstraintFromString(dimension, inequality,constraints);
		ArrayList<Constraint> new_constraints=cfs.getConstraint();
		this.milp_initiale = new MilpManager(fo, new_constraints);
		setAllEpsilon();
	}
	*/
	
	/*
	 * Costruttore di un oggetto MILP per la risoluzione di problemi formulati in formato a disequazioni contenute in stringhe. 
	 * In questo formato le variabili devono necessariamente chiamarsi X<sub>j</sub>, con l'indice j che parte da 1. 
	 * 
	 * @param inequality  Lista dei vincoli di tipo (EQ,GE,LE) sotto forma di disequazioni contenute in stringhe
	 * @param fo Un oggetto LinearObjectiveFunction che rappresenta la funzione obiettivo
	 * @throws Exception Viene generata una eccezione se il problema non &egrave; formulato correttamente 
	 */
	
	/*
	public MILP(ArrayList<String> inequality,LinearObjectiveFunction fo) throws Exception  { 
		if(inequality==null || inequality.isEmpty()) throw new LPException(RB.getString("it.ssc.pl.milp.MILP.msg6"));
		int dimension=fo.getC().length;
		ConstraintFromString cfs=new ConstraintFromString(dimension, inequality);
		ArrayList<Constraint> new_constraints=cfs.getConstraint();
		this.milp_initiale = new MilpManager(fo, new_constraints);
		setAllEpsilon();
	}
	*/
	
	
	
	/**
	 * 
	 * Costruttore di un oggetto MILP per la risoluzione di problemi formulati in formato a disequazioni contenuto in 
	 * ArrayList di Stringhe.  
	 * 
	 * @param inequality
	 * @throws Exception
	 */
	public MILP(ArrayList<String> inequality) throws Exception  { 
		if(inequality==null || inequality.isEmpty()) throw new LPException(RB.getString("it.ssc.pl.milp.LP.msg12"));
		ScanLineFOFromString fo_fromm_string=new ScanLineFOFromString(inequality);
		LinearObjectiveFunction fo=fo_fromm_string.getFOFunction();
		ArrayList<String> nomi_var=fo_fromm_string.getListNomiVar();
		ScanConstraintFromString scan_const=new ScanConstraintFromString(inequality,nomi_var);
		ArrayList<InternalConstraint> list_constraints=scan_const.getConstraints();
		this.milp_initiale = new MilpManager(fo,list_constraints,nomi_var,scan_const.getArraysProb());
		setAllEpsilon();
	}
	
	
	/**
	 *  Costruttore di un oggetto MILP per la risoluzione di problemi formulati in formato a disequazioni contenuto in 
	 * un file esterno.  
	 * 
	 * @param path Path del file contenente il problema in formato a disequazioni
	 * @throws Exception se il problema non &egrave; formulato correttamente
	 */
	
	
	public MILP(String path) throws Exception  { 
		
		BufferedReader br=null;
		ScanConstraintFromString scan_const;
		ArrayList<String> nomi_var;
		LinearObjectiveFunction fo;
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
		this.milp_initiale = new MilpManager(fo,list_constraints,nomi_var,scan_const.getArraysProb());
		setAllEpsilon();
	}
	
	
	
	/**
	 * Costruttore di un oggetto MILP per la risoluzione di problemi espressi in formato matriciale.
	 * 
	 * @param fo Un oggetto LinearObjectiveFunction che rappresenta la funzione obiettivo
	 * @param constraints La lista dei vincoli 
	 * @throws Exception Viene generata una eccezione se il problema non &egrave; formulato correttamente 
	 * 
	 */
	
	public MILP(LinearObjectiveFunction fo,ArrayList<Constraint> constraints) throws  Exception {
		this.milp_initiale = new MilpManager(fo, constraints);
		setAllEpsilon();
	}
	

	/**
	 * Costruttore di un oggetto MILP per la risoluzione di problemi espressi in formato a coefficienti.
	 * 
	 * @param input Il problema formulato col formato a coefficenti
	 * @throws Exception Viene generata una eccezione se il problema &egrave; formulato in modo non corretto
	 */
	
	public MILP(Input input) throws  Exception {
		Session session=Context.createNewSession();
		this.milp_initiale = new MilpManager(input, session); 
		logger.log(Level.INFO,RB.getString("it.ssc.pl.milp.MILP.msg1"));
		session.close();
		setAllEpsilon();
	}
	
	/**
	 * Costruttore di un oggetto MILP per la risoluzione di problemi espressi in formato a coefficienti.
	 * 
	 * @param input Il problema formulato col formato a coefficenti
	 * @param session Una sessione di lavoro SSC 
	 * @throws Exception Viene generata una eccezione se il problema &egrave; formulato in modo non corretto
	 */
	
	public MILP(Input input,Session session) throws  Exception {
		this.milp_initiale = new MilpManager(input, session); 
		setAllEpsilon();
	}
	
	
	/**
	 * Costruttore di un oggetto MILP per la risoluzione di problemi espressi o in formato sparso o a coefficienti.
	 * 
	 * @param input_sparse Il problema formulato col formato sparso o a coefficienti
	 * @param session Una sessione di lavoro SSC 
	 * @param format Il tipo di formato utilizzato (FormatType.SPARSE o FormatType.COEFF)
	 * @throws Exception Viene generata una eccezione se il problema &egrave; formulato in modo non corretto
	 */
	
	public MILP(Input input_sparse,Session session, FormatType format) throws Exception {
		this.milp_initiale = new MilpManager(input_sparse, session,format); 
		setAllEpsilon();
	}
	
	/**
	 * Costruttore di un oggetto MILP per la risoluzione di problemi espressi in formato sparso o a coefficienti.
	 * 
	 * @param input_sparse Il problema formulato col formato sparso
	 * @param format Il tipo di formato utilizzato (FormatType.SPARSE o FormatType.COEFF)
	 * @throws Exception Viene generata una eccezione se il problema &egrave; formulato in modo non corretto
	 */
	
	public MILP(Input input_sparse,FormatType format) throws  Exception {
		Session session=Context.createNewSession();
		this.milp_initiale = new MilpManager(input_sparse, session,format);
		logger.log(Level.INFO,RB.getString("it.ssc.pl.milp.MILP.msg1"));
		session.close();
		setAllEpsilon();
	}
	
	/**
	 * 
	 * @return il numero massimo di iterazioni eseguibile da ciascun simplesso
	 */
	

	public int getNumMaxIterationForSingleSimplex() {
		return num_max_iteration;
	}
	
	/**
	 * Metodo per settare il nunmero di iterazioni di ogni singolo simplesso 
	 * 
	 * @param num_max_iteration Il numero massimo di iterazioni eseguibili da ciascun simplesso
	 * @throws SimplexException Se si genera un errore durante il processo 
	 */

	public void setNumMaxIterationForSingleSimplex(int num_max_iteration) throws SimplexException {
		if(num_max_iteration <= 0) throw new SimplexException(RB.getString("it.ssc.pl.milp.MILP.msg7"));
		this.num_max_iteration = num_max_iteration;
	}
	
	/**
	 * 
	 * @return il numero massimo di simplessi eseguibili nel procedimento del branch and bound 
	 */

	public int getNumMaxSimplexs() {
		return num_max_simplex;
	}
	
	/**
	 * Metodo per impostare il nunmero massimo di simplessi
	 * 
	 * @param num_max_simplex il numero massimo di simplessi eseguibili nel procedimento del branch and bound 
	 */

	public void setNumMaxSimplexs(int num_max_simplex) {
		this.num_max_simplex = num_max_simplex;
	}

	private void setAllEpsilon() {
		this.milp_initiale.setEpsilon(epsilon);
		this.milp_initiale.setIEpsilon(iepsilon); 
		this.milp_initiale.setCEpsilon(cepsilon); 
	}
	
	/**
	 * Questo metodo permette di settare il valore epsilon relativo alla tolleranza che interviene in diversi ambiti del simplesso. &Egrave;  
	 * utilizzato nei seguenti casi : <br>
	 * 
	 * 1) Durante la fase uno, sia nella determinazione delle variabile entrante che in quella della variabile uscente con o senza regola di Bland. 
	 *    Sia per determinare se la base &egrave; degenere. Viene anche utilizzata alla fine della fase uno : se esiste una variabile ausiliaria in base, 
	 *    epsilon viene utilizzato per determinare se &egrave; possibile eliminare le righe e le colonne di queste sulla tabella estesa.  <br>
	 * 2) Durante la fase due , sia nella determinazione delle variabile entrante che in quella della variabile uscente con o senza regola di BLand. 
	 *    Sia per determinare se la base &egrave; degenere.   
	 * 
	 * @param epsilon Toleranza utilizzata in diverse fasi del simplesso. Valore default 1-E10
	 */
	
	public void setEpsilon(EPSILON epsilon) {
		this.milp_initiale.setEpsilon(epsilon);
	}
	
	/**
	 * Questo metodo permette di settare il valore epsilon relativo alla tolleranza per 
	 * determinare se una soluzione ottima della fase 1 del simplesso &egrave; prossima o uguale a zero e quindi da origine a 
	 * soluzioni ammissibili per il problema . 
	 * 
	 * @param cepsilon Tolleranza soluzione fase 1 rispetto allo zero. Valore default 1-E8
	 */
	
	public void setCEpsilon(EPSILON cepsilon) {
		this.milp_initiale.setCEpsilon(cepsilon);
	}
	
	/**
	 * Questo metodo permette di settare il valore epsilon relativo alla tolleranza per 
	 * determinare se un numero deve essere considerato intero o no. Questo controllo 
	 * avviene quando alla fine del simplesso si valuta se la soluzione trovata soddisfa la condizione di interezza 
	 * sulle variabili che devono esserlo.  Sia x un numero e sia Int(x) l'intero pi&ugrave; vicino a x, se 
	 *  | Int(x) - x | &lt; epsilon -&gt; x &#x2208; &#x2124;
	 * 
	 * @param iepsilon Tolleranza per considerare un numero come intero. Valore default 1-E10
	 */
	
	public void setIEpsilon(EPSILON iepsilon) {
		this.milp_initiale.setIEpsilon(iepsilon); 
	}
	
	/**
	 * Esegue il branch and bound.
	 * 
	 * @return Il tipo di soluzione trovata 
	 * @throws Exception Se il processo di esecuzione genera un errore 
	 */
	
	
	public SolutionType resolve() throws Exception {
		
		logger.log(SscLevel.INFO,RB.format("it.ssc.pl.milp.MILP.msg10")+threadNumber.getThread());
		
		//if(threadNumber==MILPThreadsNumber.N_1) return resolveSingleThread();
		// else 
		 return resolveMultiThread() ;
	}
	
	/*
	private SolutionType resolveSingleThread() throws Exception {
		
		int num_simplex_resolved=1;
		long start=System.currentTimeMillis();
		
		SolutionType type_solution=SolutionType.VUOTUM;
		ArrayList<MilpManager> listMangerMilpToRun=null;
		
		MilpManager milp_current=milp_initiale;    //INIZIALMENTE E' QUELLO INIZIALE (!)
		milp_current.setMaxIteration(num_max_iteration);
		
		SolutionType type_solution_initial=milp_current.resolve(); 
		TARGET_FO target=milp_initiale.getTargetFoOriginal();
		TreeV3 tree=new TreeV3(target);
		
		if(target==TARGET_FO.MAX)  lb.value=Double.NEGATIVE_INFINITY;    //per il max 
		if(target==TARGET_FO.MIN)  lb.value=Double.POSITIVE_INFINITY;    //per il min 
		
		if(type_solution_initial==SolutionType.OPTIMUM) {
			tree.addNode(milp_current);
		}	 
		
		while(!tree.isEmpty()) {
			
			milp_current=tree.getMilpBestUP();  
			if(milp_current.isSolutionIntegerAmmisible() && milp_current.isProblemSemiContinusAmmisible()) {
				milp_current.setIntegerIfOptimal();
							
				if(  (target==TARGET_FO.MAX && lb.value < milp_current.getOptimumValue())     //max 
				  || (target==TARGET_FO.MIN && lb.value > milp_current.getOptimumValue())) {  //questo vale per il min
					
					lb.value= milp_current.getOptimumValue();
					lb.milp=milp_current;
					type_solution=SolutionType.OPTIMUM; 
				}
			}
			else {
				listMangerMilpToRun = new ArrayList<>();
				MilpManager.populateArrayListBySeparation(listMangerMilpToRun,milp_current);
				
				for(MilpManager milp:listMangerMilpToRun) {
					milp.resolve();
					if(milp.getSolutionType()==SolutionType.OPTIMUM) {
						tree.addNode(milp);
					}
					num_simplex_resolved+=1;	
				}
				tree.deleteNodeWhitUPnotValide(lb.value); 
			}
			
			if(num_simplex_resolved >= num_max_simplex) { 
				logger.log(SscLevel.WARNING,RB.format("it.ssc.pl.milp.MILP.msg8")+num_max_simplex);
				logger.log(SscLevel.NOTE,RB.format("it.ssc.pl.milp.MILP.msg9"));
				return SolutionType.MAX_NUM_SIMPLEX;
			}
		}
		
		long end=System.currentTimeMillis();
		logger.log(SscLevel.TIME,RB.format("it.ssc.pl.milp.MILP.msg2",RB.getHhMmSsMmm((end-start))));
		logger.log(SscLevel.INFO,RB.getString("it.ssc.pl.milp.MILP.msg3")+num_simplex_resolved);
		if(type_solution==SolutionType.OPTIMUM) logger.log(SscLevel.INFO,RB.getString("it.ssc.pl.milp.MILP.msg4"));
		return type_solution;
	}
	*/
	
	
	private SolutionType resolveMultiThread() throws Exception {
		
		int num_simplex_resolved=1;
		long start=System.currentTimeMillis();
		SolutionType type_solution=SolutionType.VUOTUM;
		MilpManager milp_current=milp_initiale;    
		milp_current.setMaxIteration(num_max_iteration);
		
		if(!milp_current.existVarToBeIntegerOrSemicon()) {
			throw new LPException(RB.format("it.ssc.pl.milp.MILP.msg12"));
		}
		
		SolutionType type_solution_initial=milp_current.resolve(); 
		
		//lo chiamo dopo resolve perche' il target lo recupera dal milp_zero, che e' valorizzato dopo il resolve
		TARGET_FO target=milp_current.getTargetFoOriginal();
		TreeV3 tree=new TreeV3(target);
		
		if(target==TARGET_FO.MAX)  lb.value=Double.NEGATIVE_INFINITY;    //per il max 
		if(target==TARGET_FO.MIN)  lb.value=Double.POSITIVE_INFINITY;    //per il min 
		//System.out.println("lb:"+milp_current.getOptimumValue());
		
		if(type_solution_initial==SolutionType.OPTIMUM) {
			if(milp_current.isSolutionIntegerAmmisible() && milp_current.isProblemSemiContinusAmmisible()) {
				//System.out.println("intera:"+milp.getOptimumValue());
				if(  (target==TARGET_FO.MAX && lb.value < milp_current.getOptimumValue())     //max 
				  || (target==TARGET_FO.MIN && lb.value > milp_current.getOptimumValue())) {  //questo vale per il min

					milp_current.setIntegerIfOptimal();
					lb.value= milp_current.getOptimumValue();
					lb.milp=milp_current;
					type_solution=SolutionType.OPTIMUM; 
					//se devo cercare solo una soluzione ammissibile , ma non ottima
					if(isJustTakeFeasibleSolution) {
						type_solution=SolutionType.FEASIBLE;
					}
				}
			}
			else tree.addNode(milp_current);
		}	 
		
		CyclicBarrier cb =null;
		Thread tgroup0 = null;
		
		ArrayList<MilpManager> list_best_candidate=null;
		ArrayList<MilpManager> scarti_to_separe=null;
		ArrayList<MilpManager> listMangerMilpToRun=null;
		
		b:	{
			while(!tree.isEmpty()) {

				list_best_candidate=tree.getMilpBestUP(threadNumber);  
				scarti_to_separe=new ArrayList<MilpManager> ();
				for(MilpManager lp_curent:list_best_candidate) {
					scarti_to_separe.add(lp_curent);
				}

				if(!scarti_to_separe.isEmpty()) {
					listMangerMilpToRun = new ArrayList<MilpManager>();

					for(MilpManager lp_curent:scarti_to_separe) {
						MilpManager.populateArrayListBySeparation(listMangerMilpToRun,lp_curent);
					}
					//gestione a più thread
					if(threadNumber!=MILPThreadsNumber.N_1) {

						//Gestione Thread
						cb = new CyclicBarrier(listMangerMilpToRun.size());
						for(MilpManager lp_run:listMangerMilpToRun) {
							//milp.resolve();
							(tgroup0 = new Thread(new Task(cb, lp_run ))).start();
						}
						tgroup0.join();
					}
					//gestione a singolo thread
					else {
						//System.out.println("un trhea");
						for(MilpManager milp:listMangerMilpToRun) {
							milp.resolve();
						}
					}
					//RISULTATO
					//dopo esecuzione si valuta risultato
					for(MilpManager milp:listMangerMilpToRun) {
						if(milp.getSolutionType()==SolutionType.OPTIMUM) {
							//System.out.println("VALUE:"+milp.getOptimumValue());
											
							if(milp.isSolutionIntegerAmmisible() && milp.isProblemSemiContinusAmmisible()) {
								//System.out.println("intera:"+milp.getOptimumValue());
								if(  (target==TARGET_FO.MAX && lb.value < milp.getOptimumValue())     //max 
								  || (target==TARGET_FO.MIN && lb.value > milp.getOptimumValue())) {  //questo vale per il min

									milp.setIntegerIfOptimal();
									lb.value= milp.getOptimumValue();
									lb.milp=milp;
									type_solution=SolutionType.OPTIMUM; 
									//se devo cercare solo una soluzione ammissibile , ma non ottima
									if(isJustTakeFeasibleSolution) {
										type_solution=SolutionType.FEASIBLE;
										break b;
									}
								}
							}
							else tree.addNode(milp);
						}
						num_simplex_resolved+=1;	
					}
					tree.deleteNodeWhitUPnotValide(lb.value); 
				}

				if(num_simplex_resolved >= num_max_simplex) { 
					logger.log(SscLevel.WARNING,RB.format("it.ssc.pl.milp.MILP.msg8")+num_max_simplex);
					logger.log(SscLevel.NOTE,RB.format("it.ssc.pl.milp.MILP.msg9"));
					return SolutionType.MAX_NUM_SIMPLEX;
				}
			}
		}
		long end=System.currentTimeMillis();
		logger.log(SscLevel.TIME,RB.format("it.ssc.pl.milp.MILP.msg2",RB.getHhMmSsMmm((end-start))));
		logger.log(SscLevel.INFO,RB.getString("it.ssc.pl.milp.MILP.msg3")+num_simplex_resolved);
		if(type_solution==SolutionType.OPTIMUM) logger.log(SscLevel.INFO,RB.getString("it.ssc.pl.milp.MILP.msg4"));
		return type_solution;

	}
	
	
	/**
	 * Questo metodo restituisce la soluzione del problema eliminando i vincoli di interezza (soluzione rilassata). 
	 * Se vi sono variabili binarie , viene solo tolto il vincolo dall'assumere valori interi, ma  
	 * alla variabile binaria rimane il vincolo  di essere compresa tra zero ed uno . 
	 * 
	 * @return restituisce la soluzione rilassata, ovvero la soluzione del problema senza vincoli di interezza. 
	 * 
	 */
	
	public Solution getRelaxedSolution()  {
		if(milp_initiale!=null) return milp_initiale.getSolution();
		else return null;
	}
	
	/**
	 * Questo metodo restituisce, se esiste, la soluzione ottima intera, misto intera o binaria 
	 * 
	 * @return  la soluzione ottima intera, misto intera o binaria 
	 */
	
	public Solution getSolution()  {
		if(lb.milp!=null) return lb.milp.getSolution();
		else return null;
		
	}
	
	/**
	 * 
	 * @return il numero di Thread impostati per la risoluzione del problema
	 */
	
	public MILPThreadsNumber getThreadNumber() {
		return threadNumber;
	}
	
	/**
	 * Questo metodo permette di impostare il numero di Thread da utilizzare per eseguire il Branch and Bound
	 * 
	 * @param lthreadNumber
	 */

	public void setThreadNumber(MILPThreadsNumber lthreadNumber) {
		threadNumber = lthreadNumber;
	}


	public boolean isJustTakeFeasibleSolution() {
		return isJustTakeFeasibleSolution;
	}

	/**
	 * Impostando a true permette di interrompere il simplesso in modo da determinare 
	 * non una soluzione ottima ma solamente una soluzione ammissibile del problema. 
	 * @param isStopPhase2 true per interrompere il B&B ed ottenere solo una soluzione ammissibile. 
	 */

	public void setJustTakeFeasibleSolution(boolean isJustTakeFeasibleSolution) {
		this.isJustTakeFeasibleSolution = isJustTakeFeasibleSolution;
	}

	
	/**
	 * Permette di impostare uno step (valore di tolleranza sacrificabile sul valore della 
	 * funzione obiettivo, per cui questo valore &egrave; da esprimere nelle stesse unit&agrave; di misura con cui si esprime la f.o.) 
	 * per accelerare la ricerca e minimizzare l'uso della memoria. Pi&ugrave; nel dettaglio se si individua una 
	 * soluzione intera (non ottima), di norma nell'operazione di potatura , vengono tagliati tutti i rami con un valore sulla f.o.  
	 * <= al valore z della soluzione trovata (se il problema &egrave; di max) , impostando questo valore vengono potati tutti 
	 * i rami con un valore <=  z + valore_step
	 * 
	 * 
	 * E' da esprimere sempre in valore assoluto 
	 * (non possono essere inseriti valori negativi). 
	 * @param tolerableDisadvantage
	 * @throws LPException 
	 */

	/*

	public void setStepDisadvantage(double stepDisadvantage) {
		if(stepDisadvantage<0) throw new LPException(RB.getString("it.ssc.pl.milp.MILP.msg11"));
		this.stepDisadvantage = stepDisadvantage;
	}
	
	
	public double getStepDisadvantage() {
		return stepDisadvantage;
	}
	
	*/
	
	
	
}
