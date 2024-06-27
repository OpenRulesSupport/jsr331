package org.jcp.jsr331.samples;

/**
 * Problem Description "Virtual Chess Tournament"
 *  Kasparov, Karpov and Fisher played 7 games against each other.
 *  Kasparov won the most games.
 *  Karpov lost the least games.
 *  Fisher became a champion.
 *  Find a final score.
 */

import javax.constraints.Solution;
import javax.constraints.Solver;
import javax.constraints.Var;
import javax.constraints.impl.Problem;

public class Chess extends Problem {

	final static int N = 7; // number of games played played against each other

	public void defineAndSolve() {

		// Define mutual Victories, Losses and Draws
		log("define variables");
		Var V12 = variable("Kasparov won against Karpov ", 0, N);
		Var L12 = variable("Kasparov lost against Karpov", 0, N);
		Var D12 = variable("Kasparov drew against Karpov", 0, N);

		Var V13 = variable("Kasparov won against Fisher ", 0, N);
		Var L13 = variable("Kasparov lost against Fisher", 0, N);
		Var D13 = variable("Kasparov drew against Fisher", 0, N);

		Var V23 = variable("Karpov won against Fisher   ", 0, N);
		Var L23 = variable("Karpov lost against Fisher  ", 0, N);
		Var D23 = variable("Karpov drew against Fisher  ", 0, N);

		// Post constraint "Each pair played 7 games"
		log("post constraints");
		post(V12.plus(L12).plus(D12), "=", N); // V12+L12+D12==7
		post(V13.plus(L13).plus(D13), "=", N); // V13+L13+D13==7
		post(V23.plus(L23).plus(D23), "=", N); // V23+L23+D23==7

		// Define personal Victories, Losses and Draws
		Var V1 = V12.plus(V13);
		V1.setName("Kasparov wins  ");
		Var L1 = L12.plus(L13);
		L1.setName("Kasparov loses ");
		Var D1 = D12.plus(D13);
		D1.setName("Kasparov draws ");
		Var V2 = L12.plus(V23);
		V2.setName("Karpov wins    ");
		Var L2 = V12.plus(L23);
		L2.setName("Karpov loses   ");
		Var D2 = D12.plus(D23);
		D2.setName("Karpov draws   ");
		Var V3 = L13.plus(L23);
		V3.setName("Fisher wins    ");
		Var L3 = V13.plus(V23);
		L3.setName("Fisher loses   ");
		Var D3 = D13.plus(D23);
		D3.setName("Fisher draws   ");

		// Kasparov won the most games
		post(V1, ">", V2);
		post(V1, ">", V3);
		// Karpov lost the least games
		post(L2, "<", L1);
		post(L2, "<", L3);

		// Define personal Points
		log("define personal Points");
		Var P1 = V1.multiply(2).plus(D1); // P1 = 2*V1 + D1
		P1.setName("Kasparov Points");
		add("Kasparov Points", P1);
		Var P2 = V2.multiply(2).plus(D2); // P2 = 2*V2 + D2
		add("Karpov Points", P2);
		Var P3 = V3.multiply(2).plus(D3); // P3 = 2*V3 + D3
		add("Fisher Points", P3);

		// post "champion" constraint
		post(P3, ">", P1); // P3>P1
		post(P3, ">", P2); // P3>P2

		// ========= Problem Resolution ==================
		// SolverWithGoals solver = (SolverWithGoals)getSolver();
		Solver solver = getSolver();
		Solution[] solutions = solver.findAllSolutions();
		for (int i = 0; i < solutions.length; i++) {
			solutions[i].log();
		}
	}

	public static void main(String[] args) {
		Chess chess = new Chess();
		chess.defineAndSolve();
	}
}
