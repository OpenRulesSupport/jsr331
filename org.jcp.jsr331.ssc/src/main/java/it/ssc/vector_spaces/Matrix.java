package it.ssc.vector_spaces;


public class Matrix  {
	
	protected double[][] big_matrix;
	protected int n_row;
	protected int n_column;
	
	public Matrix(int n_row, int n_column) {
		this.n_row=n_row;
		this.n_column=n_column;
		this.big_matrix=new double[n_row][n_column];
	}
	
	public double[][] getMatrix() {
		return big_matrix;
	}
	
	public  Matrix(double[][] matrix) throws MatrixException  {
		if(matrix==null) throw new MatrixException("Non posso costruire un Matrix con un argomento del costruttore a null");
		if(matrix[0]==null) throw new MatrixException("Non posso costruire un Matrix con un matrix[0] a null");
		this.n_row= matrix.length;
		this.n_column=matrix[0].length;
		big_matrix=matrix;
	}
	
	
	
	/*
	public Matrix(double[][] matrix) throws MatrixException  {
		
		if(matrix==null) throw new MatrixException("Non posso costruire un Matrix con un argomento del costruttore a null");
		this.n_row= matrix.length;
		this.n_column=matrix[0].length;
		big_matrix=new double[n_row][n_column];
		for(int _i=0;_i<n_row;_i++) {
			if(matrix[_i].length!=this.n_column) throw new MatrixException("La matrice passata deve avere righe di uguale lunghezza");
			for(int _j=0;_j<n_column;_j++) {
				big_matrix[_i][_j]=matrix[_i][_j];
			}	
		}
	}
	*/
	
	public void setCustomNrow(int n_row) {
		this.n_row = n_row;
	}

	public void setCustomnNcolumn(int n_column) {
		this.n_column = n_column;
	}

	public void setCell(int _i,int _j, double value) {
		big_matrix[_i][_j]=value;
	}
	
	public double getCell(int i, int j) {
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
		double[][] big_matrix_traspose=new double[new_n_row][new_n_column];
		for(int _i=0;_i<n_row;_i++) {
			for(int _j=0;_j<n_column;_j++) {
				big_matrix_traspose[_j][_i]=big_matrix[_i][_j];
			}
		}
		this.big_matrix=big_matrix_traspose;
		this.n_row=new_n_row;
		this.n_column=new_n_column;
	}	
}
