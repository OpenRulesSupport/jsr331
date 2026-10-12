package com.javasolver;

import com.javasolver.SCIP;
import com.javasolver.SCIPConstraint;
import com.javasolver.SCIPVariable;

public class Test {
	public static void main(String[] args) {
		
		try (SCIP model = new SCIP().name("test")) {
            SCIPVariable x = model.addVariable().binary().obj(6).name("x");
            SCIPVariable y = model.addVariable().integer().bounds(0, 3).obj(5).name("y");
            
            SCIPConstraint c = model.createExpression()
			 .add(4, x)
		     .add(3, y)
		     .leq(7);
            
    		SCIP.Status status = model.maximize();
    		if (status != SCIP.Status.OPTIMAL)
                throw new AssertionError(status + " objective=" + model.getObjectiveValue());
            System.out.println("MIP smoke test passed: objective=" + model.getObjectiveValue());
           
        }
		
	}
}
