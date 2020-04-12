package javax.constraints.impl.constraint;

import javax.constraints.Constraint;
import javax.constraints.Problem;
import javax.constraints.Var;
import javax.constraints.VarBool;
import javax.constraints.impl.AbstractConstraint;

public class ConstraintTable extends AbstractConstraint {
	
	Constraint constraint;
	
	//table constraint
	/**
	 * A value indicating that a tuple is to be excluded from satisfying a table constraint
	 */
	static public final int NOT_PERMITTED = 0;
	/**
	 * A value indicating that a tuple is to be included to satisfy a table constraint
	 */
	static public final int PERMITTED = 1;

	/**
	 * This constraint states that all variables from the array "vars" must 
	 * take only those values that are compatible with the two-dimensional 
	 * table of integers "table". 
	 * There are two constraint modes: PERMITTED and NOT_PERMITTED.
	 * 
	 * In the mode "PERMITTED", the variables in the array "vars" are compatible
	 * with a row of the table if:
	 * - the first variable is bound to the value in the first column of the row, 
	 * - the 2nd variable is bound to the value in the 2nd column 
	 * - etc for all variables and columns.
	 *  
	 * If it is a (not permitted) table, then the values the variables
	 * in "vars" take must not form any of the rows present in the table.
	 *
	 * @param vars the array of variables this table constraint constrains.
	 * @param table a 2-dimensional array of ints, where each row of the table represents a
	 *              tuple containing a value for all the variables in "vars".
	 * @param status - Constant.EXCLUDE indicating the tuples expressed in the table are not permitted,
	 *              or Constant.INCLUDE indicating the tuples expressed in the table are permitted.
	 * @return a Constraint enforcing that the variables in "vars" take values which are compatible
	 *         with the table "table".
	 * @throws Exception if table[i].length != vars.length for all i.
	 */
//	public ConstraintTable(Var[] vars, int[][] table)
//			throws Exception {
//		this(vars, table, NOT_PERMITTED);
//	}

	/**
	 * Creates and returns a new constraint stating that all variables from "vars"
	 * must take values compatible with the table "table",
	 * which represents the set of (permitted)/(not permitted) combinations.
	 * @param vars array of variables
	 * @param table a matrix
	 * @param mode - EXCLUDE or INCLUDE
	 */
	public ConstraintTable(Var[] vars, int[][] table, int mode) {
		super(vars[0].getProblem());
		
		Constraint c = null;
		int n = vars.length-1;
		VarBool[] equalities = new VarBool[n];
		Problem p = getProblem();
		for (int i = 0; i < n; i++) {
			equalities[i] = p.linear(vars[i],"=",vars[i+1]).asBool();
		}
		constraint = p.post(equalities, "<", n);
		if(mode == PERMITTED){
			Constraint tmpC = p.linear(vars[0], "=", table[0][0]);
			for (int i = 1; i < table[0].length; i++) {
				tmpC = tmpC.and(p.linear(vars[i],"=",table[0][i]));
			}
			constraint = tmpC;
			for (int i = 1; i < table.length; i++) {
				tmpC = p.linear(vars[0],"=",table[i][0]);
				for (int j = 1; j < table[i].length; j++) {
					tmpC = tmpC.and(p.linear(vars[j],"=",table[i][j]));
				}
				constraint = constraint.or(tmpC);
			}
		}
//		else{
//			if(status == EXCLUDE){
//				Constraint tmpC = vars[0].ne(table[0][0]);
//				for (int i = 1; i < table[0].length; i++) {
//					tmpC = or(tmpC, vars[i].ne(table[0][i]));
//				}
//				c = tmpC;
//				for (int i = 1; i < table.length; i++) {
//					tmpC = vars[0].ne(table[i][0]);
//					for (int j = 1; j < table[i].length; j++) {
//						tmpC = or(tmpC, vars[j].ne(table[i][j]));
//					}
//					c = and(c, tmpC);
//				}
//			}
//		}
	}
	
	public void post() {
		constraint.post();
	}

	/**
	 * @param args arguments
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
