package javax.constraints.impl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.constraints.Constraint;
import javax.constraints.Problem;
import javax.constraints.Var;
import javax.constraints.VarSet;
import javax.constraints.impl.AbstractConstrainedVariable;
import javax.constraints.impl.constraint.ConstraintTrue;

/**
 * This class represents a basic implementation of constrained set variables.
 *
 */

public class BasicVarSet extends AbstractConstrainedVariable implements VarSet {

	Set valueSet;
	Var[] requiredVars;
	ValueVarMap valueVarMap; // values[i] -> requiredVars[i]
	Var cardinality;

	public BasicVarSet(Problem problem, int[] values, String name) throws Exception {
		super(problem, name);
		valueSet = new HashSet();
		for (int i = 0; i < values.length; i++) {
			valueSet.add(new Integer(values[i]));
		}
		init();
	}

	public BasicVarSet(Problem problem, int min, int max, String name) throws Exception {
		this(problem,new int[max-min+1],name);
	}
	
	public BasicVarSet(Problem problem, Set set, String name) throws Exception {
		super(problem, name);
		valueSet = set;
		init();
	}
	
	public void init() throws Exception {
		requiredVars = new Var[valueSet.size()];
		valueVarMap = new ValueVarMap();
		Iterator iter = valueSet.iterator();
		int i = 0;
		while (iter.hasNext()) {
			Integer valueObj = (Integer) iter.next();
			int value = valueObj.intValue();
			requiredVars[i] = problem.variable(name + "(" + value + ")",0, 1 );
			valueVarMap.put(valueObj, requiredVars[i]);
			i++;
		}
		cardinality = problem.variable("",0,requiredVars.length); // -1
		problem.postCardinality(requiredVars,1,"=",cardinality);
	}

	public Var[] getRequiredVars() {
		return requiredVars;
	}

	public void setEmpty(boolean flag) {
		if (flag)
			getProblem().post(cardinality,"=",0);
		else
			getProblem().post(cardinality,">=",1);
	}

	public boolean isBound() {
		for (int i = 0; i < requiredVars.length; i++) {
			if (!requiredVars[i].isBound())
				return false;
		}
		return true;
	}

	public Set getValue() throws Exception {
		if (!isBound())
			throw new Exception(
					"Attempt to get value of the unbound set variable "
							+ getName());
		return getRequiredSet();
	}

	public void setValue(Set set) throws Exception {
		Iterator iter = set.iterator();
		while (iter.hasNext()) {
			Integer valueObj = (Integer) iter.next();
			int value = valueObj.intValue();
			require(value);
		}
		valueVarMap.removeNotRequiredValues();
	}

	public Set getRequiredSet() {
		return valueVarMap.getRequiredSet();
	}

	public Set getPossibleSet() {
		return valueVarMap.getPossibleSet();
	}

	public boolean isPossible(int value) {
		Var var = valueVarMap.get(value);
		if (var == null)
			return false;
		if (var.getMax() == 0) // removed
			return false;
		return true;
	}

	public boolean isRequired(int value) {
		Var var = valueVarMap.get(value);
		if (var == null)
			return false;
		if (var.getMax() == 1)
			return true;
		return false;
	}

	public Var getRequiredVar(int value) throws Exception {
		Var var = valueVarMap.get(value);
		if (var == null)
			throw new Exception("Value " + value + " is not in domain of "
					+ getName());
		return var;
	}

	public void remove(int value) throws Exception {
		Var var = getRequiredVar(value);
		getProblem().post(var,"=",0);
	}

	public void require(int value) throws Exception {
		Var var = getRequiredVar(value);
		getProblem().post(var,"=",1);
	}

	public boolean contains(Set setOfValues) {
		if (!valueSet.containsAll(setOfValues))
			return false;
		Iterator iter = setOfValues.iterator();
		while (iter.hasNext()) {
			int value = ((Integer) iter.next()).intValue();
			if (!isPossible(value))
				return false;
		}
		return true;
	}

	public Var getCardinality() {
		return cardinality;
	}

	public Constraint doNotIntersectWith(VarSet varSet) throws Exception {
		VarSet intersection = intersection(varSet);
		if (intersection == null)
			return new ConstraintTrue(getProblem()); // nothing to post
		return getProblem().post(intersection.getCardinality(),"=",0);
	}

	public Set getValueSet() {
		return valueSet;
	}

	public VarSet intersection(VarSet varSet) throws Exception {

		Set values1 = this.getValueSet();
		Set values2 = ((BasicVarSet) varSet).getValueSet();

		int[] commonValues = new int[values1.size()];
		Iterator iter = values1.iterator();
		int numberOfCommonValues = 0;
		while (iter.hasNext()) {
			Integer currentValue = (Integer) iter.next();
			if (values2.contains(currentValue)) {
				commonValues[numberOfCommonValues++] = currentValue.intValue();
			}
		}
		if (numberOfCommonValues == 0)
			return null;

		int[] intersection = new int[numberOfCommonValues];
		System.arraycopy(commonValues, 0, intersection, 0, numberOfCommonValues);
		Problem p = getProblem();
		String name = getName() + "&" + varSet.getName();
		BasicVarSet intersectionVarSet = new BasicVarSet(p, intersection, name);
		for (int i = 0; i < intersection.length; i++) {
			int value = intersection[i];
			Var var1 = getRequiredVar(value);
			Var var2 = ((BasicVarSet) varSet).getRequiredVar(value);
			Var var3 = intersectionVarSet.getRequiredVar(value);
			Constraint c11 = p.linear(var1,"=",1);
			Constraint c21 = p.linear(var2,"=",1);
			Constraint c31 = p.linear(var3,"=",1);
			p.postIfThen(c11.and(c21), c31);
			Constraint c10 = p.linear(var1,"=",0);
			Constraint c20 = p.linear(var2,"=",0);
			Constraint c30 = p.linear(var3,"=",0);
			p.postIfThen(c10.or(c20), c30);
		}
		return intersectionVarSet;
	}

	public VarSet union(VarSet varSet) {
		getProblem().log("==== union is not implemented");
		return null;
	}

	public String toString() {
		return "VarSet " + getName() + cardinality + ":" + valueVarMap.toString();
	}

	class ValueVarMap {
		HashMap map;

		ValueVarMap() {
			map = new HashMap();
		}

		void put(int value, Var var) {
			map.put(new Integer(value), var);
		}

		void put(Integer value, Var var) {
			map.put(value, var);
		}

		Var get(int value) {
			return (Var) map.get(new Integer(value));
		}

		Var get(Integer value) {
			return (Var) map.get(value);
		}

		public Set getRequiredSet() {
			HashSet requiredSet = new HashSet();
			Set entries = map.entrySet();
			Iterator it = entries.iterator();
			while (it.hasNext()) {
				Map.Entry entry = (Map.Entry) it.next();
				// System.out.println(entry.getKey() + "-->" + entry.getValue());
				Var var = (Var) entry.getValue();
				if (var.getMin() == 1) { // instantiated with 1
					requiredSet.add(entry.getKey());
				}
			}
			return requiredSet;
		}

//		public void removeNotRequiredValues() {
//			ArrayList toBeRemoved = new ArrayList();
//			Set entries = map.entrySet();
//			Iterator it = entries.iterator();
//			while (it.hasNext()) {
//				Map.Entry entry = (Map.Entry) it.next();
//				Var var = (Var) entry.getValue();
//				if (var.getMin() != 1) { // not instantiated with 1
//					toBeRemoved.add(entry);
//				}
//			}
//			for (int i = 0; i < toBeRemoved.size(); i++) {
//				entries.remove(toBeRemoved.get(i));
//			}
//		}

		public void removeNotRequiredValues() throws Exception {
			Set entries = map.entrySet();
			Iterator it = entries.iterator();
			while (it.hasNext()) {
				Map.Entry entry = (Map.Entry) it.next();
				Var var = (Var) entry.getValue();
				if (var.getMin() != 1) { // not instantiated with 1
					getProblem().post(var,"=",0);
				}
			}
		}

		public Set getPossibleSet() {
			HashSet possibleSet = new HashSet();
			Set entries = map.entrySet();
			Iterator it = entries.iterator();
			while (it.hasNext()) {
				Map.Entry entry = (Map.Entry) it.next();
				// System.out.println(entry.getKey() + "-->" + entry.getValue());
				Var var = (Var) entry.getValue();
				if (var.getMax() == 1) {
					possibleSet.add(entry.getKey());
				}
			}
			return possibleSet;
		}
		
		public String toString() {
			StringBuffer buf = new StringBuffer();
			Set entries = map.entrySet();
			Iterator it = entries.iterator();
			while (it.hasNext()) {
				Map.Entry entry = (Map.Entry) it.next();

				Var var = (Var) entry.getValue();
				if (var.getMin() == 1) { // instantiated with 1
					buf.append(" ["+ entry.getKey() + "]"); // required
				}
				else {
					buf.append(" "+ entry.getKey()); // possible
				}
			}
			return buf.toString();
		}
	}

}
