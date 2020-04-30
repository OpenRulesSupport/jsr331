package javax.constraints.impl;

import javax.constraints.Problem;
import javax.constraints.impl.AbstractVarReal;
import javax.constraints.impl.Real;

public class VarReal extends AbstractVarReal implements javax.constraints.VarReal {

	double min; 
	double max;
	
	boolean usedInConstraints;
	
	public VarReal(Problem problem) {
		super(problem);
		usedInConstraints = false;
	}

	public VarReal(Problem problem, String name) {
		super(problem, name);
		usedInConstraints = false;
	}
	
	public boolean isDerived() {
		javax.constraints.impl.Problem p = 
				(javax.constraints.impl.Problem)getProblem();
		
		return p.isDerivedVar(this);
	}

	@Override
	public double getMin() {
		return min;
	}

	@Override
	public double getMax() {
		return max;
	}
	
	public void setMin(double min) {
		this.min = min;
	}

	public void setMax(double max) {
		this.max = max;
	}
	
	public void setValue(double value) {
		setMin(value);
		setMax(value);
	}

	@Override
	public boolean isBound() {
		// TODO Auto-generated method stub
		return Real.eq(getProblem(),getMin(),getMax());
	}

	public javax.constraints.VarReal negative() {
		VarReal var = new VarReal(getProblem());
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
    public javax.constraints.VarReal multiply(double value) {
        Problem p = getProblem();
        VarReal prod = (VarReal)p.scalProd(new double[]{value}, new VarReal[]{this});
        //p.add(name,prod);
        return prod;
    }

}
