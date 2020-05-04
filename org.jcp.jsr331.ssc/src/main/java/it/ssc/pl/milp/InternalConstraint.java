package it.ssc.pl.milp;

import it.ssc.log.SscLogger;

import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;

final class InternalConstraint implements Cloneable,Serializable {
	
	

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final Logger logger=SscLogger.getLogger();
	public enum TYPE_CONSTR {EQ, LE, GE}; 
	private double[] Ai;
	private double bi;
	private TYPE_CONSTR type;
	private String name;
	
	
	public InternalConstraint(int dimension) {
		Ai=new double[dimension];
	}
	
	public double[] getAi() {
		return Ai;
	}
	
	public void setAij(int j, Double aij) {
		Ai[j]=aij;
	}
	
	
	public void setAi(double[] Ai) {
		this.Ai=Ai;
	}
	
	
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double getAij(int j) {
		return Ai[j];
	}
	
	public void setBi(double bi) {
		this.bi = bi;
	}
	
	public double getBi() {
		return bi;
	}

	public TYPE_CONSTR getType() {
		return type;
	}
	
	public ConsType getConsType() {
		if(type==TYPE_CONSTR.EQ) return ConsType.EQ;
		else if(type==TYPE_CONSTR.LE)  return ConsType.LE;
		else if(type==TYPE_CONSTR.GE)  return ConsType.GE;
		else return null;
	}

	public void setType(TYPE_CONSTR type) {
		this.type = type;
	}
	
	public void standardize_b() {
		if(bi < 0.0) {
			bi=-bi;
			for(int j=0;j<Ai.length;j++) {
				if(Ai[j]!=0.0)  Ai[j]=-Ai[j];
			}
			if(type==TYPE_CONSTR.GE) type=TYPE_CONSTR.LE;
			else if(type==TYPE_CONSTR.LE) type=TYPE_CONSTR.GE;
		}
	}
	
	public static InternalConstraint createConstraintFromVar(int dim,int index,Double value, TYPE_CONSTR ltype) {
		InternalConstraint l_constraint =new InternalConstraint(dim);
		for(int j=0;j<dim;j++) {
			if(j==index)  l_constraint.Ai[j]=1.0;
			else l_constraint.Ai[j]=0.0;
			l_constraint.bi=value;
			//System.out.println("mmmm:"+value);
			l_constraint.type=ltype;
		}
		return l_constraint;
	}
	
	public void aprint() {
		for(int j=0;j<Ai.length;j++) {
			System.out.print(Ai[j]+"\t");
		}
		System.out.print(type+"\t");
		System.out.print(bi+"\n");
	}
	
	public InternalConstraint clone() {
		InternalConstraint clone=null;
		try {
			clone=(InternalConstraint)super.clone();
			clone.Ai=(double[])Ai.clone();
		} 
		catch (CloneNotSupportedException e) {
			logger.log(Level.SEVERE,"Clonazione it.ssc.pl.milp.InternalConstraint",e);
		}
		return clone;
	}
}
