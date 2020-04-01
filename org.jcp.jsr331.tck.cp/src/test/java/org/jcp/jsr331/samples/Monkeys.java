//===============================================
// J A V A  C O M M U N I T Y  P R O C E S S
// 
// J S R  3 3 1
// 
// Monkey Business - see https://dmcommunity.wordpress.com/2015/10/30/monkey-business/
// 
//================================================
package org.jcp.jsr331.samples;

import javax.constraints.Problem;
import javax.constraints.ProblemFactory;
import javax.constraints.Solution;
import javax.constraints.VarString;

public class Monkeys {

	static final String[] names = { "Mike", "Sam", "Anna", "Harriet" };
	static final String[] fruits = { "Banana", "Pear", "Orange", "Apple" };
	static final String[] restingPlaces = { "Grass", "Rock", "Stream", "Tree Branch" };
	
	class Monkey {
		String	name;
		VarString fruit;
		VarString restingPlace;
		
		public Monkey(String name) {
			this.name = name;
			fruit = p.variableString(name + "'s fruit", fruits);
			restingPlace = p.variableString(name + "'s restingPlace", restingPlaces);
		}

		public String toString() {
			return "Monkey [name=" + name + ", fruit=" + fruit
					+ ", restingPlace=" + restingPlace + "]";
		}
	}
	
	Problem p = ProblemFactory.newProblem("Monkeys");
	Monkey[] monkeys = new Monkey[names.length];

	public void define() {
		try {
			// Variables
			VarString[] fruitVariables = new VarString[monkeys.length];
			VarString[] restingPlaceVariables = new VarString[monkeys.length];
			for (int i = 0; i < monkeys.length; i++) {
				Monkey m = new Monkey(names[i]);
				monkeys[i] = m;
				fruitVariables[i] = m.fruit;
				restingPlaceVariables[i] = m.restingPlace;
				p.postIfThen(p.linear(m.restingPlace, "=", "Rock"), 
						     p.linear(m.fruit, "=", "Apple"));
				p.postIfThen(p.linear(m.fruit, "=", "Pear"), 
					     p.linear(m.restingPlace, "!=", "Tree Branch"));
				if (m.name.equals("Sam")) {
					p.post(m.fruit, "!=", "Banana");
					p.post(m.restingPlace, "=", "Grass");
				}
				if (m.name.equals("Anna")) {
					p.post(m.fruit, "!=", "Pear");
					p.post(m.restingPlace, "=", "Stream");
				}
				if (m.name.equals("Mike")) {
					p.post(m.fruit, "!=", "Orange");
				}
				if (m.name.equals("Harriet")) {
					p.post(m.restingPlace, "!=", "Tree Branch");
				}
			}
			
			p.postAllDiff(fruitVariables);
			p.postAllDiff(restingPlaceVariables);

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public Solution solve() {
		return p.getSolver().findSolution();
	}

	public void print(Solution solution) {
		p.log("SOLUTION:");
		for (int i = 0; i < p.getVarStrings().length; i++) {
			VarString var = p.getVarStrings()[i];
			String value = solution.getValueString(var.getName());
			p.log(var.getName() + " = " + value);
		}
	}

	public static void main(String[] args) {
		Monkeys p = new Monkeys();
		p.define();
		Solution solution = p.solve();
		p.print(solution);
	}

}
