package it.ssc.pl.milp;

import java.util.ArrayList;

final class SolutionImpl implements Solution {
	
	private Var[] variables;
	private PLProblem milp_original;
	private SolutionType type_solution;
	
	//Costruttore da usare sulla parte MILP
	SolutionImpl(SolutionType type_solution,PLProblem milp_originall,  int basis[],double values[], Var[] variables_deep) {
		this.type_solution=type_solution;
		this.milp_original=milp_originall;
		this.variables= milp_original.getVariables();
		passaZeroSemicontVar(variables_deep);
		int index_var=0;
		int index_base=0,index_p1_base=0;
		for(Var var:variables) {
			
			//se la variabile e' libera , vuol dire che e' negativa e  ha un lower bound indefiniti,  
			if(var.isFree()) {			
				double value_sum=0;
				if((index_base=getIndexInBase( basis,index_var))!=-1) {
					value_sum=values[index_base];
				}	
				if((index_p1_base=getIndexInBase( basis,index_var+1))!=-1) {
					value_sum-=values[index_p1_base];
				}
				var.setValue(value_sum);
				index_var++;
			}
			//La variabile non e' libera  ed e' in base e  quindi puo avere un lower bound, se ce l'ha occorre ritrasformare per rendere valore variabile effettivo
			else if((index_base=getIndexInBase( basis,index_var))!=-1) {
				
				if(var.isSemicon() && !var.isZeroSemicontVar()) {
					if(!Double.isNaN(var.getLowerSemicon()) && var.getLowerSemicon()!=0.0) {
						 var.setValue(values[index_base]+var.getLowerSemicon());
					 }
				}
				else if(!Double.isNaN(var.getLower()) && var.getLower()!=0.0) {
					 var.setValue(values[index_base]+var.getLower());
				 }
				else var.setValue(values[index_base]);
			}
			//la variabile non è in base , ma occorre sommarci il lower, in quanto se ha avuto valore zero, occorre sommarci il lower
			else if(!Double.isNaN(var.getLower()) && var.getLower()!=0.0) {
				var.setValue(var.getLower());
			}
			
			else if(var.isSemicon() && !var.isZeroSemicontVar()) {
				if(!Double.isNaN(var.getLowerSemicon()) && var.getLowerSemicon()!=0.0) {
					 var.setValue(var.getLowerSemicon());
				 }
			}
			index_var++;
		}
	}
	
	//Costruttore da usare sulla parte LP
	SolutionImpl(SolutionType type_solution,PLProblem milp_originall,  int basis[],double values[]) {
		
		this.type_solution=type_solution;
		this.milp_original=milp_originall;
		variables= milp_original.getVariables();
		int index_var=0;
		int index_base=0,index_p1_base=0;
		for(Var var:variables) {
			
			if(var.isFree()) {			
				double value_sum=0;
				if((index_base=getIndexInBase( basis,index_var))!=-1) {
					value_sum=values[index_base];
				}	
				if((index_p1_base=getIndexInBase( basis,index_var+1))!=-1) {
					value_sum-=values[index_p1_base];
				}
				var.setValue(value_sum);
				index_var++;
			}
			else if((index_base=getIndexInBase( basis,index_var))!=-1) {
				if(!Double.isNaN(var.getLower()) && var.getLower()!=0.0) {
					 var.setValue(values[index_base]+var.getLower());
				 }
				else var.setValue(values[index_base]);
			}
			//la variabile non è in base , ma occorre sommarci il lower
			else if(!Double.isNaN(var.getLower())&& var.getLower()!=0.0) {
				var.setValue(var.getLower());
			}
			index_var++;
		}
	
	}	
	
	private void passaZeroSemicontVar(Var[] variables_deep)  {
		int index_var=0;
		for(Var new_var:this.variables) {
			new_var.setZeroSemicontVar(variables_deep[index_var].isZeroSemicontVar());
			index_var++;
		}
	}
	
	public SolutionType getTypeSolution() {
		return type_solution;
	}
	
	public Var[] getVariables() {
		return this.variables;
	}
	
	private int  getIndexInBase( int basis[],int index) {
		for (int i = 0; i < basis.length; i++) {
			if(index== basis[i]) {
				return i;
			}
		}
		return -1;
	}
	
	public double getOptimumValue() {
		double z=0;
		ObjectiveFunctionImpl fo=this.milp_original.getObjFunction();
		for (int i = 0; i < variables.length; i++) {
			z+=variables[i].getValue()*fo.getCj(i);
		}
		return z;
	}
	
	
	public double getValue() {
		double z=0;
		ObjectiveFunctionImpl fo=this.milp_original.getObjFunction();
		for (int i = 0; i < variables.length; i++) {
			z+=variables[i].getValue()*fo.getCj(i);
		}
		return z;
	}

	
	public SolutionConstraint[] getSolutionConstraint() {
		
		ArrayList<InternalConstraint> list_const=milp_original.getListConstraint();
		SolutionConstraintImpl[] sol_const=new SolutionConstraintImpl[list_const.size()];
		int _i=0;
		for(InternalConstraint internal_const:list_const)  {			
			sol_const[_i++]=new SolutionConstraintImpl(internal_const.getAi(), 
														 internal_const.getBi(), 
														 internal_const.getConsType(), 
														 variables);
			String name=internal_const.getName();
			if(name==null) name="row"+_i;
			sol_const[_i-1].setName(name);
			
		}
		return sol_const;
	}
}
