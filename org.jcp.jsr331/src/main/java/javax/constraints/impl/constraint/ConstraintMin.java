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
 * This is a linear constraint for a constrained integer variable that is equal to the minimal
 * variable in the array "vars" when they are all instantiated.
 */
public class ConstraintMin extends AbstractConstraint {

	String oper;
	Var[] vars;
	Var var;
	int value;
	Var minVar;

	public ConstraintMin(Var[] vars, String oper, Var var) {
		super(vars[0].getProblem());
		this.vars = vars;
		this.oper = oper;
		this.var = var;
		createMinVar();		
	}
	
	public ConstraintMin(Var[] vars, String oper, int value) {
		super(vars[0].getProblem());
		this.vars = vars;
		this.oper = oper;
		this.var = null;
		this.value = value;
		createMinVar();		
	}
	
	void createMinVar() {
		AbstractProblem p = (AbstractProblem)getProblem();
		int min = vars[0].getMin(); // the smallest minimum
		int max = vars[0].getMax(); // the smallest maximum
		for (int i = 1; i < vars.length; i++) {
			int mini = vars[i].getMin();
			int maxi = vars[i].getMax();
			if (min > mini)
				min = mini;
			if (max > maxi)
				max = maxi;
		}
		minVar = p.variable("MinArray", min, max);
		Var[] equalities = new Var[vars.length];
		for (int i = 0; i < vars.length; i++) {
			p.post(minVar, "<=", vars[i]);
			equalities[i] = p.linear(minVar,"=",vars[i]).asBool();
		}
		p.post(equalities, ">=", 1);
	}
	
	public void post() {
		if (var == null)
			getProblem().post(minVar, oper, value);
		else 
			getProblem().post(minVar, oper, var);
	}
	
	public Var getMinVar() {
		return minVar;
	}
	
}
