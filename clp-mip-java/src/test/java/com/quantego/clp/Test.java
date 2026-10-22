package com.quantego.clp;

import com.quantego.clp.CLP;
import com.quantego.clp.CLPVariable;

public class Test {
	public static void main(String[] args) {
		CLP model = new CLP();
	
		CLPVariable x = model.addVariable().binary().obj(6);
		CLPVariable y = model.addVariable().integer().bounds(0, 3).obj(5);
	
		model.createExpression()
			 .add(4, x)
		     .add(3, y)
		     .leq(7);
	
		CLP.STATUS status = model.maximize();
		if (status != CLP.STATUS.OPTIMAL)
            throw new AssertionError(status + " objective=" + model.getObjectiveValue());
        System.out.println("MIP smoke test passed: objective=" + model.getObjectiveValue());
	}
}
