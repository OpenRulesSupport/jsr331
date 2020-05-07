package org.jcp.jsr331.samples;

import javax.constraints.*;

/**
 * Title: FamilyRiddle <br>
 * Description: (was taken from ILOG SolverClass User Guide)<br>
 * Let's assume that Rene and Leo are both heads of household, and--what a
 * coincidence---both families include three girls and three boys. The youngest
 * child in Leo's family is a girl, and in Rene's family, a little girl has just
 * arrived. In other words, there is a girl in Rene's family whose age is less
 * than one year. Neither family includes any twins, nor any children closer in
 * age than a year.<br>
 * <br>
 * All the children are under age ten. In each family, the sum of the ages of
 * the girls is equal to the sum of the ages of the boys; in fact, the sum of
 * the squares of the ages of the girls is equal to the sum of the squares of
 * the ages of the boys. The sum of the ages of all these children is 60.<br>
 * <br>
 * Here's our riddle: what are the ages of the children in these two families?<br>
 */

public class FamilyRiddle {
	static Problem P = ProblemFactory.newProblem("example");
	static int n = 3;
	static int maxAge = 9;

	static Var sumOfSquares(Var[] array) {
			int size = array.length;
			Var[] products = new Var[size];
			for (int i = 0; i < size; i++) {
				products[i] = array[i].multiply(array[i]);
			}
			return P.sum(products);
	}

	static public class Family {
		public String father = null;

		public Var[] daughters = new Var[n];

		public Var[] sons = new Var[n];

		public Var[] children = new Var[2*n];

		public Family(String father) {
			this.father = father;
			for (int i = 0; i < n; i++) {
				String dname = father+"'s daughter " + (i+1);
				daughters[i] = P.variable(dname, 0, maxAge);
				String sname = father+"'s son " + (i+1);
				sons[i] = P.variable(sname,0, maxAge);
			}
			for (int i = 0; i < sons.length; i++) {
				children[i] = daughters[i];
				children[i + sons.length] = sons[i];
			}
			addConstraints();
		}

		private void addConstraints() {
			// there are no twins
			P.postAllDiff(children);
			// the sum of the ages of the girls is equal to the sum of the ages
			// of the boys
			P.post(P.sum(sons),"=",P.sum(daughters));
			// the sum of the squares of the ages of the girls
			// is equal to the sum of the squares of the ages of the boys
			P.post(sumOfSquares(sons), "=", sumOfSquares(daughters));
		}

		public Var[] getChildrenVars() {
			return children;
		}

		public void print() {
			P.log("" + father + "'s family: " + "\nsons: ");
			String str = new String();
			for (int i = 0; i < sons.length; i++)
				str += sons[i].getValue() + " ";
			P.log(str);
			P.log("daughters: ");
			str = "";
			for (int i = 0; i < daughters.length; i++)
				str += daughters[i].getValue() + " ";
			P.log(str);
		}

	}

	public static void main(String[] args) {

//		========= Problem Representation ==================
		Family LeoFamily = new Family("Leo");
		Family ReneFamily = new Family("Rene");

		// in Rene's family, a little girl has just arrived
		P.post(ReneFamily.daughters[0],"=",0);
		// The youngest child in Leo's family is a girl
		for (int i = 1; i < LeoFamily.children.length; i++) {
			P.post(LeoFamily.daughters[0],"<",LeoFamily.children[i]);
		}
		// The sum of the ages of all these children is 60
		P.post(P.sum(ReneFamily.children).plus(P.sum(LeoFamily.children)), "=", 60);

//		========= Problem Resolution ==================
		// Find a solution
		Solver solver = P.getSolver();
		SearchStrategy search1 = solver.getSearchStrategy();
		search1.setVars(ReneFamily.getChildrenVars());
		solver.addSearchStrategy(LeoFamily.getChildrenVars());
		Solution solution = solver.findSolution();
		if (solution == null)
			P.log("No Solutions");
		else{
			P.log(solution.toString());
			P.log(LeoFamily.getChildrenVars());
			P.log(ReneFamily.getChildrenVars());
		}
	}
}