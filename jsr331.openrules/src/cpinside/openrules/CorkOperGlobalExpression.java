package cpinside.openrules;

/**
 * This class implements constrained expressions operators that can be used
 * inside OpenRules tables to create cp constraints defined on arays.
 * The current list of constraint operators includes:
 * SUM "sum"
 * SCALPROD "scalprod"
 * MIN min"
 * MAX "max"
 * ELEMENT "element"
 * CAR "card"
 *
 * @author VA
 *
 */

import javax.constraints.Var;
import javax.constraints.CSP;

public class CorkOperGlobalExpression {
	String name;

	ExpressionOperator operator;

	public CorkOperGlobalExpression(String s) {
		System.out.println("CorkOperBinaryConstraint: " + s);
		name = s;
		if (s.equals("sum"))
			operator = SUM;
		else if (s.equals("scalprod"))
			operator = SCALPROD;
		else if (s.equals("element"))
			operator = ELEMENT;
		else if (s.equals("min"))
			operator = MIN;
		else if (s.equals("max"))
			operator = MAX;
		else if (s.equals("card"))
			operator = CARD;
		else
			throw (new RuntimeException("Operator " + s + " is not defined"));
	}

	public Var createExpression(CSP p, String vars, String var,
			String value, String name) {
		Var[] array = p.getVarArray(vars);
		Var exp;
		if (var != null) {
			Var v = p.getVar(var);
			exp = operator.create(array, v);
		} else if (value != null) {
			int v = Integer.parseInt(value);
			exp = operator.create(array, v);
		} else {
			exp = operator.create(array);
		}
		exp.setName(name);
		p.add(exp);
		return exp;
	}

	public Var createExpression(CSP p, String vars, String name) {
		Var[] array = p.getVarArray(vars);
		Var exp = operator.create(array);
		exp.setName(name);
		p.add(exp);
		return exp;
	}

	static abstract class ExpressionOperator {
		public Var create(Var[] vars, Var var) {
			return null;
		}

		public Var create(Var[] vars, int i) {
			return null;
		}

		public Var create(Var[] vars) {
			return null;
		}

		public Var create(Var[] vars, int[] ints) {
			return null;
		}

		public Var create(int[] ints, Var var) {
			return null;
		}
	}

	static ExpressionOperator SUM = new ExpressionOperator() {
		@Override
		public Var create(Var[] vars) {
			return vars[0].getProblem().sum(vars);
		}
	};

	static ExpressionOperator SCALPROD = new ExpressionOperator() {
		@Override
		public Var create(Var[] vars, int[] ints) {
			return vars[0].getProblem().scalarProduct(ints, vars);
		}
	};

	static ExpressionOperator MIN = new ExpressionOperator() {
		@Override
		public Var create(Var[] vars) {
			return vars[0].getProblem().min(vars);
		}
	};

	static ExpressionOperator MAX = new ExpressionOperator() {
		@Override
		public Var create(Var[] vars) {
			return vars[0].getProblem().max(vars);
		}
	};

	static ExpressionOperator CARD = new ExpressionOperator() {
		@Override
		public Var create(Var[] vars, Var var) {
			try {
				return vars[0].getProblem().cardinality(vars, var);
			} catch (Exception e) {
				throw (new RuntimeException("ExpressionOperator " + toString()
						+ " cannot be created"));
			}
		}

		@Override
		public Var create(Var[] vars, int value) {
			try {
				return vars[0].getProblem().cardinality(vars, value);
			} catch (Exception e) {
				throw (new RuntimeException("ExpressionOperator " + toString()
						+ " cannot be created"));
			}
		}
	};

	static ExpressionOperator ELEMENT = new ExpressionOperator() {
		@Override
		public Var create(Var[] vars, Var var) {
			try {
				return vars[0].getProblem().elementAt(vars, var);
			} catch (Exception e) {
				throw (new RuntimeException("ExpressionOperator " + toString()
						+ " cannot be created"));
			}
		}

		@Override
		public Var create(int[] ints, Var var) {
			try {
				return var.getProblem().elementAt(ints, var);
			} catch (Exception e) {
				throw (new RuntimeException("ExpressionOperator " + toString()
						+ " cannot be created"));
			}
		}
	};

	@Override
	public String toString() {
		return name;
	}
}
