//versione 3
package it.ssc.pl.milp;

import  it.ssc.i18n.RB;
import java.util.logging.Logger;
import it.ssc.log.SscLevel;
import it.ssc.log.SscLogger;
import it.ssc.pl.milp.util.LPThreadsNumber;
import it.ssc.util.Tuple2;
import it.ssc.vector_spaces.Matrix;
import it.ssc.vector_spaces.MatrixException;
import it.ssc.vector_spaces.Vector;

final class Phase1 extends Phase {
	
	private static final Logger logger=SscLogger.getLogger();
	
	private final int n;
	private final int m;
	private  int n_aux;
	private boolean isMilp=false;
	
	
	public Phase1(Matrix A, Vector B,EPSILON epsilon,EPSILON cepsilon) throws SimplexException, MatrixException {
		
		super(epsilon, cepsilon);
		this.m=A.getNrow();
		this.n=A.getNcolumn();
		if(m!=B.lenght()) {
			throw new SimplexException("Il numero di righe di A (matrice dei coefficienti) non si adatta al numero di componenti del vettore B dei termini noti");
		}
		Tuple2<Integer,double[][]> ctr_phase1=createTablePhase1(A.getMatrix(),B.getVector());
		this.n_aux=ctr_phase1._0;
		this.TBEX=ctr_phase1._1;
		this._N=A.getNcolumn() + n_aux; //numero ausiliarie n_aux + n
		this._M=A.getNrow();
	}
	
	public void setMilp(boolean isMilp) {
		this.isMilp = isMilp;
	}

	
	@SuppressWarnings("unchecked")
	private Tuple2<Integer,double[][]> createTablePhase1(double[][] A_,double[] B_) throws MatrixException {
		
		basis = new int[m];
		//questo vettore dovra' determinare se nella riga iesima esiste gia una variabile 
		//che può essere utilizzata per far diventare il sistema in forma canonica ( cij =1)
		//se non le trova, vengono create con valore tupla a false
		Tuple2<Integer,Boolean>[] var_canonical=new Tuple2[m];
		
		//vedo se in ogni riga esiste una variabile con un cij =1  
		double sum_Aij;
		for (int row = 0; row < m ; row++) {  //spazzolo le righe 
			for (int j = 0; j < n ; j++) { //su una righa vedo se sulle diverse colonne ci sono aij=1
				if(A_[row][j]==1) {          //se si , controllo che gli altri valori sulla colonna siano a zero 
					sum_Aij=0;
					for (int k = 0; k < m ; k++) { 
						sum_Aij+=Math.abs(A_[k][j]);
					}
					if(sum_Aij==1) { 
						var_canonical[row]=new Tuple2<>(j,true);
					}
				}
			}
		}
		
		//numero variabili ausiliarie introdotte
		n_aux=0;
		
		//determino la dove occorre inserire una variabile ausiliaria
		
		for (int row = 0; row < m ; row++) { 
			 if(var_canonical[row]==null)  { 
				 var_canonical[row]=new Tuple2<>(n + n_aux,false); 
				 n_aux++;
			 }
		}
		
		double[] C_=calcNewCoefficienti(A_,var_canonical);
		double z_init= calcNewValueZ(B_,var_canonical);
			
		//Matrix table = new Matrix(m+1,n+n_aux+1);
		double[][] table_exended_loc=new double[m+1][];
		
		
		//MATRICE ORIGINALE
		for (int i = 0; i < m ; i++) {
			table_exended_loc[i]=new double[n+n_aux+1];
			for (int j = 0; j < n ; j++) {
				table_exended_loc[i][j]=A_[i][j];
			}
			A_[i]=null;
		}   
		
		
		//VARIABILI SLACKS - B - BASE 
		for (int i = 0; i < m ; i++) {
			for (int j = n; j < n+ n_aux ; j++) {
				if(var_canonical[i]._0==j && !var_canonical[i]._1) table_exended_loc[i][j]=1.0;
			}
			table_exended_loc[i][n_aux + n]=  B_[i]; //new 
			setBases(i,var_canonical[i]._0); 
		}
		
		table_exended_loc[m]=new double[n+n_aux+1];
		
		for (int j = 0; j < n ; j++) {
			table_exended_loc[m][j]=C_[j];
		}
		
		table_exended_loc[m][n_aux+n]=z_init; 
		return new Tuple2<Integer,double[][]>(n_aux,table_exended_loc);
	}
	
	private double calcNewValueZ(double[]  B_,Tuple2<Integer,Boolean>[] var_canonical) {
		double init_z=0.0;
		for (int i = 0; i < m ; i++) {
			if(!var_canonical[i]._1) init_z=init_z + B_[i];
		}
		return init_z;
	}
	
	
	private double[] calcNewCoefficienti(double[][] A_,Tuple2<Integer,Boolean>[] var_canonical) {
		double[] C2= new double[n] ;
		for (int i = 0; i < m ; i++) {
			if(!var_canonical[i]._1) {
				for (int j = 0; j < n ; j++) {
					C2[j]=C2[j]+A_[i][j];
				}
			}
		}   
		return C2;
	}
	
	public SolutionType resolve(long num_iteration) throws InterruptedException  {
		
		int var_incoming=0,row_var_outgoing=0;
		SolutionType solution=SolutionType.MAX_ITERATIUM; 
		
		while(this.iteration < num_iteration) {
			
			/*
			logger.log(SscLevel.NOTE,"PRIMA TABELLA:");
			printTable2();
			printBases();
			*/
			
			if(isBaseDegenerate()) var_incoming = test_var_incoming_bland();
			else var_incoming = test_var_incoming();
			
			if (var_incoming == -1) {	
				solution= SolutionType.OPTIMUM; 
				break;
			} 
			
			if ((row_var_outgoing = test_var_outgoing(var_incoming)) == -1) { 
				solution= SolutionType.ILLIMITATUM;
				break;
			}
			
			if(threadsNumber==LPThreadsNumber.AUTO) pivotingParallel(row_var_outgoing,var_incoming);
			else if(threadsNumber!=LPThreadsNumber.N_1) pivotingParallelCyclic(row_var_outgoing,var_incoming);
			else pivoting(row_var_outgoing,var_incoming);
			
			setBases(row_var_outgoing,var_incoming);
			this.iteration++;
		}
		
		if(solution== SolutionType.MAX_ITERATIUM) logger.log(SscLevel.WARNING,"Raggiunto il massimo numero di iterazioni "+(num_iteration));
		double z=getValueZ();
		if(!isMilp) {
			logger.log(SscLevel.INFO,RB.getString("it.ssc.pl.milp.Phase1.msg1")+z);
		}
		
		if(solution== SolutionType.OPTIMUM && (   Math.abs(z) > this.cepsilon )) {
			if(!isMilp)  {
				logger.log(SscLevel.NOTE,"Fase Uno - Condizione per esistenza di soluzioni ammissibili : |z| <= epsilon ="+cepsilon);
				logger.log(SscLevel.WARNING,"Fase Uno - Non sussuste la condizione per esistenza di soluzioni ammissibili in quanto |z| > epsilon . Il valore epsilon puo' essere modificato tramite il metodo setCEpsilon()");
			}
			solution= SolutionType.VUOTUM;
		}
		else if(solution== SolutionType.ILLIMITATUM) {
			if(!isMilp) logger.log(SscLevel.WARNING,"Fase Uno non ha raggiunto convergenza - Ottenuto ottimo illimitato. ");
		}
		return solution;
	}
	

   

   //a fronte dell'indice della riga i dove la variabile ausiliaria e' in base, vedo se c'è qualche Aij =! 0 
   private int existVarOrigOutBase(int index_aux) {
	   for (int j = 0; j < n ; j++) {
		   if ( Math.abs(TBEX[index_aux][j]) > epsilon )  {
			   return j;
		   }
	   }
	   return -1;
   }
   
   //se ce in base una variabile con indice  >=n e' ausiliaria
   private int existAuxBase() {
	   for (int i = 0; i < basis.length; i++) {
			if(basis[i] >= n) return i ;
		}
	   return -1;
   }
   
	public Matrix pulish() throws MatrixException { 
		//se c'e' una variabile ausiliaria in base (naturalmente degenere) si fa uscire 
		//se quelle presenti hanno zero sulle variabili reali.  Si tolgono le righe 
		// si cancellano le colonne relative alle ausiliaria  
		
		Pulish pulish=new Pulish();
		pulish.exitAuxFromBase();
		double[][] table_pulish=pulish.deleteRowAux(TBEX);
		TBEX=null;
		return pulish.clearColumnAux(table_pulish);
		//return pulish.deleteColumnAux(table_pulish);
	}
	
	//AGGIUNTO IL 15/10/2018
	
	public double[] getValuesBases() {
		double[] values=new double[_M];
		for(int _a=0;_a <this.m;_a++) {
			values[_a]= TBEX[_a][ _N];
			//System.out.println("VALUESSSE:"+values[_a]);
		}
		return values;
	}
   

   private final class Pulish {
	   
	   double[][] deleteRowAux(double[][] table_pulish) {
			int index_aux_out = 0;
			while (((index_aux_out = existAuxBase()) != -1) && ifAllCoeffZeroAux(index_aux_out)) {
				//System.out.println("delete row");
				table_pulish=deleteSingleRowAux(index_aux_out,table_pulish);
				updateBase(index_aux_out);
			}
		    return table_pulish;
	   }
	   
	    void updateBase(int row_canc ) {
		   int new_basis[]=new int[basis.length-1];
		   int index_row = 0;
		   for (int i = 0; i < basis.length; i++) {
			   if (row_canc==i) continue;
			   new_basis[index_row]=basis[i] ;
			   index_row++;
		   }
		   basis=new_basis;
	   }
	    
	    private boolean ifAllCoeffZeroAux(int index) {
	 	   for (int j = 0; j < n ; j++) {
	 		   if( Math.abs(TBEX[index][j]) > epsilon  ) {
	 			  logger.log(SscLevel.WARNING,"Esiste alla fine di Fase 1, una variabile artificiale in base che non è stata eliminata !");
	 			  return false;
	 		   }
	 	   }
	 	   return true;
	    }
	    
	    //non tocca numero righe o colonne
	    private void exitAuxFromBase() {
	 	   int index_aux_out = 0, index_orig_in = 0;
	 	   //se esiste una variabile ausiliaria in base e se sulla riga di questa c'è un Aij =! 0 , faccio pivoting per farla uscire
	 	   //finche non escono tutte. 
	 	   while (((index_aux_out = existAuxBase()) != -1) && ((index_orig_in = existVarOrigOutBase(index_aux_out)) != -1)) {
	 		   //printTable2();
	 		   pivoting(index_aux_out, index_orig_in);
	 		   setBases(index_aux_out,index_orig_in);
	 		   Phase1.this.iteration++;
	 	   }
	    }
	    
	    /*
	     * versione da testare , con annullamento riga
	     */
	    
	    @SuppressWarnings("unused")
		private Matrix deleteColumnAuxOld(double[][]  A_all_column) throws MatrixException {
			   int n_row = A_all_column.length;
			   double[][] table=new double[n_row][] ;

			   for (int i = 0; i < n_row; i++) {
				   int index_col = 0;
				   table[i]=new double[ n + 1];
				   for (int j = 0; j <= n + n_aux; j++) {
					   if (!(j < n || j == n + n_aux)) continue;
					   table[i][index_col]= A_all_column[i][j]; 
					   index_col++;
				   }
				   A_all_column[i]=null;  //elimino riga per riga per liberare spazio il prima possibile
			   }
			   A_all_column=null;
			   return new Matrix(table);
		   }
	    
	    
	    /**
	     * Versione con riutilizzo della stessa matrice
	     * @param A_all_column
	     * @return
	     * @throws MatrixException
	     */
	    
	    
	   
		@SuppressWarnings("unused")
		private Matrix deleteColumnAux(double[][]  A_all_column) throws MatrixException {
			   int n_row = A_all_column.length;
			  
			   for (int i = 0; i < n_row; i++) {
				   int index_col = 0;
				   double[] subarray=new double[n + 1];
				   for (int j = 0; j <= n + n_aux; j++) {
					   if (!(j < n || j == n + n_aux)) continue;  //QUI SI PUO OTTIMIZZARE
					   subarray[index_col]= A_all_column[i][j]; 
					   index_col++;
				   }
				   A_all_column[i]=subarray;  //elimino riga per riga per liberare spazio il prima possibile
			   }
			   //A_all_column=null;
			   return new Matrix(A_all_column);
		   }
	    
	    
	    /**
	     * Questo metodo sostituisce deleteColumnAux. Uso la stessa matrice mettendo a NaN le colonne aux non piu'
	     * utilizzate. 
	     * 
	     * @param A_all_column
	     * @return
	     * @throws MatrixException
	     */
	    
	    private Matrix clearColumnAux(double[][]  A_all_column) throws MatrixException {
			   int n_row = A_all_column.length;
			   for (int i = 0; i < n_row; i++) {
				   
				   /*
				    * La nuova matrice deve avere n+1 colonne.
				    * Sulla colonna n+1, occorre scrivere i valori rhs (colonna n + n_aux) della matrice originaria. 
				    * Fatto questo tutti i valore da n+2 in poi vanno messi a NaN
				    */
				   A_all_column[i][n]=  A_all_column[i][n + n_aux];
				   for (int j = (n+1); j <= n + n_aux; j++) {
					   A_all_column[i][j]=Double.NaN;
				   }
			   }
			   Matrix newA= new Matrix(A_all_column); 
			   newA.setCustomnNcolumn(n + 1);
			   //A_all_column=null;
			   return newA;
		   }
	    
	    
	   
	   //versione nuove da Testare. Fatta copia riga per riga.   ???????????????
	   
	   double[][] deleteSingleRowAux(int row_canc ,double[][] table_pulish) {
			int n_row = table_pulish.length;  
			double[][] table=new double[n_row-1][] ;

			int index_row = 0;
			for (int i = 0; i < n_row; i++) {
				if (row_canc==i) continue;
				table[index_row]= table_pulish[i];
				index_row++;
			}
			return table;
	   }
   }  
}
