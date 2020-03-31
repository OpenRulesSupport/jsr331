//================================================================
// J A V A  C O M M U N I T Y  P R O C E S S
// 
// J S R  3 3 1 
// 
// CONSTRAINER-BASED REFERENCE IMPLEMENTATION
//
// Copyright (c) Cork Constraint Computation Centre, 2010
// University College Cork, Cork, Ireland, www.4c.ucc.ie
// Constrainer is copyrighted by Exigen Group, USA.
// 
//================================================================
package javax.constraints.impl.search;

import javax.constraints.Solver;
import javax.constraints.Var;
import javax.constraints.impl.search.goal.Goal;

import com.exigen.ie.constrainer.Constrainer;
import com.exigen.ie.constrainer.GoalGenerate;
import com.exigen.ie.constrainer.IntExp;
import com.exigen.ie.constrainer.IntExpArray;

public class GoalAssignValuesNative extends ConstrainerGoal {
	Var[] vars;
	com.exigen.ie.constrainer.Goal nativeGoal;
	
	public GoalAssignValuesNative(Solver solver, Var[] vars) {
		super(solver,"generate");
		this.vars = vars;
		Constrainer constrainer = getConstrainer();
		IntExpArray intvars = new IntExpArray(constrainer,vars.length);
		for(int i=0; i<vars.length; i++) {
			IntExp var = (IntExp)vars[i].getImpl();
			intvars.set(var, i);
		}
		nativeGoal = new GoalGenerate(intvars);
		setVars(vars);
	}

	public Goal execute() throws Exception {
	    com.exigen.ie.constrainer.Goal resultGoal = nativeGoal.execute();
	    if (resultGoal == null)
	    	return null; // all vars are instantiated

	    return new ConstrainerGoal(getSolver(),resultGoal);
	}

}
