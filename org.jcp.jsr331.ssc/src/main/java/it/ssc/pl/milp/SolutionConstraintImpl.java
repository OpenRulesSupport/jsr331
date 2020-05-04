package it.ssc.pl.milp;

final class SolutionConstraintImpl implements SolutionConstraint {
	private String name;
	private ConsType rel;
	private double bi;
	private double value;
	
	
	SolutionConstraintImpl( double[] Ai, double bi, ConsType rel, Var[] var) {
		for(int _i=0; _i< Ai.length ; _i++) {
			value+=Ai[_i]*var[_i].getValue();
		}
		this.rel=rel;
		this.bi=bi;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public ConsType getRel() {
		return rel;
	}
	public double getRhs() {
		return bi;
	}
	public double getValue() {
		return value;
	}
}
