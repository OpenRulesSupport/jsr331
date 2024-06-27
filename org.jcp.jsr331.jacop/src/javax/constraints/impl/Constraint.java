/**
 *  Constraint.java 
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

import java.util.ArrayList;

import javax.constraints.ConsistencyLevel;
import javax.constraints.Problem;

import JaCoP.constraints.And;
import JaCoP.constraints.IfThen;
import JaCoP.constraints.Not;
import JaCoP.constraints.Or;
import JaCoP.constraints.PrimitiveConstraint;
import JaCoP.constraints.Reified;

/**
 * @author Radoslaw Szymanek
 *
 */

public class Constraint extends AbstractConstraint {

	public Constraint(Problem problem) {
        super(problem);       
	}

	
	public Constraint(Problem problem, JaCoP.constraints.Constraint nativeConstraint) {
        super(problem);
        
        JaCoP.constraints.Constraint[] array = new JaCoP.constraints.Constraint[1];
        array[0] = nativeConstraint;
        
        setImpl(array);
        
	}

	
	public Constraint(Problem problem, JaCoP.constraints.Constraint[] nativeConstraints) {

		super(problem);        
        setImpl(nativeConstraints);
        
	}

	@Override
	public void post() {
		AbstractProblem p = (AbstractProblem) getProblem();
		p.post(this);
	}

	@Override
	public void post(ConsistencyLevel consistencyLevel) {
		AbstractProblem p = (AbstractProblem) getProblem();
		p.post(this);
	}
	
	@Override
	public VarBool asBool() {
		
		JaCoP.constraints.Constraint[] array = (JaCoP.constraints.Constraint[]) this.getImpl();
		ArrayList<PrimitiveConstraint> list = new ArrayList<PrimitiveConstraint>();
		
		for (JaCoP.constraints.Constraint current : array) {
			if (!(current instanceof PrimitiveConstraint))
				throw new RuntimeException("Constraint " + current + 
						" has no implementation for the method asBool()" +
						" It cannot be used within asBool().");
			else
				list.add((PrimitiveConstraint)current);
		}

		javax.constraints.VarBool auxilary = new VarBool((javax.constraints.impl.Problem)this.getProblem(), 0, 1);

		if (list.size() > 1) {
			new Constraint(this.getProblem(), new Reified(new And(list), (JaCoP.core.IntVar)auxilary.getImpl()) ).post();
		}
		else
			new Constraint(this.getProblem(), new Reified(list.get(0), (JaCoP.core.IntVar)auxilary.getImpl()) ).post();
			
		return (VarBool) auxilary;
		
	}
	
	@Override
	public Constraint and(javax.constraints.Constraint constraint) {
		
		JaCoP.constraints.Constraint[] array = (JaCoP.constraints.Constraint[]) constraint.getImpl();
		ArrayList<PrimitiveConstraint> list = new ArrayList<PrimitiveConstraint>();
		
		for (JaCoP.constraints.Constraint current : array) {
			if (!(current instanceof PrimitiveConstraint))
				throw new RuntimeException("Constraint " + current + 
						" has no implementation for the method and." +
						" It cannot be used in logical expressions.");
			else
				list.add((PrimitiveConstraint)current);
		}

		array = (JaCoP.constraints.Constraint[])this.getImpl();
		
		for (JaCoP.constraints.Constraint current : array) {
			if (!(current instanceof PrimitiveConstraint))
				throw new RuntimeException("Constraint " + current + 
						" has no implementation for the method and." +
						" It cannot be used in logical expressions.");
			else
				list.add((PrimitiveConstraint)current);
		}

		return new javax.constraints.impl.Constraint(getProblem(), new And(list));

	}

	@Override
	public Constraint or(javax.constraints.Constraint constraint) {
		
		JaCoP.constraints.Constraint[] array = (JaCoP.constraints.Constraint[]) constraint.getImpl();
		ArrayList<PrimitiveConstraint> list = new ArrayList<PrimitiveConstraint>();
		
		for (JaCoP.constraints.Constraint current : array) {
			if (!(current instanceof PrimitiveConstraint))
				throw new RuntimeException("Constraint " + current + 
						" has no implementation for the method and." +
						" It cannot be used in logical expressions.");
			else
				list.add((PrimitiveConstraint)current);
		}

		array = (JaCoP.constraints.Constraint[])this.getImpl();
		
		for (JaCoP.constraints.Constraint current : array) {
			if (!(current instanceof PrimitiveConstraint))
				throw new RuntimeException("Constraint " + current + 
						" has no implementation for the method and." +
						" It cannot be used in logical expressions.");
			else
				list.add((PrimitiveConstraint)current);
		}

		return new javax.constraints.impl.Constraint(getProblem(), new Or(list));

	}

	@Override
	public Constraint negation() {
		
		JaCoP.constraints.Constraint[] array = (JaCoP.constraints.Constraint[]) getImpl();
		ArrayList<PrimitiveConstraint> list = new ArrayList<PrimitiveConstraint>();
		
		for (JaCoP.constraints.Constraint current : array) {
			if (!(current instanceof PrimitiveConstraint))
				throw new RuntimeException("Constraint " + current + 
						" has no implementation for the method and." +
						" It cannot be used in logical expressions.");
			else
				list.add((PrimitiveConstraint)current);
		}

		if (list.size() > 1)
			return new javax.constraints.impl.Constraint(getProblem(), new Not(new And(list)));
		else 
			return new javax.constraints.impl.Constraint(getProblem(), new Not((PrimitiveConstraint)array[0]));			

	}

	@Override
	public Constraint implies(javax.constraints.Constraint constraint) {
		
		JaCoP.constraints.Constraint[] array = (JaCoP.constraints.Constraint[]) constraint.getImpl();
		JaCoP.constraints.PrimitiveConstraint[] arrayOfThen = new PrimitiveConstraint[array.length];
		int i = 0;
		
		for (JaCoP.constraints.Constraint current : array) {
			if (!(current instanceof PrimitiveConstraint))
				throw new RuntimeException("Constraint " + current + 
						" has no implementation for the method implies." +
						" It cannot be used in logical expressions.");
			else
				arrayOfThen[i++] = (PrimitiveConstraint)current;
		}

		array = (JaCoP.constraints.Constraint[])this.getImpl();
		JaCoP.constraints.PrimitiveConstraint[] arrayOfIf = new PrimitiveConstraint[array.length];		
		i = 0;
		
		for (JaCoP.constraints.Constraint current : array) {
			if (!(current instanceof PrimitiveConstraint))
				throw new RuntimeException("Constraint " + current + 
						" has no implementation for the method implies." +
						" It cannot be used in logical expressions.");
			else
				arrayOfIf[i++] = (PrimitiveConstraint)current;
		}
		 
		if (arrayOfIf.length == 1) {

			if (arrayOfThen.length == 1) {
				return new javax.constraints.impl.Constraint(getProblem(), new IfThen(arrayOfIf[0], arrayOfThen[0]));
			}
			else {
				return new javax.constraints.impl.Constraint(getProblem(), new IfThen(arrayOfIf[0], new And(arrayOfThen)));				
			}
			
		}
		else {

			if (arrayOfThen.length == 1) {
				return new javax.constraints.impl.Constraint(getProblem(), new IfThen(new And(arrayOfIf), arrayOfThen[0]));
			}
			else {
				return new javax.constraints.impl.Constraint(getProblem(), new IfThen(new And(arrayOfIf), new And(arrayOfThen)));				
			}

		}
		
	}
	
}
