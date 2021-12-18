package javax.constraints.impl.constraint;

import javax.constraints.Var;
import javax.constraints.impl.Constraint;
import javax.constraints.impl.Problem;

/**
 * Implements a global constraint that represent multiple cardinalities 
 * at the same time.
 * 
 * @see Cardinality
 * @author Fabio Biselli
 *
 */
public class GlobalCardinality extends Constraint {
	
	/**
	 * For each index i the number of times the value "values[i]" 
	 * occurs in the array "vars" should be cardMin[i] and cardMax[i] (inclusive) 
	 * @param vars array of constrained integer variables
	 * @param values array of integer values within domain of all vars
	 * @param cardMin array of integers that serves as lower bounds for values[i]
	 * @param cardMax array of integers that serves as upper bounds for values[i]
	 * Note that arrays values, cardMin, and cardMax should have the same size 
	 * otherwise a RuntimeException will be thrown
	 */
	public GlobalCardinality(
			Var[] vars, 
			int[] values, 
			int[] cardMin, 
			int[] cardMax) {
		super(vars[0].getProblem());
		Problem p = (Problem) vars[0].getProblem();
		int min = cardMin[0];
		int max = cardMax[0];
		for(int i = 0; i < cardMin.length; i++){
			if(cardMin[i] > cardMax[i]) {
				throw new RuntimeException("GlobalCardinality error: cardMin["+i+"] <= cardMax["+i+"]");
			}
			if (cardMin[i] < min)
				min = cardMin[i];
			if (cardMax[i] > max)
				max = cardMax[i];
		}
		Var[] cardinalityVars = new Var[values.length];
		for (int i = 0; i < cardinalityVars.length; i++) {
			cardinalityVars[i] = new javax.constraints.impl.Var(p, min, max);
			cardinalityVars[i].setName(p.getFreshName()+"card"+i);
		}
		GlobalCardinality result = new GlobalCardinality(vars,values,cardinalityVars);
		for (int i = 0; i < cardinalityVars.length; i++) {
			p.post(cardinalityVars[i],">=",cardMin[i]);
			p.post(cardinalityVars[i],"<=",cardMax[i]);
		}
		setImpl(result.getImpl());
		p.addAuxVariables(vars);
		p.addAuxVariables(cardinalityVars);
	}
	
	/**
	 * Build a new Constraint such as for each index i the number of time
	 * the values <code>value[i]</code> occurs in the array of variables
	 * <code>vars</code> is exactly <code>cardinalityVars</code>.
	 * 
	 * @param vars the array of integer variables
	 * @param values the integer values
	 * @param card the array of integer variables that represent 
	 * the cardinality the variable must be constrained to.
	 */
	public GlobalCardinality(
			Var[] vars, 
			int[] values, 
			Var[] card) {
		super(vars[0].getProblem());
		if (card.length != values.length) {
			throw new RuntimeException("GlobalCardinality error: arrays values and cardinalityVars do not have same size");
		}
		Problem p = (Problem) vars[0].getProblem();
		Constraint[] results = new Constraint[values.length];
		JSetL.Constraint r = new JSetL.Constraint();
		for (int i = 0; i < values.length; i++) {
			results[i] = new Cardinality(vars, values[i], "=", card[i]);
			r.and((JSetL.Constraint) results[i].getImpl());
		}
		Constraint result = new Constraint(p,r);
		setImpl(result.getImpl());
		p.addAuxVariables(vars);
		p.addAuxVariables(card);
	}
	
	public void post() {
		((Problem) getProblem()).post(this);
	}

}
