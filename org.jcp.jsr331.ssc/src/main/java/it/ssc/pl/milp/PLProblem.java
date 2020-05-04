package it.ssc.pl.milp;

import it.ssc.i18n.RB;
import it.ssc.log.SscLogger;
import it.ssc.pl.milp.ObjectiveFunction.TARGET_FO;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;


 final class PLProblem implements Costant , Cloneable, Serializable {
	 
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private static final Logger logger=SscLogger.getLogger();	
	private ObjectiveFunctionImpl fo;
	private ArrayList<InternalConstraint> list_constraint;  
	private Var[] array_var;
	private int new_dimension;
	private TARGET_FO target_fo= TARGET_FO.MAX;
		
	public ObjectiveFunctionImpl getObjFunction() {
		return fo;                                
	}

	public PLProblem(int dimension) {
		list_constraint=new ArrayList<InternalConstraint>(); 
		initArrayVar(dimension);
		fo=new ObjectiveFunctionImpl(dimension);
	}
	
	private void initArrayVar(int dimension) {
		array_var=new Var[dimension] ;
		for(int _j=0;_j< array_var.length;_j++) {
			array_var[_j]=new Var(); 
		}
	}
	
	public void setNameVar(int index,String name_var) {
		array_var[index].setName(name_var);
	}
	
	public void setCjOF(int index, Double value) {
		fo.setCj(index, value);
	}
	
	public void setTargetObjFunction(String target) {
		if(target.equalsIgnoreCase(MIN)) {
			fo.setType(ObjectiveFunctionImpl.TARGET_FO.MIN);
			target_fo= TARGET_FO.MIN;
		}
		else if(target.equalsIgnoreCase(MAX)) {
			fo.setType(ObjectiveFunctionImpl.TARGET_FO.MAX);
			target_fo= TARGET_FO.MAX;
		}	
	}
	
	public TARGET_FO getTarget_fo() {
		return target_fo;
	}

	public Var getVar(int index) {
		return array_var[index];
	}
	
	public Var[] getVariables() {
		return array_var; 
	}

	public void addConstraint(InternalConstraint constraint) {
		list_constraint.add(constraint);
	}
	
	public void standardize() {
		
		fo.standardize(); //standardizza la funzione obiettivo. Se MIN -> MAX
		
		//Aggiornare i valori di b con gli lower bound
		double aij;
		InternalConstraint constrainte ;
		Iterator<InternalConstraint> itr = list_constraint.iterator();
	    while (itr.hasNext()) {
	    	constrainte = itr.next();
			double cumulata=0;
			for(int _a=0;_a<array_var.length;_a++) { 
				aij=constrainte.getAij(_a);
				Double lower=array_var[_a].getLower();
				if(!Double.isNaN(lower) && lower!=0.0) {
					cumulata+= -(lower*aij);
				}  
			}
			constrainte.setBi(constrainte.getBi()+cumulata);
	    }
		
		//ciclo sulle variabili 
		//Se lower o upper != null -> vincoli 
		for(int _j=0;_j< array_var.length;_j++) {
			double lower=array_var[_j].getLower();
			double upper=array_var[_j].getUpper();
			double appo_lower=0;
			if(!Double.isNaN(lower) && lower!=0.0) {
				appo_lower=lower;
			}
			if(!Double.isNaN(upper)) {
				InternalConstraint constraint=InternalConstraint.createConstraintFromVar(
						array_var.length, _j, upper - appo_lower, InternalConstraint.TYPE_CONSTR.LE);
				//System.out.println("kkkk"+(upper - appo_lower));
				list_constraint.add(constraint);
			}
		}
		
		//da mettere alla fine 
		for(InternalConstraint constraint: list_constraint) {
			constraint.standardize_b();
		}
		
		/*
		for(InternalConstraint constraint: list_constraint) {
			constraint.aprint();
		}*/
		
		this.new_dimension=newDimensionProblemToPhase1();
	}
	
	
	public double []  getVectorC() {
		double C[]=new double[new_dimension];
		int index_cj=0;
		for(int _a=0;_a<array_var.length;_a++) {
			double cj=fo.getCj(_a);
			C[index_cj]=cj;
			index_cj++;
			if(array_var[_a].isFree()) {
				if(cj!=0.0) C[index_cj]=-cj;
				else C[index_cj]=0.0;
				index_cj++;
			}
		}
		return C;
	}
	
	public double[][]  getMatrixA() {  
		double Aij[][]=new double[list_constraint.size()][];
		int index_contr=0;
		int index_Ai=0;
		int index_slack=0;
		double aij;
		
		InternalConstraint constraint ;
		Iterator<InternalConstraint> itr = list_constraint.listIterator();
	    while (itr.hasNext()) {
	    	//System.out.println("->"+index_contr);
	    	constraint = itr.next();
	    	Aij[index_contr]=new double[new_dimension];
	    	//System.out.println(""+index_contr);
			index_Ai=0;
			for(int _a=0;_a<array_var.length;_a++) { 
				aij=constraint.getAij(_a);
				Aij[index_contr][index_Ai]=aij;
				index_Ai++;
				if(array_var[_a].isFree()) {
					if(aij!=0) Aij[index_contr][index_Ai]=-aij;
					else Aij[index_contr][index_Ai]=0.0;
					index_Ai++;
				}
			}
			
			if(index_slack==0) index_slack=index_Ai;
			if((constraint.getType()==InternalConstraint.TYPE_CONSTR.GE)) {
				Aij[index_contr][index_slack]=-1.0;
				index_slack++;
			}
			else if((constraint.getType()==InternalConstraint.TYPE_CONSTR.LE)) {
				Aij[index_contr][index_slack]=1.0;
				index_slack++;
			}
			
			index_contr++;
			//System.out.println(""+index_contr);
	        itr.remove();
	    }
		return Aij;
	}
	
	public double [] getVectorB() {
		double B[]=new double[list_constraint.size()];
		int index_b=0;
		for(InternalConstraint constraint: list_constraint) {
			B[index_b]=constraint.getBi();
			index_b++;
		}
		return B;
	}
	
	private int newDimensionProblemToPhase1() {
		int N=array_var.length;
		int N_free=0;
		int N_slacks=0;
		for(int _j=0;_j< array_var.length;_j++) {
			if(array_var[_j].isFree()) N_free++;
		}
		for(InternalConstraint constraint: list_constraint) {
			if(!(constraint.getType()==InternalConstraint.TYPE_CONSTR.EQ)) N_slacks++;
		}
		return N+N_free+N_slacks;
	}
	
	
	public PLProblem clone() {
		
		PLProblem clone=null;
		try {
			clone=(PLProblem)super.clone();
			clone.array_var=array_var.clone();
			for(int _a=0;_a<clone.array_var.length;_a++) {
				clone.array_var[_a]=array_var[_a].clone();
			}
			
			clone.fo=fo.clone();
			clone.list_constraint=new ArrayList<InternalConstraint>();
			
			for(InternalConstraint constra: list_constraint) {
				clone.list_constraint.add(constra.clone());
			}
		} 
		catch (CloneNotSupportedException e) {
			logger.log(Level.SEVERE,"Clonazione it.ssc.pl.milp.MilpProblem",e);
		}
		return clone;
	}
	
	public void configureInteger() throws LPException {
		boolean is_present_upper_or_lower_in_var_binary=false; 
		for(Var var:array_var) {
			if(var.getType()==Var.TYPE_VAR.BINARY) {
				if(!var.getLowerIsNaN() && var.getLower()!= 0.0) {
					is_present_upper_or_lower_in_var_binary=true; 
				}
				else if(!var.getUpperIsNaN() && var.getUpper()!= 1.0) {
					is_present_upper_or_lower_in_var_binary=true;
				}
				var.setUpper(1.0); //1.0
				var.setLower(0.0); //0.0
			}
		}
		if(is_present_upper_or_lower_in_var_binary) {
			logger.log(Level.WARNING,RB.getString("it.ssc.pl.milp.MilpProblem.msg1"));
		}
	}
	
	
	public void configureSemicont() throws LPException {
		
		for(Var var:array_var) {
			if(var.isSemicon()) { 
				double upper=var.getUpper();
				double lower=var.getLower();
				var.resetUpperLower();
				var.setUpperSemicon(upper);
				var.setLowerSemicon(lower);
				
				if((Double.isNaN(lower) || lower <= 0.0) && (Double.isNaN(upper) || upper >= 0.0)) {
					throw new LPException(RB.format("it.ssc.pl.milp.MilpProblem.msg2", var.getName()));
				}
			}
		}
	}

	public ArrayList<InternalConstraint> getListConstraint() {
		return list_constraint;
	}
}
