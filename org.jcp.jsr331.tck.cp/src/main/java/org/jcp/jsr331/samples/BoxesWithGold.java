package org.jcp.jsr331.samples;

/*
 * There are three boxes, but only one of them has gold inside. 
 * Additionally, each box has a message printed on it. 
 * One of these messages is true and the other two are lies.
 * The first box says, “Gold is in this box”.
 * The second box says, “Gold is not in this box”.
 * The third box says, “Gold is in not in Box 1”.
 * Which box contains the gold?
 * 
 * Source: https://dmcommunity.org/challenge/challenge-june-2021/
 */

import javax.constraints.*;

public class BoxesWithGold {

	public static void main(String[] args) {
		Problem p = ProblemFactory.newProblem("BoxesWithGold");
		// ======= Define variables
        Var goldInBox1 = p.variable("Gold in Box 1",0,1);
        Var goldInBox2 = p.variable("Gold in Box 2",0,1);
        
        // Define statements as constraints
        Constraint c1 = p.linear(goldInBox1,"=",1); // statement on Box 1
        Constraint c2 = p.linear(goldInBox2,"=",0); // statement on Box 2
        Constraint c3 = p.linear(goldInBox1,"=",0); // statement on Box 3
        
        // Post constraint "Only one statement is true"
        Var sum = p.sum(c1.asBool(), c2.asBool(),c3.asBool());
        p.post(sum,"=",1);
       
		// === PROBLEM RESOLUTION ================================
		Solution solution = p.getSolver().findSolution();
        if (solution.getValue("Gold in Box 1") == 1)
            p.log("SOLUTION: Gold in Box 1");
        else
        if (solution.getValue("Gold in Box 2") == 1)
            p.log("SOLUTION: Gold in Box 2");
        else
            p.log("SOLUTION: Gold in Box 3");
	}
}
