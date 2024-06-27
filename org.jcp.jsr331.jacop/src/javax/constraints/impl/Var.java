/**
 *  Var.java 
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

import java.util.Arrays;

import javax.constraints.extra.PropagationEvent;
import javax.constraints.extra.Propagator;

import JaCoP.constraints.AbsXeqY;
import JaCoP.constraints.XmulCeqZ;
import JaCoP.constraints.XmulYeqZ;
import JaCoP.constraints.XplusCeqZ;
import JaCoP.constraints.XplusYeqZ;
import JaCoP.core.IntDomain;
import JaCoP.core.IntVar;
import JaCoP.core.IntervalDomain;
import JaCoP.core.SmallDenseDomain;

/**
 * @author Radoslaw Szymanek
 *
 */

public class Var extends AbstractVar {

	public Var(Problem problem, int min, int max) {
		
		super(problem, "");
		IntVar variable = new IntVar(problem.store, min, max);
		this.setImpl(variable);
		this.setName(variable.id());
	}

	public Var(Problem problem, String name, int min, int max) {
		
		// TODO: If min=0 and max=1 then use BooleanVar instead of IntVar.
		super(problem, name);
		this.setImpl( new IntVar(problem.store, name, min, max) );

	}

	public Var(Problem problem, String name, int[] domain) {

		super(problem, name);

		Arrays.sort(domain);
				
		IntDomain dom = new IntervalDomain();
		
		for (int value : domain)
			dom.unionAdapt(value);
		
		if (dom.max() - dom.min() <= 63) {
			IntDomain smallDom = new SmallDenseDomain();
			((SmallDenseDomain)smallDom).addDom(dom);
			dom = smallDom;
		}
		
		this.setImpl( new IntVar(problem.store, name, dom) );
		
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
		assert false;
		throw new RuntimeException("Not implemented");
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
