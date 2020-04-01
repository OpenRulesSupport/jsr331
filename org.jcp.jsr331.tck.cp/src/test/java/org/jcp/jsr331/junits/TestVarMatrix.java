package org.jcp.jsr331.junits;

import javax.constraints.Problem;
import javax.constraints.ProblemFactory;
import javax.constraints.Var;
import javax.constraints.VarMatrix;

public class TestVarMatrix {
	
	public static void main(String[] args) {
		Problem p = ProblemFactory.newProblem("TestVarMatrix");
		int rows = 14;
		int columns = 7;
		
		VarMatrix ft = p.variableMatrix("BM", 1, 1, rows, columns);
//		p.log(ft.toString());
//		p.log("\n");
		System.out.println("COLUMNS");
		for (int i = 0; i < columns; i++) {
			Var[] vars = ft.column(i);
			System.out.println();
			for (int j = 0; j < rows; j++) {
				System.out.print(vars[j].toString() + " ");
			}
		}
		System.out.println("\nROWS");
		for (int i = 0; i < rows; i++) {
			Var[] vars = ft.row(i);
			System.out.println();
			for (int j = 0; j < columns; j++) {
				System.out.print(vars[j].toString() + " ");
			}
		}
	}

}
