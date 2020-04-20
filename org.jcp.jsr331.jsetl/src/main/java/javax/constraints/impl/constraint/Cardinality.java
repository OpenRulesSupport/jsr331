package javax.constraints.impl.constraint;

import java.util.Vector;

import javax.constraints.Var;
import javax.constraints.impl.Constraint;
import javax.constraints.impl.Problem;
import javax.constraints.impl.search.Solver;

import jsetl.ConstraintClass;
import jsetl.IntLVar;
import jsetl.SolverClass;
import jsetl.lib.GlobalConstraints;
//import jsetl.lib.GlobalConstraints;


/**
 * Implements a global constraint that deal with cardinalities of the arrays of 
 * constrained variables. The ``cardinality variable'' is a constrained variable 
 * that is equal to the number of those elements in the given array that are bound 
 * to a given value. The class represents a linear constraint that involve the 
 * cardinality variable and an integer value or a variable.
 * 
 * @author Fabio Biselli
 *
 */
public class Cardinality extends Constraint {
	
	/**
	 * Build a new ConstraintClass that bind the cardinality of the variables
	 * of the given array of variables <code>vars</code> that satisfy the 
	 * constraint "oper <code>var</code>" to be equal the given
	 * integer <code>cardValue</code>. Where <code>oper</code> is a string 
	 * representing the commons constraint operator.
	 * 
	 * @param vars the array of integer variables.
	 * @param cardValue the cardinality
	 * @param oper the string representing the operator 
	 * @param var the integer variable that the variable must be 
	 * constrained to.
	 */
	public Cardinality (Var[] vars, int cardValue, String oper, Var var) {
		super(vars[0].getProblem());
		Problem p = (Problem) vars[0].getProblem();
		IntLVar value = ((javax.constraints.impl.Var) var).getIntLVar();
		ConstraintClass cardinality;
		IntLVar v = new IntLVar(p.getFreshName()+"c"+cardValue, cardValue);
		IntLVar k = new IntLVar(p.getFreshName()+"_card"+cardValue);
		SolverClass solver = ((Solver) p.getSolver()).getSolverClass();
		GlobalConstraints listOps = new GlobalConstraints(solver);
		Vector<IntLVar> varsList = new Vector<IntLVar>();
		for (int i = 0; i < vars.length; i++)
			varsList.add((IntLVar) vars[i].getImpl());
        cardinality = listOps.occurrence(varsList, v, k);
        ConstraintClass linear;
		switch(p.getOperator(oper)) {
		case 1: {
			// Case = "equals". 
			linear = cardinality.and(k.eq(value));
		} break;
		case 2: {
			// Case != "not equals".
			linear = cardinality.and(k.neq(value));
		} break;
		case 3: {
			// Case < "less".
			linear = cardinality.and(k.lt(value));
		}  break;
		case 4: {
			// Case <= "less equals".
			linear = cardinality.and(k.le(value));
		}  break;
		case 5: {
			// Case > "greater".
			linear = cardinality.and(k.gt(value));
		}  break;
		case 6: {
			// Case >= "greater equals".
			linear = cardinality.and(k.ge(value));
		}  break;
		default: throw new UnsupportedOperationException();
		}
		Constraint result = new Constraint(p, linear);
		((Solver) p.getSolver()).add(result);
		setImpl(result.getImpl());
		p.addAuxVariable(var);
		p.addAuxVariables(vars);
		p.addAuxVariable(k);
		p.addAuxVariable(v);
	}

	/**
	 * Build a new ConstraintClass that bind the cardinality of the variables
	 * of the given array of variables <code>vars</code> that satisfy the 
	 * constraint "oper <code>var</code>" to be equal the given
	 * integer <code>cardValue</code>. Where <code>oper</code> is a string 
	 * representing the commons constraint operator.
	 * 
	 * @param vars the array of integer variables.
	 * @param cardValue the cardinality
	 * @param oper the string representing the operator 
	 * @param var the integer variable that the variable must be 
	 * constrained to.
	 */
	public Cardinality(Var[] vars, Var cardValue, String oper, Var var) {
		super(vars[0].getProblem());
		Problem p = (Problem) vars[0].getProblem();
		ConstraintClass cardinality;
		IntLVar v = new IntLVar(((IntLVar)cardValue.getImpl()));
		IntLVar k = new IntLVar(p.getFreshName());
		SolverClass solver = ((Solver) p.getSolver()).getSolverClass();
		GlobalConstraints listOps = new GlobalConstraints(solver);
		Vector<IntLVar> varsList = new Vector<IntLVar>();
		for (int i = 0; i < vars.length; i++)
			varsList.add((IntLVar) vars[i].getImpl());
        cardinality = listOps.occurrence(varsList, v, k);
		IntLVar value = ((javax.constraints.impl.Var) var).getIntLVar();
		ConstraintClass linear;
		switch(p.getOperator(oper)) {
		case 1: {
			// Case = "equals".
			linear = cardinality.and(k.eq(value));
		} break;
		case 2: {
			// Case != "not equals".
			linear = cardinality.and(k.neq(value));
		} break;
		case 3: {
			// Case < "less".
			linear = cardinality.and(k.lt(value));
		}  break;
		case 4: {
			// Case <= "less equals".
			linear = cardinality.and(k.le(value));
		}  break;
		case 5: {
			// Case > "greater".
			linear = cardinality.and(k.gt(value));
		}  break;
		case 6: {
			// Case >= "greater equals".
			linear = cardinality.and(k.ge(value));
		}  break;
		default: throw new UnsupportedOperationException();
		}
		Constraint result = new Constraint(p, linear);
		((Solver) p.getSolver()).add(result);
		setImpl(result.getImpl());
		p.addAuxVariable(var);
		p.addAuxVariables(vars);
		p.addAuxVariable(k);
		p.addAuxVariable(cardValue);
	}
	
	/**
	 * Build a new ConstraintClass that bind the cardinality of the variables
	 * of the given array of variables <code>vars</code> that satisfy the 
	 * constraint "oper <code>value</code>" to be equal the given
	 * integer <code>cardValue</code>. Where <code>oper</code> is a string 
	 * representing the commons constraint operator.
	 * 
	 * @param vars the array of integer variables
	 * @param cardValue the cardinality
	 * @param oper the string representing the operator 
	 * @param value the value that the variable must be constrained to.
	 * 
	 */
	public Cardinality(Var[] vars, int cardValue, String oper, int value) {
		super(vars[0].getProblem());
		Problem p = (Problem) vars[0].getProblem();
		ConstraintClass cardinality;
		IntLVar v = new IntLVar(p.getFreshName()+"c"+cardValue, cardValue);
		IntLVar k = new IntLVar(p.getFreshName()+"_card");
		
		SolverClass solver = ((Solver) p.getSolver()).getSolverClass();
		GlobalConstraints listOps = new GlobalConstraints(solver);
		Vector<IntLVar> varsList = new Vector<IntLVar>();
		for (int i = 0; i < vars.length; i++)
			varsList.add((IntLVar) vars[i].getImpl());
        cardinality = listOps.occurrence(varsList, v, k);
        ConstraintClass linear;
		switch(p.getOperator(oper)) {
		case 1: {
			// Case = "equals".
			linear = cardinality.and(k.eq(value));
		} break;
		case 2: {
			// Case != "not equals".
			linear = cardinality.and(k.neq(value));
		} break;
		case 3: {
			// Case < "less".
			linear = cardinality.and(k.lt(value));
		}  break;
		case 4: {
			// Case <= "less equals".
			linear = cardinality.and(k.le(value));
		}  break;
		case 5: {
			// Case > "greater".
			linear = cardinality.and(k.gt(value));
		}  break;
		case 6: {
			// Case >= "greater equals".
			linear = cardinality.and(k.ge(value));
		}  break;
		default: throw new UnsupportedOperationException();
		}
		Constraint result = new Constraint(p, linear);
		((Solver) p.getSolver()).add(result);
		setImpl(cardinality);
		p.addAuxVariables(vars);
		p.addAuxVariable(k);
		p.addAuxVariable(v);
	}

	/**
	 * Build a new ConstraintClass that bind the cardinality of the variables
	 * of the given array of variables <code>vars</code> that satisfy the 
	 * constraint "oper <code>value</code>" to be equal the given
	 * integer variable <code>cardValue</code>. Where <code>oper</code> is a 
	 * string 
	 * representing the commons constraint operator.
	 * 
	 * @param vars the array of integer variables
	 * @param cardValue the cardinality
	 * @param oper the string representing the operator 
	 * @param value the value that the variable must be constrained to.
	 */
	public Cardinality(Var[] vars, Var cardValue, String oper, int value) {
		super(vars[0].getProblem());
		Problem p = (Problem) vars[0].getProblem();
		ConstraintClass cardinality;
		IntLVar v = new IntLVar(((IntLVar)cardValue.getImpl()));
		IntLVar k = new IntLVar(p.getFreshName()+"_card");
		SolverClass solver = ((Solver) p.getSolver()).getSolverClass();
		GlobalConstraints listOps = new GlobalConstraints(solver);
		Vector<IntLVar> varsList = new Vector<IntLVar>();
		for (int i = 0; i < vars.length; i++)
			varsList.add((IntLVar) vars[i].getImpl());
        cardinality = listOps.occurrence(varsList, v, k);
        ConstraintClass linear;
		switch(p.getOperator(oper)) {
		case 1: {
			// Case = "equals".
			linear  = cardinality.and(k.eq(value));
		} break;
		case 2: {
			// Case != "not equals".
			linear = cardinality.and(k.neq(value));
		} break;
		case 3: {
			// Case < "less".
			linear = cardinality.and(k.lt(value));
		}  break;
		case 4: {
			// Case <= "less equals".
			linear = cardinality.and(k.le(value));
		}  break;
		case 5: {
			// Case > "greater".
			linear = cardinality.and(k.gt(value));
		}  break;
		case 6: {
			// Case >= "greater equals".
			linear = cardinality.and(k.ge(value));
		}  break;
		default: throw new UnsupportedOperationException();
		}
		Constraint result = new Constraint(p, linear);
		((Solver) p.getSolver()).add(result);
		setImpl(cardinality);
		p.addAuxVariables(vars);
		p.addAuxVariable(k);
		p.addAuxVariable(cardValue);
	}
	
	public void post() {
		 getProblem().add(this);
	}
}
