package it.ssc.vector_spaces;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;


public class BigMatrix implements Cloneable {
	static final BigDecimal ZERO_BIG=new BigDecimal("0.0",MathContext.DECIMAL128);
	static final BigDecimal NEG_ONE_BIG=new BigDecimal("-1.0",MathContext.DECIMAL128);
	protected BigDecimal[][] big_matrix;
	protected int n_row;
	protected int n_column;
	
	
	public BigMatrix(int n_row,int n_column) {
		this.n_row=n_row;
		this.n_column=n_column;
		this.big_matrix=new BigDecimal[n_row][n_column];
		this.inizialize();
	}
	
	private void inizialize() {
		for(int _i=0;_i<n_row;_i++) {
			for(int _j=0;_j<n_column;_j++) {
				big_matrix[_i][_j]=ZERO_BIG;
			}
		}	
	}
	
	//Gestione di matrici di dimensione 0x0 o 0x1 ?????? 
	
	public BigMatrix(double[][] matrix) throws MatrixException  {
		
		if(matrix==null) throw new MatrixException("Non posso costruire un Matrix con un argomento del costruttore a null");
		this.n_row= matrix.length;
		this.n_column=matrix[0].length;
		big_matrix=new BigDecimal[n_row][n_column];
		String str_double;
		for(int _i=0;_i<n_row;_i++) {
			if(matrix[_i].length!=this.n_column) throw new MatrixException("La matrice passata deve avere righe di uguale lunghezza");
			for(int _j=0;_j<n_column;_j++) {
				str_double=String.valueOf(matrix[_i][_j]);
				big_matrix[_i][_j]=new BigDecimal(str_double,MathContext.DECIMAL128);
			}	
		}
	}
	
	
	public void setCell(int _i,int _j, double value) {
		String str_double=String.valueOf(value);
		big_matrix[_i][_j]=new BigDecimal(str_double,MathContext.DECIMAL128);
	}
	
	public double getCell(int i,int j) {
		return big_matrix[i][j].setScale(30, RoundingMode.HALF_DOWN).doubleValue();
	}
	
	public void setBigCell(int _i,int _j, BigDecimal value) {
		if(value==null) big_matrix[_i][_j]=ZERO_BIG;
		else big_matrix[_i][_j]=value;
	}
	
	public BigDecimal getBigCell(int i,int j) {
		return big_matrix[i][j];
	}
	
	public int getNrow() {
		return n_row;
	}

	public int getNcolumn() {
		return n_column;
	}

	public void traspose() {
		int new_n_row=n_column;
		int new_n_column=n_row;
		BigDecimal[][] big_matrix_traspose=new BigDecimal[new_n_row][new_n_column];
		for(int _i=0;_i<n_row;_i++) {
			for(int _j=0;_j<n_column;_j++) {
				big_matrix_traspose[_j][_i]=big_matrix[_i][_j];
			}
		}
		this.big_matrix=big_matrix_traspose;
		this.n_row=new_n_row;
		this.n_column=new_n_column;
	}
	
	
	public void multiply(double scalar)  {
		BigDecimal big_scalar=new BigDecimal(String.valueOf(scalar), MathContext.DECIMAL128);
		for(int _i=0;_i<this.n_row;_i++) {
			for(int _j=0;_j<this.n_column;_j++) {
				big_matrix[_i][_j]=big_matrix[_i][_j].multiply(big_scalar, MathContext.DECIMAL128);
			}	
		}
	}

	@Override
	public  BigMatrix clone() throws CloneNotSupportedException {
		// TODO Auto-generated method stub
		BigMatrix m_clone=(BigMatrix) super.clone();
		return m_clone;
	}
}
