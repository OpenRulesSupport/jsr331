package it.ssc.vector_spaces;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

public class MatrixFunction {
	
	static final BigDecimal ZERO_BIG=new BigDecimal("0.0",MathContext.DECIMAL128);
	static final BigDecimal NEG_ONE_BIG=new BigDecimal("-1.0",MathContext.DECIMAL128);
	
	public static double determinant(BigMatrix matrix) throws MatrixException  {
		return detBigMatrix(matrix).setScale(30, RoundingMode.HALF_DOWN).doubleValue();
	}
	
	static BigDecimal detBigMatrix(BigMatrix matrix) throws MatrixException {
		
		if(matrix.n_column!= matrix.n_row) throw new MatrixException("Per il calcolo del determinante la matrice deve essere quadrata");
		
		BigDecimal det_a=ZERO_BIG;
		BigDecimal appo_big;
		if(matrix.n_column==1)  { 
			return matrix.big_matrix[0][0];
		}
		else if(matrix.n_column==2)  { 
			BigDecimal diag1=matrix.big_matrix[0][0].multiply(matrix.big_matrix[1][1], MathContext.DECIMAL128);
			BigDecimal diag2=matrix.big_matrix[0][1].multiply(matrix.big_matrix[1][0], MathContext.DECIMAL128);
			return diag1.subtract(diag2, MathContext.DECIMAL128);
		}
		else if(matrix.n_column>2)  { 
			
			BigMatrix new_matrix;
			int i_optimal=buildRowOptimal(matrix); 
			//navigo per le colonne sulla prima riga 
			for (int _j=0;_j<matrix.n_column;_j++) { 
				new_matrix=cutMatrixRowCol(i_optimal,_j, matrix);
				
				if(  matrix.big_matrix[i_optimal][_j].compareTo(ZERO_BIG)!=0  ) { 
					
					appo_big=matrix.big_matrix[i_optimal][_j].multiply(detBigMatrix(new_matrix), MathContext.DECIMAL128);
					appo_big=appo_big.multiply(NEG_ONE_BIG.pow(i_optimal +_j +2, MathContext.DECIMAL128), MathContext.DECIMAL128);
					det_a=det_a.add(appo_big, MathContext.DECIMAL128);
				}	
			}
		}
		
		return det_a;
	}
	
	static int buildRowOptimal(BigMatrix matrix)  {
		int row_optimal=0;
		int n_zero_for_row_optimal=0;
		int appo_zero;
		for(int _i=0 ; _i < matrix.n_row ;_i++) {
			appo_zero=0;
			for(int _j=0 ; _j < matrix.n_column ;_j++) {
				if (matrix.big_matrix[_i][_j].compareTo(ZERO_BIG)==0) {
					appo_zero++;
				}
			}
			if(n_zero_for_row_optimal < appo_zero) {
				row_optimal=_i;
				n_zero_for_row_optimal = appo_zero;
			}
		}
		return row_optimal;
	}
	

	static BigMatrix cutMatrixRowCol(int row_index,int col_index, BigMatrix matrix) {
		int n_row=matrix.getNrow();
		int n_col=matrix.getNcolumn();
		BigMatrix new_matrix = new BigMatrix(n_row-1,n_col-1);
		int _ni,_nj; 
		for(int _i=0;_i<n_row ;_i++) {
			if( _i==row_index) continue;
			for(int _j=0;_j<n_col ;_j++)  { 
				if(_j==col_index ) continue;
				if(_i >row_index) _ni=_i-1;
				else _ni=_i;
				if(_j >col_index) _nj=_j-1;
				else _nj=_j;
				new_matrix.big_matrix[_ni][_nj]= matrix.big_matrix[_i][_j];
			}
		}
		return new_matrix;
	}
	
	
	
	public static BigMatrix inverse(BigMatrix matrix) throws MatrixException  {
		if(matrix.n_column!= matrix.n_row)  throw new MatrixException("Per il calcolo dell'inversa la matrice deve essere quadrata");
		int dim_n=matrix.n_column;
		

		BigDecimal det_A=detBigMatrix(matrix);
	
		if(det_A.compareTo(ZERO_BIG)==0) throw new MatrixException("Per il calcolo dell'inversa la matrice deve avere determinante diverso da zero");
		BigMatrix matrix_inverse=new BigMatrix(dim_n,dim_n);
		
		BigDecimal det_Aij;
		BigMatrix Aij;
		for(int _i=0;_i<dim_n ;_i++) {
			for(int _j=0;_j<dim_n ;_j++)  { 
				Aij=cutMatrixRowCol(_j,_i,matrix);
				
				det_Aij=detBigMatrix(Aij);
				det_Aij=det_Aij.divide(det_A, MathContext.DECIMAL128);
				matrix_inverse.big_matrix[_i][_j]=det_Aij.multiply(NEG_ONE_BIG.pow(_i +_j +2, MathContext.DECIMAL128), MathContext.DECIMAL128);
			}
		}
		return matrix_inverse;
	}
	
	public static BigMatrix createMatrixFromVector(BigVector vector) {
		BigMatrix matrix_vector;
		int lenght_vector=vector.lenght();
		if(vector.getTipo()==BigVector.TYPE_VECTOR.ROW) {
			matrix_vector=new BigMatrix(1,lenght_vector);
			for(int _j=0;_j < lenght_vector;_j++)  {
				matrix_vector.big_matrix[0][_j]=vector.getBigCell(_j);
			}
		}
		else {
			matrix_vector=new BigMatrix(lenght_vector,1);
			for(int _i=0;_i < lenght_vector;_i++)  {
				matrix_vector.big_matrix[_i][0]=vector.getBigCell(_i);
			}
		}
		return matrix_vector;
	}
	
	
	public static BigMatrix product(BigMatrix matrix,BigVector  vector) throws Exception {
		BigMatrix matrix2=createMatrixFromVector(vector);
		return product(matrix,matrix2);
	}
	
	public static BigMatrix product(BigVector vector, BigMatrix matrix) throws MatrixException  {
		BigMatrix matrix1=createMatrixFromVector(vector);
		return product(matrix1,matrix);
	}
	
	public static BigMatrix product(BigMatrix matrix1,BigMatrix matrix2) throws MatrixException  {
		int n_rows1=matrix1.getNrow();
		int n_colu1=matrix1.getNcolumn();
		int n_rows2=matrix2.getNrow();
		int n_colu2=matrix2.getNcolumn();
		
		// dim m 
		if(n_colu1!=n_rows2) throw new MatrixException("La matrici non possono essere moltiplicate, hanno dimensioni non compatibili ("+n_rows1+","+n_colu1+")*("+n_rows2+","+n_colu2+") ");
		BigMatrix matrix_product=new BigMatrix(n_rows1,n_colu2);
		BigDecimal scalare;
		for(int _i=0;_i<n_rows1;_i++) {
			for(int _j=0;_j<n_colu2;_j++) {
				scalare= ZERO_BIG;
				for(int _m=0;_m<n_colu1;_m++) {
					BigDecimal single_produtc=matrix1.big_matrix[_i][_m].multiply(matrix2.big_matrix[_m][_j], MathContext.DECIMAL128);
					scalare=scalare.add(single_produtc, MathContext.DECIMAL128);
				}	
				matrix_product.big_matrix[_i][_j]=scalare;
			}
		}
		return matrix_product;
	}

}
