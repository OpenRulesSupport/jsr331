//=============================================
// J A V A  C O M M U N I T Y  P R O C E S S
// 
// J S R  3 3 1
// 
// Common Implementation
// 
//============================================= 
package javax.constraints.impl.constraint;


import javax.constraints.Var;
import javax.constraints.impl.AbstractConstraint;
import javax.constraints.impl.AbstractProblem;


/**
 * This is a linear constraints for a constrained integer variable that is equal to the maximal
 * variable in the array "vars" when they are all instantiated.
 */
public class ConstraintMax extends AbstractConstraint {

	String oper;
	Var[] vars;
	Var var;
	int value;
	Var maxVar;

	public ConstraintMax(Var[] vars, String oper, Var var) {
		super(vars[0].getProblem());
		this.vars = vars;
		this.oper = oper;
		this.var = var;
		createMaxVar();		
	}
	
	public ConstraintMax(Var[] vars, String oper, int value) {
		super(vars[0].getProblem());
		this.vars = vars;
		this.oper = oper;
		this.var = null;
		this.value = value;
		createMaxVar();		
	}
	
	void createMaxVar() {
		AbstractProblem p = (AbstractProblem)getProblem();
		int min = vars[0].getMin(); // the largest minimum
		int max = vars[0].getMax(); // the largest maximum
		for (int i = 1; i < vars.length; i++) {
			int mini = vars[i].getMin();
			int maxi = vars[i].getMax();
			if (min < mini)
				min = mini;
			if (max < maxi)
				max = maxi;
		}
		maxVar = p.variable("MaxArray", min, max);
		Var[] equalities = new Var[vars.length];
		for (int i = 0; i < vars.length; i++) {
			p.post(maxVar, ">=", vars[i]);
			equalities[i] = p.linear(maxVar,"=",vars[i]).asBool();
		}
		p.post(equalities, ">=", 1);
	}
	
	public void post() {
		if (var == null)
			getProblem().post(maxVar, oper, value);
		else 
			getProblem().post(maxVar, oper, var);
	}
	
	public Var getMaxVar() {
		return maxVar;
	}
	
}
