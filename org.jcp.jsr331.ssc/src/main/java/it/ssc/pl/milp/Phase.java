package it.ssc.pl.milp;

import java.util.ArrayList;
import java.util.concurrent.CyclicBarrier;

import it.ssc.pl.milp.util.LPThreadsNumber;
import it.ssc.step.parallel.Parallelizable;
import it.ssc.step.parallel.Task;

abstract class Phase {

	protected int basis[];
	protected double[][] TBEX;
	protected long iteration = 0; 
	protected int _M;
	protected int _N;
	protected double epsilon;
	protected double cepsilon;
	private ArrayList<Row> listIndici ;
	
	
	//variabili appoggio per simplesso parallelo B 
	private int l_out;
	private int l_in;
	private double l_PERNO;
	private double l_ROW_OUT[];
	private Parallelizable[] listrun;
	protected LPThreadsNumber threadsNumber=LPThreadsNumber.N_1;
	private CyclicBarrier cb ;
	private Thread tgroup0 ;
	private int num_thread;
	

	protected Phase(int _M, int _N, EPSILON epsilon) {
		this._M=_M; //righe 
		this._N=_N; //colonne
		this.epsilon=epsilon.getValue();
	}
	
	protected Phase(EPSILON epsilon,EPSILON cepsilon) {
		this.cepsilon=cepsilon.getValue();
		this.epsilon=epsilon.getValue();
	}
	
	private void fillListRow() {
		listIndici=new ArrayList<Row> ();
		for(int i=0;i<=_M;i++) listIndici.add(new Row(i,TBEX[i]));
	}
	
	public void setThreadsNumber(LPThreadsNumber threadsNumber) {
		this.threadsNumber = threadsNumber;
		if(threadsNumber==LPThreadsNumber.AUTO) fillListRow() ;
		else if(threadsNumber!=LPThreadsNumber.N_1) {
			num_thread=threadsNumber.getThread();
			cb  = new CyclicBarrier(num_thread);
		}
	}

	protected final class Row {
		int i;
		double[] val;
		Row(int _i, double[] _val) {
			i=_i;
			val=_val;
		}
	}
	
	public abstract SolutionType resolve(long num_iteration) throws InterruptedException;

	public long getNumIteration() {
		return iteration;
	}

	protected void setBases(int row,int in) {
		basis[row] = in;
	}

	public double getValueZ() {
		return  -(TBEX[_M][_N]);
	}
	
	protected final void pivotingParallel(final int out, final int in) {
		final double PERNO=TBEX[out][in];
		final double ROW_OUT[]=TBEX[out];
		
		//System.out.println("AUTO");
		
		listIndici.parallelStream().forEach( (row) -> { 
			if (row.val[in]!=0.0 && row.i != out) {
				double PRO_IN_DIV_PERNO=row.val[in] / PERNO;
				for (int j = 0; j <= _N; j++) {
					if(j!=in) row.val[j] -=  ROW_OUT[j] * PRO_IN_DIV_PERNO;
				}
				//aggiornamento colonna variabile entrante 
				row.val[ in]= 0.0;
			}	
		});
		
		//riga variabile uscente
		for (int j = 0; j <= _N; j++) {
			if (j != in) {
				ROW_OUT[j] /=  PERNO;
			}
		}
		ROW_OUT[in] = 1.0;
	}

	protected final void pivoting(final int out, final int in) {
		
		final double PERNO=TBEX[out][in];
		final double ROW_OUT[]=TBEX[out];
		double PRO_IN_DIV_PERNO=0;
		double TBEX_i[];
		
		//System.out.println("Normal");
		
		for (int i = 0; i <= _M; i++) {
			TBEX_i=TBEX[i];
			if ( TBEX_i[in]!=0.0  && i != out ) { 
				
				PRO_IN_DIV_PERNO=TBEX_i[in] / PERNO;
				for (int j = 0; j < in; j++) {
					TBEX_i[j] -=  ROW_OUT[j] * PRO_IN_DIV_PERNO;
					//TBEX[i][j] = ((TBEX[i][j]*TBEX[out][ in]) - (TBEX[out][ j] * TBEX[i][in])) / TBEX[out][ in];
				}
				for (int j = in+1; j <= _N; j++) {
					TBEX_i[j] -=  ROW_OUT[j] * PRO_IN_DIV_PERNO;
				}
				//aggiornamento colonna variabile entrante 
				TBEX_i[ in]= 0.0;
			}	
		}
		
		//riga variabile uscente
		for (int j = 0; j <= _N; j++) {
			if (j != in) {
				ROW_OUT[j] /=  PERNO;
			}
		}
		ROW_OUT[in] = 1.0;
	}

	
	
	/**
	 * Versione A 
	 * 
	 * @param var_incoming
	 * @return
	 * @throws InterruptedException 
	 */
	
	
	/*
	protected final void pivotingParallelA(final int out, final int in) throws InterruptedException {
		
		int num_thread=2;
		
		final double PERNO=TBEX[out][in];
		final double ROW_OUT[]=TBEX[out];
		
		int block=_M/num_thread;
		
		for(int ntr=0;ntr<num_thread;ntr++) {
			
			final int start=ntr*block;
			final int end;
			if(ntr==(num_thread-1)) end=_M+1;
			else end = (ntr+1)*(block);
			
			(tgroup0 = new Thread(new Task(cb, () -> {
				
				for (int i = start; i < end; i++) {
					double[] TBEX_i=TBEX[i];
					if ( TBEX_i[in]!=0.0  && i != out ) { 
						
						double PRO_IN_DIV_PERNO=TBEX_i[in] / PERNO;
						for (int j = 0; j < in; j++) {
							TBEX_i[j] -=  ROW_OUT[j] * PRO_IN_DIV_PERNO;
							//TBEX[i][j] = ((TBEX[i][j]*TBEX[out][ in]) - (TBEX[out][ j] * TBEX[i][in])) / TBEX[out][ in];
						}
						for (int j = in+1; j <= _N; j++) {
							TBEX_i[j] -=  ROW_OUT[j] * PRO_IN_DIV_PERNO;
						}
						//aggiornamento colonna variabile entrante 
						TBEX_i[ in]= 0.0;
					}	
				}
			}))).start();
		}
		tgroup0.join();
		
		//riga variabile uscente
		for (int j = 0; j <= _N; j++) {
			if (j != in) {
				ROW_OUT[j] /=  PERNO;
			}
		}
		ROW_OUT[in] = 1.0;
	}
	
	*/
	
	
	
	/**
	* VERSIONE B
	* 
	* @param out
	* @param in
	* @throws InterruptedException
	*/
	
	protected final void pivotingParallelCyclic(final int outo, final int ino) throws InterruptedException {
		
		this.l_out=outo;
		this.l_in=ino;
		this.l_PERNO=TBEX[l_out][l_in];
		this.l_ROW_OUT=TBEX[l_out];
		
		final int block=_M/num_thread;
		
		if(listrun==null) {
			//System.out.println("Ciclic");
			listrun=new Parallelizable[num_thread];
			for(int ntr=0;ntr<num_thread;ntr++) {
				
				final int start=ntr*block;
				final int end;
				if(ntr==(num_thread-1)) end=_M+1;
				else end = (ntr+1)*(block);
				
				listrun[ntr] =  () -> {
					
					//System.out.println("out:"+out+"  in:"+in);
					
					for (int i = start; i < end; i++) {
						double[] TBEX_i=TBEX[i];
						if ( TBEX_i[l_in]!=0.0  && i != l_out ) { 
							
							double PRO_IN_DIV_PERNO=TBEX_i[l_in] / l_PERNO;
							for (int j = 0; j < l_in; j++) {
								TBEX_i[j] -=  l_ROW_OUT[j] * PRO_IN_DIV_PERNO;
								//TBEX[i][j] = ((TBEX[i][j]*TBEX[out][ in]) - (TBEX[out][ j] * TBEX[i][in])) / TBEX[out][ in];
							}
							for (int j = l_in+1; j <= _N; j++) {
								TBEX_i[j] -=  l_ROW_OUT[j] * PRO_IN_DIV_PERNO;
							}
							//aggiornamento colonna variabile entrante 
							TBEX_i[ l_in]= 0.0;
						}	
					}
				};
			}
		}
		
		for(int a=0;a<listrun.length;a++)  {
			(this.tgroup0 = new Thread(new Task(cb,listrun[a]))).start();
		}
		this.tgroup0.join();
		
		//riga variabile uscente
		for (int j = 0; j <= _N; j++) {
			if (j != l_in) {
				l_ROW_OUT[j] /=  l_PERNO;
			}
		}
		l_ROW_OUT[l_in] = 1.0;
	}



	protected final int test_var_outgoing(int var_incoming) {
		int row_var_outgoing = -1;
		double collo_bottiglia_value= 0, collo_bottiglia_candidate_value=0;
		for (int i = 0; i < _M; i++) {
			if (TBEX[i][var_incoming] <= epsilon) {    
				continue;
			}
			collo_bottiglia_candidate_value = TBEX[i][_N] / TBEX[i][var_incoming];
			if (row_var_outgoing == -1 || collo_bottiglia_candidate_value < collo_bottiglia_value) {   //dovrebbe funzionare bland. Prendo la prima riga buona
				row_var_outgoing = i;
				collo_bottiglia_value = collo_bottiglia_candidate_value;
			}
		}
		return row_var_outgoing;  //se -1 OTTIMO ILLIMITATO. Tutti i coefficienti sulla colonna pivotale sono o zero o negativi
	}

	//Test variabile entrante (senza Bland)
	//Sulla riga dei costi io ho espresso, a differenza di Simeone, i coefficienti con il segno originale. 
	//Mentre su Simeone sono cambiati di segno.  Quindi io faccio test che se sono <= 0 Allora : Soluzione ottina. 
	protected final int test_var_incoming() {
		double TBEX_M[]=TBEX[_M];
		double value_cj_var_incoming=0.0;
		int index_var_incoming = 0;
		for (int j = 0; j < _N; j++) {
			//controllo se c'e' un qualche coefficente sulla f.o. maggiore di ZERO
			//naturalmente relativo alle variabili non basiche. E prendo quello con valore più alto. 
			if (TBEX_M[ j]> (value_cj_var_incoming) ) { 
				value_cj_var_incoming = TBEX_M[ j]; 
				index_var_incoming = j;
			}
		}

		//if (value_var_incoming > 0 && value_var_incoming < epsilon.getValue())  System.out.println("#######Value:"+value_var_incoming);
		if (value_cj_var_incoming > epsilon ) return index_var_incoming;  // @@@@@@@ epsilon.getValue()
		else return -1;  // optimal
	}


	protected final int test_var_incoming_bland() {
		
		double TBEX_M[]=TBEX[_M];
		for (int j = 0; j < _N; j++) {
			//Cerco la variabile entrante. La candidata dovrebbe essere quella con il costo più elevato. 
			//?? Ma per la regola di Bland prendo la prima 
			//controllo se c'e' un qualche coefficente sull f.o. maggiore di ZERO
			//naturalmente relativo alle variabili non basiche.
			//10/08/2018
			//Uso epsilon in quanto valori infinitesimali dei coefficienti danno incrementi infinitesimali 
			//della fuinzione obiettivo. => Quindi uso epsilon. ?????????
			if (TBEX_M[j] > epsilon) {           
				return j;
			}
		}
		return -1; // ottimo
	}


	protected  final boolean isBaseDegenerate() {		
		for (int i = 0; i < _M; i++) { 
			//System.out.println("Value:"+value);   
			if( Math.abs(TBEX[i][_N]) <= epsilon) {       //Questi valori non possone essere negativi..... Non capisco la presenza del abs ??????  
				return true;   
			} 
		}
		return false;
	}

	
	 void printTable2(double[][] table_pulis) {
		for(int _i=0;_i<table_pulis.length;_i++) {
			System.out.println("");
			for(int _j=0;_j<table_pulis[0].length;_j++) {
				double val=table_pulis[_i][_j];
				System.out.printf("\t : %7.24f",val);
			}
		}
		System.out.println("");
	}
	 
	 void printTable2() {
			for(int _i=0;_i<TBEX.length;_i++) {
				System.out.println("");
				for(int _j=0;_j<TBEX[0].length;_j++) {
					double val=TBEX[_i][_j];
					System.out.printf("\t : %7.24f",val);
				}
			}
			System.out.println("");
		}
	

	
	 void printBases() {

		System.out.println("F2 dim:" + basis.length);
		for (int i = 0; i < basis.length; i++) {
			System.out.println("F2 X" + (basis[i]+1));
		}
	}

	public int[] getBasisClone() {
		return basis.clone();
	}
	
	public void resetTBEX() {
		TBEX=null;
	}
	
	public int[] getBasis() {
		return basis;
	}
}
