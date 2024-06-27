package org.jcp.jsr331.samples;

import javax.constraints.Var;
import javax.constraints.Solution;
import javax.constraints.impl.Problem;
import javax.constraints.impl.constraint.AllDifferent;

/**
 * Sudoku Description: 
 * A 81 cells square grid is divided in 9 smaller blocks of 9 cells
 * (3 x 3). Some of the 81 are filled with one digit. The aim of the puzzle is
 * to fill in the other cells, using digits except 0, such as each digit appears
 * once and only once in each row, each column and each smaller block. The
 * solution is unique.
 */

public class Sudoku {

	static int[][] data1 = new int[][] {
		{0,6,9,0,0,7,0,0,5},
		{0,5,0,0,0,4,0,2,0},
		{4,0,0,0,5,0,1,0,0},
		{8,0,5,0,0,0,6,0,0},
		{6,7,0,2,9,5,0,1,4},
		{0,0,1,0,0,0,7,0,9},
		{0,0,6,0,1,0,0,0,7},
		{0,1,0,4,0,0,0,8,0},
		{5,0,0,3,0,0,2,6,0}
	};

	static int[][] data = new int[][] {
			{7,0,0,1,0,6,8,2,0},
			{0,0,3,0,0,0,0,0,0},
			{0,0,8,0,9,0,4,0,0},
			{0,0,7,9,0,0,0,0,0},
			{0,0,0,0,5,3,0,1,0},
			{1,0,9,2,0,0,6,0,0},
			{0,0,0,0,0,0,9,3,0},
			{0,0,0,5,0,0,0,0,2},
			{0,0,0,4,0,0,0,7,0}
	};

	public static void main(String[] args) {

		// ========= Problem Representation ==================

		// create problem instance
		Problem p = new Problem("Sudoku");
		// Create 9X9 Square with Var variables with a domain [1;9]
		int n = 9;
		// Create n x n square of Var(s)
		Var[] x = new Var[n * n];
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				String iName = "x" + "[" + i + "," + j	+ "]";
				x[i * n + j] = p.variable(iName, 1, n); 
			}
		}

		Var[][] rows = new Var[][] {
		  { x[0*n+0], x[0*n+1], x[0*n+2],  x[0*n+3], x[0*n+4], x[0*n+5],  x[0*n+6], x[0*n+7], x[0*n+8] },
		  { x[1*n+0], x[1*n+1], x[1*n+2],  x[1*n+3], x[1*n+4], x[1*n+5],  x[1*n+6], x[1*n+7], x[1*n+8] },
		  { x[2*n+0], x[2*n+1], x[2*n+2],  x[2*n+3], x[2*n+4], x[2*n+5],  x[2*n+6], x[2*n+7], x[2*n+8] },

		  { x[3*n+0], x[3*n+1], x[3*n+2],  x[3*n+3], x[3*n+4], x[3*n+5],  x[3*n+6], x[3*n+7], x[3*n+8] },
		  { x[4*n+0], x[4*n+1], x[4*n+2],  x[4*n+3], x[4*n+4], x[4*n+5],  x[4*n+6], x[4*n+7], x[4*n+8] },
		  { x[5*n+0], x[5*n+1], x[5*n+2],  x[5*n+3], x[5*n+4], x[5*n+5],  x[5*n+6], x[5*n+7], x[5*n+8] },

		  { x[6*n+0], x[6*n+1], x[6*n+2],  x[6*n+3], x[6*n+4], x[6*n+5],  x[6*n+6], x[6*n+7], x[6*n+8] },
		  { x[7*n+0], x[7*n+1], x[7*n+2],  x[7*n+3], x[7*n+4], x[7*n+5],  x[7*n+6], x[7*n+7], x[7*n+8] },
		  { x[8*n+0], x[8*n+1], x[8*n+2],  x[8*n+3], x[8*n+4], x[8*n+5],  x[8*n+6], x[8*n+7], x[8*n+8] }
		  };

		Var[][] blocks = new Var[][] {
		  { x[0*n+0], x[0*n+1], x[0*n+2],
			x[1*n+0], x[1*n+1], x[1*n+2],
			x[2*n+0], x[2*n+1], x[2*n+2] },
		  { x[0*n+3], x[0*n+4], x[0*n+5],
			x[1*n+3], x[1*n+4], x[1*n+5],
			x[2*n+3], x[2*n+4], x[2*n+5] },
		  { x[0*n+6], x[0*n+7], x[0*n+8],
			x[1*n+6], x[1*n+7], x[1*n+8],
			x[2*n+6], x[2*n+7], x[2*n+8] },

		  { x[3*n+0], x[3*n+1], x[3*n+2],
			x[4*n+0], x[4*n+1], x[4*n+2],
			x[5*n+0], x[5*n+1], x[5*n+2] },
		  { x[3*n+3], x[3*n+4], x[3*n+5],
			x[4*n+3], x[4*n+4], x[4*n+5],
			x[5*n+3], x[5*n+4], x[5*n+5] },
		  { x[3*n+6], x[3*n+7], x[3*n+8],
			x[4*n+6], x[4*n+7], x[4*n+8],
			x[5*n+6], x[5*n+7], x[5*n+8] },

		  { x[6*n+0], x[6*n+1], x[6*n+2],
			x[7*n+0], x[7*n+1], x[7*n+2],
			x[8*n+0], x[8*n+1], x[8*n+2] },
		  { x[6*n+3], x[6*n+4], x[6*n+5],
			x[7*n+3], x[7*n+4], x[7*n+5],
			x[8*n+3], x[8*n+4], x[8*n+5] },
		  { x[6*n+6], x[6*n+7], x[6*n+8],
			x[7*n+6], x[7*n+7], x[7*n+8],
			x[8*n+6], x[8*n+7], x[8*n+8] }
		  };

		Var[][] columns = new Var[][] {
		  { x[0*n+0], x[1*n+0], x[2*n+0], x[3*n+0], x[4*n+0], x[5*n+0], x[6*n+0], x[7*n+0], x[8*n+0] },
		  { x[0*n+1], x[1*n+1], x[2*n+1], x[3*n+1], x[4*n+1], x[5*n+1], x[6*n+1], x[7*n+1], x[8*n+1] },
		  { x[0*n+2], x[1*n+2], x[2*n+2], x[3*n+2], x[4*n+2], x[5*n+2], x[6*n+2], x[7*n+2], x[8*n+2] },
		  { x[0*n+3], x[1*n+3], x[2*n+3], x[3*n+3], x[4*n+3], x[5*n+3], x[6*n+3], x[7*n+3], x[8*n+3] },
		  { x[0*n+4], x[1*n+4], x[2*n+4], x[3*n+4], x[4*n+4], x[5*n+4], x[6*n+4], x[7*n+4], x[8*n+4] },
		  { x[0*n+5], x[1*n+5], x[2*n+5], x[3*n+5], x[4*n+5], x[5*n+5], x[6*n+5], x[7*n+5], x[8*n+5] },
		  { x[0*n+6], x[1*n+6], x[2*n+6], x[3*n+6], x[4*n+6], x[5*n+6], x[6*n+6], x[7*n+6], x[8*n+6] },
		  { x[0*n+7], x[1*n+7], x[2*n+7], x[3*n+7], x[4*n+7], x[5*n+7], x[6*n+7], x[7*n+7], x[8*n+7] },
		  { x[0*n+8], x[1*n+8], x[2*n+8], x[3*n+8], x[4*n+8], x[5*n+8], x[6*n+8], x[7*n+8], x[8*n+8] }
		  };

		for (int i = 0; i < n; i++) {
			new AllDifferent(rows[i]).post();
			new AllDifferent(columns[i]).post();
			new AllDifferent(blocks[i]).post();
		}

		for (int i = 0; i < n; i++) {
			for(int j=0; j < n; j++) {

				if (data[i][j] != 0) {
					try {
					   p.post(rows[i][j],"=",data[i][j]);
					} catch(Exception e) {
					   p.log("Fail at i="+i+" j="+j);
					   System.exit(-1);
					}
				}
			}
		}

		// ========= Problem Resolution ==================
		Solution solution = p.getSolver().findSolution();
		if (solution == null)
			p.log("No Solutions");
		else {
			for (int i = 0; i < n; i++) {
				String str = new String();
				for (int j = 0; j < n; j++) {
					int value = solution.getValue("x["+i+"," +j+"]");
					str = str + value + " ";
				}
				p.log(str);
			}
		}
	}
}