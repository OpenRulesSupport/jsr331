//===============================================
// J A V A  C O M M U N I T Y  P R O C E S S
// 
// J S R  3 3 1
// 
// Test Compatibility Kit
// 
//================================================

package org.jcp.jsr331.samples;

/*
 Given a supply of different components and bins of given types,
 determine all assignments of components to bins satisfying
 specified assignment constraints subject to an optimization criterion.

 In the following example there are 5 types of components:
 glass, plastic, steel, wood, copper

 There are three types of bins:
 red, blue, green

 whose capacity constraints are:
 red   has capacity 3
 blue  has capacity 1
 green has capacity 4

 whose containment constraints are:
 red   can contain glass, wood, copper
 blue  can contain glass, steel, copper
 green can contain plastic, wood, copper

 and requirement constraints are (for all bin types):
 wood requires plastic

 Certain component types cannot coexist:
 glass  exclusive copper
 copper exclusive plastic

 and certain bin types have capacity constraint for certain components:
 red   contains at most 1 of wood
 green contains at most 2 of wood

 Here is the components demand:
 1 of glass
 2 of plastic
 1 of steel
 3 of wood
 2 of copper

 What is the minimum total number of bins required to contain the components?
 
 Note. This problem was initially described in the ILOG Solver User Manual

 ------------------------------------------------------------ */

import java.util.Vector;

import javax.constraints.Constraint;
import javax.constraints.Solution;
import javax.constraints.Solver;
import javax.constraints.Var;
import javax.constraints.impl.Problem;
import javax.constraints.impl.constraint.Linear;

public class Bins {

	// Demand
	static final int[] demand = { 1, 2, 1, 3, 2 };

	// ConfigureBins
	static final int red = 0, blue = 1, green = 2;
	static final String[] binTypes = { "red", "blue", "green" };

	static final int[] binCapacities = { 3, 1, 4 };

	static Problem csp = null;

	Solution solution;

	public static void main(String[] args) {
		long executionStart = System.currentTimeMillis();
		int maxBins = 0;
		for (int i = 0; i < demand.length; ++i) {
			maxBins += demand[i];
		}

		int numberOfBins = 1;
		while (true) {
			try {
				Bins bins = new Bins(numberOfBins);
				if (bins.solve()) {
					out("*** Solution: " + numberOfBins + " bins");
					for (int b = 0; b < numberOfBins; b++) {
						out(bins.bin(b).toString());
					}
					break;
				}
			} catch (Exception e) {
				out("Failed to create " + numberOfBins + " bin(s)");
			}
			out("=== " + numberOfBins + " bins is not enough");
			numberOfBins++;
			if (numberOfBins > maxBins) {
				out("Something wrong with the implementation of constrains");
				break;
			}
		}
		long executionTime = System.currentTimeMillis() - executionStart;
		System.out.println("Total Execution time: " + executionTime + " msec");
	}


	// ================================================================== ConfigureBins

	Vector<Bin> bins = null;

	int numberOfBins = 1;

	public Bins(int numberOfBins) throws Exception {

		this.numberOfBins = numberOfBins;

		csp = new Problem("ConfigureBins " + numberOfBins);
		csp.log("*** Solve the problem for " + numberOfBins + " bins");
		bins = new Vector<Bin>(numberOfBins);
		for (int i = 0; i < numberOfBins; i++) {
			bins.add(new Bin(csp, i + 1));
			// avoid bins symmetry
			if (i > 0) {
				// Problem.log("Avoid symmetry constraints for bin " + notRev);
				Bin bin1 = bins.elementAt(i - 1);
				Bin bin2 = bins.elementAt(i);
//				bin1.type.le(bin2.type);
				csp.post(bin1.type, "<=", bin2.type);
			}
		}

		out("Post demand constraints..");
		for (int j = 0; j < components.length; j++) {
			Var[] binCounts = new Var[numberOfBins];
			for (int b = 0; b < numberOfBins; b++) {
				binCounts[b] = bins.elementAt(b).counts[j];
				csp.add(binCounts[b]);
			}
			String constraintName = demand[j] + " " + components[j];
			try {
				out("Post Demand Consraint: " + constraintName);
				csp.post(binCounts, "=", demand[j]);
			} catch (Exception e) {
				out("failed to demand " + constraintName);
				throw e;
			}
		}
	}

	Bin bin(int i) {
		return bins.elementAt(i);
	}

	boolean solve() {
		Var[] types = new Var[numberOfBins];
		Var[] counts = new Var[numberOfBins * components.length];
		int x = 0;
		for (int b = 0; b < numberOfBins; b++) {
			types[b] = bins.elementAt(b).type;
			for (int j = 0; j < components.length; j++) {
				counts[x] = bins.elementAt(b).counts[j];
				x++;
			}
		}
		Solver solver = csp.getSolver();
//		csp.log(types);
//		csp.log(counts);
//		solver.traceFailures(true);
//		solver.traceExecution(true);
//		solver.trace(types,PropagationEvent.ANY);
//		solver.trace(counts,PropagationEvent.ANY);
		solver.setSearchStrategy(types);
		solver.addSearchStrategy(counts);
		solution = solver.findSolution();
		solver.logStats();
		return solution!=null;
	}

	static public void out(String text) {
		System.out.println(text);
	}

	// =========================================================== one Bin
	// Components
	static final int glass = 0, plastic = 1, steel = 2, wood = 3, copper = 4;
	static final String[] components = 
	               { "glass", "plastic", "steel", "wood", "copper" };
	class Bin {
		
		public int id;
		public Var type;
		public Var capacity;
		public Var[] counts; // per component
		
		Bin(Problem csp,int binId) {
			id = binId;
			out("Create Bin-" + id);
			type = csp.variable("Bin" + id + "Type", 0, binTypes.length - 1);

			csp.log("Capacity constraints");
			int capacityMax = 0;
			for (int i = 0; i < binCapacities.length; i++) {
				if (binCapacities[i] > capacityMax)
					capacityMax = binCapacities[i];
			}
			capacity = csp.variable("capacity", 0, capacityMax);
			csp.postElement(binCapacities, type, "=", capacity);
			
			counts = new Var[components.length];
			for (int i = 0; i < components.length; i++) {
				counts[i] = csp.variable(countName(i), 0, capacityMax);
			}
			// Sum of counts <= capacity
			csp.post(counts, "<=", capacity);
			
//			Var sumOfCounts = pb.var("sum", 1,capacityMax);
//			pb.linear(counts,"=",sumOfCounts);
//			pb.element(binCapacities, type, ">=", sumOfCounts);

			out("Containment constraints");
			Constraint c1, c2, c3;
			
			// red contains at most 1 of wood
			c1 = new Linear(type,"=",red);
			c2 = new Linear(counts[wood],"<=",1);
			c1.implies(c2).post();

			// green contains at most 2 of wood
			c1 = new Linear(type,"=",green); //????
			c2 = new Linear(counts[wood],"<=",2);
			c1.implies(c2).post();

			// red can contain glass, wood, copper
			c1 = new Linear(type,"=",red);
			c2 = new Linear(counts[plastic],"=",0);
			c3 = new Linear(counts[steel],"=",0);
			c1.implies(c2.and(c3)).post();

			// blue can contain glass, steel, copper
			c1 = new Linear(type,"=",blue);
			c2 = new Linear(counts[plastic],"=",0);
			c3 = new Linear(counts[wood],"=",0);
			c1.implies(c2.and(c3)).post();

			// green can contain plastic, wood, copper
			c1 = new Linear(type,"=",green);
			c2 = new Linear(counts[glass],"=",0);
			c3 = new Linear(counts[steel],"=",0);
			c1.implies(c2.and(c3)).post();

			// wood requires plastic
			c1 = new Linear(counts[wood],">",0);
			c2 = new Linear(counts[plastic],">",0);
			c1.implies(c2).post();

			// glass exclusive copper
			c1 = new Linear(counts[glass],"=",0);
			c2 = new Linear(counts[copper],"=",0);
			c1.or(c2).post();

			// copper exclusive plastic
			c1 = new Linear(counts[copper],"=",0);
			c2 = new Linear(counts[plastic],"=",0);
			c1.or(c2).post();
		}

		public String countName(int component) {
			return "Bin" + id + ":" + components[component];
		}

		@Override
		public String toString() {
			StringBuffer buf = new StringBuffer();
			buf.append("Bin#" + id + " (" + binTypes[solution.getValue("Bin" + id + "Type")] + "):");
			for (int i = 0; i < components.length; ++i) {
				int c = solution.getValue(countName(i));
				if (c > 0)
					buf.append(" " + components[i] + "=" + c);
			}
			return buf.toString();
		}

	} 
}
