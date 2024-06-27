package cpinside.openrules;

/**
 * This class implements all constraint operators that can be used
 * inside OpenRules tables to create new cp constraints.
 * The current list of constraint operators includes:
 * LT "<"
 * LE "<="
 * EQ "=" (note: use "'=" inside Excel cells)
 * NE "!="
 * GT ">"
 * GE ">="
 *
 * @author VA
 *
 */

import javax.constraints.Constraint;
import javax.constraints.Var;
import javax.constraints.CSP;

public class CorkOperBinaryConstraint {
	String 				name;
	ConstraintOperator 	operator;

	public CorkOperBinaryConstraint(String s) {
		//System.out.println("CorkOperBinaryConstraint: " + s);
		name = s;
		if (s.equals("<"))
			operator = LT;
		else if (s.equals("<="))
			operator = LE;
		else if (s.equals("="))
			operator = EQ;
		else if (s.equals("!="))
			operator = NE;
		else if (s.equals(">"))
			operator = GT;
		else if (s.equals(">="))
			operator = GE;
		else
			throw (new RuntimeException("Operator " + s + " is not defined"));
	}

	public Constraint addConstraint(CSP p, String var1,
			String var2, String value2, String name) {
		Var v1 = p.getVar(var1);
		Constraint cc;
		if (var2 != null) {
			Var v2 = p.getVar(var2);
			cc = constrain(v1, v2);
		} else {
			int v2 = Integer.parseInt(value2);
			cc = constrain(v1, v2);
		}
		cc.setName(name);
		p.add(cc);
		return cc;
	}

	public Constraint constrain(Var var1, Var var2) {
		return operator.constrain(var1, var2);
	}

	public Constraint constrain(Var var1, int i2) {
		return operator.constrain(var1, i2);
	}

	static interface ConstraintOperator {
		public Constraint constrain(Var var1, Var var2);

		public Constraint constrain(Var var1, int i2);
	}

	static ConstraintOperator LT = new ConstraintOperator() {
		public Constraint constrain(Var var1, Var var2) {
			return var1.lt(var2);
		}

		public Constraint constrain(Var var1, int i2) {
			return var1.lt(i2);
		}
	};

	static ConstraintOperator LE = new ConstraintOperator() {
		public Constraint constrain(Var var1, Var var2) {
			return var1.le(var2);
		}

		public Constraint constrain(Var var1, int i2) {
			return var1.le(i2);
		}
	};

	static ConstraintOperator GE = new ConstraintOperator() {
		public Constraint constrain(Var var1, Var var2) {
			return var1.ge(var2);
		}

		public Constraint constrain(Var var1, int i2) {
			return var1.ge(i2);
		}
	};

	static ConstraintOperator GT = new ConstraintOperator() {
		public Constraint constrain(Var var1, Var var2) {
			return var1.gt(var2);
		}

		public Constraint constrain(Var var1, int i2) {
			return var1.gt(i2);
		}
	};

	static ConstraintOperator EQ = new ConstraintOperator() {
		public Constraint constrain(Var var1, Var var2) {
			return var1.eq(var2);
		}

		public Constraint constrain(Var var1, int i2) {
			return var1.eq(i2);
		}
	};

	static ConstraintOperator NE = new ConstraintOperator() {
		public Constraint constrain(Var var1, Var var2) {
			return var1.ne(var2);
		}

		public Constraint constrain(Var var1, int i2) {
			return var1.ne(i2);
		}
	};

	@Override
	public String toString() {
		return name;
	}
}
