package javax.constraints.impl;

import javax.constraints.Problem;
import javax.constraints.impl.AbstractVar;

public class Var extends AbstractVar implements javax.constraints.Var {

	int min;
	int max;
	int numberOfPlusVars;
	
	boolean usedInConstraints;
	
	public Var(Problem problem) {
		super(problem);
		usedInConstraints = false;
		numberOfPlusVars = 0;
	}

	public Var(Problem problem, String name) {
		super(problem, name);
		usedInConstraints = false;
		numberOfPlusVars = 0;
	}
	
	public boolean isDerived() {
		javax.constraints.impl.Problem p = 
				(javax.constraints.impl.Problem)getProblem();
		
		return p.isDerivedVar(this);
	}

	@Override
	public int getMin() {
		return min;
	}

	@Override
	public int getMax() {
		return max;
	}
	
	public void setMin(int min) {
		this.min = min;
	}

	public void setMax(int max) {
		this.max = max;
	}
	
	public void setValue(int value) {
		setMin(value);
		setMax(value);
	}

	@Override
	public boolean isBound() {
		// TODO Auto-generated method stub
		return (getMin() == getMax());
	}

	public javax.constraints.Var negative() {
		Var var = new Var(getProblem());
		var.setMin(-getMax());
		var.setMax(-getMin());
		var.setName("-"+getName());
		return var;
	}


	public boolean isUsedInConstraints() {
		return usedInConstraints;
	}

	public void setUsedInConstraints(boolean usedInConstraints) {
		this.usedInConstraints = usedInConstraints;
	}
	
	@Override
	public javax.constraints.Var plus(int value) { 
		//throw new RuntimeException("There is no implementation for the Var method plus(int value)");
		Problem p = getProblem();
		numberOfPlusVars++;
		String name = getName()+"+"+numberOfPlusVars;
		int min = getMin();
		int max = getMax();
		if (value > 0) {
			if (min+value > max)
				min = max;
			max = max + value;
		}
		Var var = (Var)p.variable(name,getMin()+value, getMax()+value);
		Var scalProd = (Var)p.scalProd(new int[]{1,-1}, new Var[]{this,var});
		p.post(scalProd, "=", value);
		return var;
	}

	@Override
	public javax.constraints.Var plus(javax.constraints.Var var) {
		//throw new RuntimeException("There is no implementation for the Var method plus(Var var)");
		Problem p = getProblem();
		numberOfPlusVars++;
//		String name = getName()+"+"+var.getName();
		javax.constraints.Var sum = (javax.constraints.Var)p.sum(new javax.constraints.Var[]{this,var});
		//p.add(name,sum);
		return sum;
	}
	
	@Override
	public javax.constraints.Var multiply(int value) {
		//throw new RuntimeException("There is no implementation for the Var method multiply(int value)");
		Problem p = getProblem();
		numberOfPlusVars++;
//		String name = getName()+"+"+numberOfPlusVars;
		Var prod = (Var)p.scalProd(new int[]{value}, new Var[]{this});
		//p.add(name,prod);
		return prod;
	}


}
