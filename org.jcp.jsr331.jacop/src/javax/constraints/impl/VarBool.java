/**
 *  VarBool.java 
 *  This file is part of JaCoP.
 *
 *  JaCoP is a Java Constraint Programming solver. 
 *	
 *	Copyright (C) 2000-2008 Krzysztof Kuchcinski and Radoslaw Szymanek
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *  
 *  Notwithstanding any other provision of this License, the copyright
 *  owners of this work supplement the terms of this License with terms
 *  prohibiting misrepresentation of the origin of this work and requiring
 *  that modified versions of this work be marked in reasonable ways as
 *  different from the original version. This supplement of the license
 *  terms is in accordance with Section 7 of GNU Affero General Public
 *  License version 3.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package javax.constraints.impl;

import javax.constraints.extra.PropagationEvent;
import javax.constraints.extra.Propagator;

import JaCoP.constraints.AbsXeqY;
import JaCoP.constraints.XmulCeqZ;
import JaCoP.constraints.XmulYeqZ;
import JaCoP.constraints.XplusCeqZ;
import JaCoP.constraints.XplusYeqZ;
import JaCoP.core.BooleanVar;
import JaCoP.core.BoundDomain;
import JaCoP.core.IntDomain;
import JaCoP.core.IntVar;

/**
 * @author Radoslaw Szymanek
 *
 */

public class VarBool extends AbstractVar implements javax.constraints.VarBool {

	public VarBool(Problem problem) {
		
		super(problem, "");

		BooleanVar im = new BooleanVar(problem.store, new BoundDomain(0, 1));
		this.setImpl( im );
		this.setName(im.id());

	}

	public VarBool(Problem problem, int min, int max) {
		
		super(problem, "");
		
		assert (min >= 0) : "Values below 0 are not allowed for Boolean variable";
		assert (max <= 1) : "Values above 1 are not allowed for Boolean variable";
		
		BooleanVar im = new BooleanVar(problem.store, new BoundDomain(min, max));
		this.setImpl( im );
		this.setName(im.id());

	}

	public VarBool(Problem problem, String name, int min, int max) {
		
		super(problem, name);
		
		assert (min >= 0) : "Values below 0 are not allowed for Boolean variable";
		assert (max <= 1) : "Values above 1 are not allowed for Boolean variable";

		BooleanVar im = new BooleanVar(problem.store, name, new BoundDomain(min, max));
		this.setImpl( im );

	}

	public VarBool(Problem problem, String name) {
		
		super(problem, name);
				
		BooleanVar im = new BooleanVar(problem.store, name, new BoundDomain(0, 1));
		this.setImpl( im );

	}

	@Override
	public void addPropagator(Propagator propagator, PropagationEvent event) {
		// TODO Auto-generated method stub
		assert false;
		throw new RuntimeException("Not implemented");
	}

	@Override
	public javax.constraints.Var abs() {
		
		int max = Math.max(this.getMax(), Math.abs(this.getMin()));
		
		javax.constraints.impl.Var absVar = new javax.constraints.impl.Var( (Problem)problem, 0, max);
		
		javax.constraints.impl.Constraint constraint = new Constraint((Problem)problem, 
																	  new AbsXeqY((IntVar)this.getImpl(),
																			  	  (IntVar)absVar.getImpl()) );
		((Problem)problem).post( constraint );
		
		return absVar;
	
	}

	@Override
	public javax.constraints.Var plus(int value) {
		
		javax.constraints.Var auxilary;
		
		if (value >= 0) 
			auxilary = this.getProblem().variable(Math.min(IntDomain.MaxInt, this.getMin() + value), 
				Math.min(IntDomain.MaxInt, this.getMax() + value));
		else
			auxilary = this.getProblem().variable(Math.max(IntDomain.MinInt, this.getMin() + value), 
					Math.max(IntDomain.MinInt, this.getMax() + value));
		
		new javax.constraints.impl.Constraint(this.getProblem(), 
				new XplusCeqZ( (IntVar)this.getImpl(), 
							   value, 
							   (IntVar)auxilary.getImpl() )).post();

		return auxilary;

	}

	@Override
	public javax.constraints.Var plus(javax.constraints.Var var) {
		
		javax.constraints.Var auxilary = this.getProblem().variable(Math.max(IntDomain.MinInt, this.getMin()+var.getMin()), 
				Math.min(IntDomain.MaxInt, this.getMax() + var.getMax()));
				
		new javax.constraints.impl.Constraint(this.getProblem(), 
				new XplusYeqZ( (IntVar)this.getImpl(), 
							   (IntVar)var.getImpl(), 
							   (IntVar)auxilary.getImpl() )).post();

		return auxilary;
		
	}

	@Override
	public boolean contains(int value) {
		
		return ((BooleanVar)getImpl()).domain.contains(value);
		
	}

	@Override
	public int getMax() {
		
		return ((IntVar)getImpl()).max();

	}

	@Override
	public int getMin() {
		
		return ((IntVar)getImpl()).min();
		
	}

	@Override
	public boolean isBound() {
		
		return ((IntVar)getImpl()).singleton();

	}

	@Override
	public javax.constraints.Var multiply(int value) {
		
		javax.constraints.Var auxilary;
		
		if (value >= 0) 
			auxilary = this.getProblem().variable(Math.max(IntDomain.MinInt, this.getMin() * value), 
				Math.min(IntDomain.MaxInt, this.getMax() * value));
		else
			auxilary = this.getProblem().variable(Math.max(IntDomain.MinInt, this.getMax() * value), 
					Math.min(IntDomain.MaxInt, this.getMin() * value));
		
		new javax.constraints.impl.Constraint(this.getProblem(), 
				new XmulCeqZ( (IntVar)this.getImpl(), 
							   value, 
							   (IntVar)auxilary.getImpl() )).post();

		return auxilary;

	}

	@Override
	public javax.constraints.Var multiply(javax.constraints.Var var) {


		int value = var.getMin();
		int min;
		int max;
		
		if (value >= 0) {
			min = Math.max(IntDomain.MinInt, this.getMin() * value); 
			max = Math.min(IntDomain.MaxInt, this.getMax() * value);
		}
		else {
			min = Math.max(IntDomain.MinInt, this.getMax() * value); 
			max = Math.min(IntDomain.MaxInt, this.getMin() * value);
		}
		
		value = var.getMax();
		
		if (value >= 0) {
			min = Math.min(min, Math.max(IntDomain.MinInt, this.getMin() * value)); 
			max = Math.max(max, Math.min(IntDomain.MaxInt, this.getMax() * value));
		}
		else {
			min = Math.min(min, Math.max(IntDomain.MinInt, this.getMax() * value)); 
			max = Math.max(max, Math.min(IntDomain.MaxInt, this.getMin() * value));
		}

		javax.constraints.Var auxilary = this.getProblem().variable(min, max);
		
		new javax.constraints.impl.Constraint(this.getProblem(), 
				new XmulYeqZ( (IntVar)this.getImpl(), 
							   (IntVar)var.getImpl(), 
							   (IntVar)auxilary.getImpl() )).post();

		return auxilary;

	}

	@Override
	public void setName(String name) {
		
		((IntVar)this.getImpl()).id = name;
		super.setName(name);
		
	}
	
}
