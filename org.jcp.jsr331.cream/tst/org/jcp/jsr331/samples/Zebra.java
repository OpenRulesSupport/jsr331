package org.jcp.jsr331.samples;

import javax.constraints.*;

/*
Where is Zebra?			
1. There are five houses.			
2. The Englishman lives in the red house.			
3. The Spaniard owns the dog.
4. Coffee is drunk in the green house.			
5. The Ukrainian drinks tea.
6. The green house is immediately to the right of the ivory house.			
7. The Old Gold smoker owns snails.
8. Kools are smoked in the yellow house.			
9. Milk is drunk in the middle house.		
10. The Norwegian lives in the first house.			
11. The man who smokes Chesterfields lives in the house next to the man with the fox.			
12. Kools are smoked in the house next to the house where the horse is kept.	
13. The Lucky Strike smoker drinks orange juice.
14. The Japanese smokes Parliaments.
15. The Norwegian lives next to the blue house.			
*/

public class Zebra {
	
	Problem p = ProblemFactory.newProblem("Zebra");

	public void define() {

		// Define variables
		
		//Colors	
		Var green  = p.variable("green", 0, 4);
		Var ivory  = p.variable("ivory", 0, 4);
		Var blue   = p.variable("blue", 0, 4);
		Var red    = p.variable("red", 0, 4);
		Var yellow = p.variable("yellow", 0, 4);
		// People	
		Var Norwegian  = p.variable("Norwegian", 0, 4);
		Var Ukrainian  = p.variable("Ukrainian", 0, 4);
		Var Japanese   = p.variable("Japanese", 0, 4);
		Var Englishman = p.variable("Englishman", 0, 4);
		Var Spaniard   = p.variable("Spaniard", 0, 4);
		// Drinks	
		Var juice  = p.variable("juice", 0, 4);
		Var tea    = p.variable("tea", 0, 4);
		Var milk   = p.variable("milk", 0, 4);
		Var water  = p.variable("water", 0, 4);
		Var coffee = p.variable("coffee", 0, 4);
		// Pets	
		Var snail = p.variable("snail", 0, 4);
		Var dog   = p.variable("dog", 0, 4);
		Var fox   = p.variable("fox", 0, 4);
		Var horse = p.variable("horse", 0, 4);
		Var ZEBRA = p.variable("ZEBRA", 0, 4);
		// Cigarettes	
		Var Chesterfield = p.variable("Chesterfield", 0, 4);
		Var Parliament   = p.variable("Parliament", 0, 4);
		Var Lucky        = p.variable("Lucky", 0, 4);
		Var OldGolds     = p.variable("OldGolds", 0, 4);
		Var Kools        = p.variable("Kools", 0, 4);


		// ======= Post constraints
		Var[] colors = { green,ivory,blue,red,yellow };
		p.postAllDiff(colors);
		Var[] people = { Norwegian,Ukrainian,Japanese,Englishman,Spaniard };
		p.postAllDiff(people);
		Var[] drinks = { juice,tea,milk,water,coffee };
		p.postAllDiff(drinks);
		Var[] pets = { snail,dog,fox,horse,ZEBRA };
		p.postAllDiff(pets);
		Var[] cigarettes = { Chesterfield,Parliament,Lucky,OldGolds,Kools };
		p.postAllDiff(cigarettes);
		
		p.post(Englishman,"=",red); 	// The Englishman lives in the red house
		p.post(Spaniard,"=",dog); 		// The Spaniard owns the dog
		p.post(coffee,"=",green); 		//	Coffee is drunk in the green house
		p.post(Ukrainian,"=",tea); 		//	The Ukrainian drinks tea
		p.post(OldGolds,"=",snail); 	//	The Old Golds smoker owns snails
		p.post(Kools,"=",yellow); 		//	Kools are smoked in the yellow house
		p.post(milk,"=",2); 			//	Milk is drunk in the middle house
		p.post(Norwegian,"=",0); 		//	The Norwegian lives in the first house
		p.post(Lucky,"=",juice); 		//	The Lucky Strike smoker drinks orange juice
		p.post(Japanese,"=",Parliament);//	The Japanese smokes Parliament

		// The green house is immediately to the right of the ivory house
		p.post(green,"=",ivory.plus(1));
		// The man who smokes Chesterfields lives in the house next to the man with the fox
		p.postOr(p.linear(Chesterfield,"=",fox.plus(1)), 
                 p.linear(Chesterfield,"=",fox.minus(1)) );
		// Kools are smoked in the house next to the house where the horse is kept
	    p.postOr(p.linear(Kools,"=",horse.plus(1)), 
	             p.linear(Kools,"=",horse.minus(1)) );
		// The Norwegian lives next to the blue house
	    p.postOr(p.linear(Norwegian,"=",blue.plus(1)),
	    		 p.linear(Norwegian,"=",blue.minus(1)) );
	
		p.log(p.getVars());
	}

	public void solve() {
		Solver solver = p.getSolver();
		Solution solution = solver.findSolution();
		if (solution != null) {
			p.log("==== Solution ====");
			Var[] vars = p.getVars();
			for(int house = 0; house < 5; house++) {
			    StringBuffer buf = new StringBuffer();
			    buf.append("House #"+(house+1)+":");
			    for(int i=0; i<vars.length; i++) {
			    	String name = vars[i].getName();
			    	int value = solution.getValue(name);
			    	if (value == house)
			    		buf.append("  " + name);
			    }
			    p.log(buf.toString());
			}
		}
		solver.logStats();
	}
	
	public static void main(String[] args) {
		Zebra t = new Zebra();
		t.define();
		t.solve();
	}
}