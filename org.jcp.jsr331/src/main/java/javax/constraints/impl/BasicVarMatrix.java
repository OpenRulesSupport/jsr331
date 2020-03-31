package javax.constraints.impl;

import javax.constraints.Problem;
import javax.constraints.ProblemFactory;
import javax.constraints.Var;
import javax.constraints.VarMatrix;

/**
 * A VarMatrix is a two-dimensional array of Var objects
 *
 */

public class BasicVarMatrix implements VarMatrix {
	
	Problem problem;
	String name;
	int rows;
	int columns;
	int min;
	int max;
	
	Var[][] matrix;
	Var[] rowVars;
	Var[] columnVars;
	
	public BasicVarMatrix(Problem problem, String name, int min, int max, int rows, int columns) {
		this.problem = problem;
		this.name = name;
		this.min = min;
		this.max = max;
		this.rows = rows;
		this.columns = columns;
		matrix = new Var[rows][columns];
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < columns; j++) {
				//String varName = name+"("+i+";"+j+")";
				String varName = name+i+j;
				matrix[i][j] = problem.variable(varName,min,max);
			}
		}
	}
	
	public int numberOfRows() {
		return rows;
	}
	
	public int numberOfColumns() {
		return columns;
	}
	
	public Var[] row(int i) {
		Var[] rowi = new Var[columns];
		for (int j = 0; j < columns; j++) {
			rowi[j] = matrix[i][j];
		}
		return rowi;
	}
	
	public Var[] column(int j) {
		Var[] columnj = new Var[rows];
		for (int i = 0; i < rows; i++) {
			columnj[i] = matrix[i][j];
		}
		return columnj;
	}
	
	public Var[] flat() {
		Var[] array = new Var[rows*columns];
		int k = 0;
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < columns; j++) {
				array[k++] = matrix[i][j];
			}
		}
		return array;
	}
	
	public Var[] diagonal1() {
		if (rows != columns)
			return null;
		Var[] diag = new Var[rows];
		int k = 0;
		for (int i = 0; i < rows; i++) {
				diag[k++] = matrix[i][i];
		}
		return diag;
	}
	
	public Var[] diagonal2() {
		if (rows != columns)
			return null;
		Var[] diag = new Var[rows];
		int k = 0;
		for (int i = 0; i < rows; i++) {
				diag[k++] = matrix[i][rows-i-1];
		}
		return diag;
	}
	
	public Var get(int i,int j) {
		return matrix[i][j];
	}
	
	public void post(int[][] data) {
		for (int i = 0; i < rows; i++) {
		     for(int j = 0; j< columns; j++)  {
		    	 problem.post(matrix[i][j],"=",data[i][j]);
		     }
		}
	}
	
	public void post(int i, int j, int value) {
		problem.post(matrix[i][j], "=", value);
	}
	
	public String toString() {
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < rows; i++) {
		     for(int j=0; j< columns; j++)  {
		    	Var var = matrix[i][j];
		    	if (var.isBound()) {
			        int value = var.getValue();
			        String gap = " "; 
			        if (value < 10) gap = "  ";
			        buf.append(gap + value);
		    	}
		    	else {
		    		buf.append(" " + var.toString());
		    	}
		     }
		     buf.append("\n");
		}
		return buf.toString();
	}
	
}
