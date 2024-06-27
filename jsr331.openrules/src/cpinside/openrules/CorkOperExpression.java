package cpinside.openrules;

/**
 * This class implements all operators that can be used
 * inside OpenRules tables that define constrained integer expression
 * define by cp. The current list of operators includes:
 * ADD "+"
 * SUB "-"
 * MUL "*"
 * DIV "/"
 * MOD "mod" for divide by module
 * ABS "abs" for absolute value
 * NEG "neg" for negation
 *
 * @author VA
 *
 */

import javax.constraints.Var;
import javax.constraints.CSP;

public class CorkOperExpression {
	String 				name;
	ExpressionOperator 	operator;
	boolean 			binary;

	public CorkOperExpression(String s) {
		name = s;
		setBinary(true);
		if (s.equals("+"))
			operator = ADD;
		else if (s.equals("-"))
			operator = SUB;
		else if (s.equals("*"))
			operator = MUL;
		else if (s.equals("/"))
			operator = DIV;
		else if (s.equals("abs")) {
			operator = ABS;
			setBinary(false);
		} else if (s.equals("neg")) {
			operator = NEG;
			setBinary(false);
		} else if (s.equals("MOD"))
			operator = MOD;
		else
			throw (new RuntimeException("CorkOperExpression " + s
					+ " is not defined"));
	}

	public Var createExpression(CSP p, String var1, String var2,
			String value2, String name) {
		Var v1 = p.getVar(var1);
		Var exp;
		if (isBinary()) {
			if (var2 != null) {
				Var v2 = p.getVar(var2);
				exp = create(v1, v2);
			} else {
				int v2 = Integer.parseInt(value2);
				exp = create(v1, v2);
			}
		} else
			exp = create(v1);
		return createExpression(p,exp,name);
	}

	public Var createExpression(CSP p, Var exp, String name) {
		Var result = p.addVar(name, exp.getMin(), exp.getMax());
		result.eq(exp).post(); // important to post!
		return result;
	}

	public Var createBinaryExpression(CSP p, String var1, String var2, String name) {
		Var v1 = p.getVar(var1);
		if (!isBinary())
			throw (new RuntimeException("CorkOperExpression " + toString() + " should be binary"));
		Var v2 = p.getVar(var2);
		Var exp = create(v1, v2);
		return createExpression(p,exp,name);
	}

	public Var createUnaryExpression(CSP p, String var, String name) {
		Var v = p.getVar(var);
		if (isBinary())
			throw (new RuntimeException("CorkOperExpression " + toString() + " should be unary"));
		Var	exp = create(v);
		return createExpression(p,exp,name);
	}

	public Var createValueExpression(CSP p, String var1, String value2, String name) {
		Var v1 = p.getVar(var1);
		if (!isBinary())
			throw (new RuntimeException("CorkOperExpression " + toString() + " should be binary"));

		int v2 = Integer.parseInt(value2);
		Var exp = create(v1, v2);
		return createExpression(p,exp,name);
	}

	public boolean isBinary() {
		return binary;
	}

	public void setBinary(boolean binary) {
		this.binary = binary;
	}



	public Var create(Var var1, Var var2) {
		return operator.create(var1, var2);
	}

	public Var create(Var var1, int i2) {
		return operator.create(var1, i2);
	}

	public Var create(Var var) {
		return operator.create(var);
	}

	static abstract class ExpressionOperator {
		public Var create(Var var1, Var var2) {
			return null;
		}

		public Var create(Var var1, int i2) {
			return null;
		}

		public Var create(Var var) {
			return null;
		}
	}

	static ExpressionOperator ADD = new ExpressionOperator() {
		@Override
		public Var create(Var var1, Var var2) {
			return var1.add(var2);
		}

		@Override
		public Var create(Var var1, int i2) {
			return var1.add(i2);
		}
	};

	static ExpressionOperator SUB = new ExpressionOperator() {
		@Override
		public Var create(Var var1, Var var2) {
			return var1.sub(var2);
		}

		@Override
		public Var create(Var var1, int i2) {
			return var1.sub(i2);
		}
	};

	static ExpressionOperator MUL = new ExpressionOperator() {
		@Override
		public Var create(Var var1, Var var2) {
			return var1.mul(var2);
		}

		@Override
		public Var create(Var var1, int i2) {
			return var1.mul(i2);
		}
	};

	static ExpressionOperator DIV = new ExpressionOperator() {
		@Override
		public Var create(Var var1, Var var2) {
			try {
				return var1.div(var2);
			} catch (Exception e) {
				throw new RuntimeException("CorkOperExpression: divide by zero");
			}
		}

		@Override
		public Var create(Var var1, int i2) {
			return var1.div(i2);
		}
	};

	static ExpressionOperator MOD = new ExpressionOperator() {
		@Override
		public Var create(Var var1, Var var2) {
			throw (new RuntimeException(
					"CorkOperExpression mod(Var,Var) is not defined"));
		}

		@Override
		public Var create(Var var1, int i2) {
			return var1.mod(i2);
		}
	};

	static ExpressionOperator ABS = new ExpressionOperator() {
		@Override
		public Var create(Var var) {
			return var.abs();
		}
	};

	static ExpressionOperator NEG = new ExpressionOperator() {
		@Override
		public Var create(Var var) {
			return var.neg();
		}
	};

	@Override
	public String toString() {
		return name;
	}

}
