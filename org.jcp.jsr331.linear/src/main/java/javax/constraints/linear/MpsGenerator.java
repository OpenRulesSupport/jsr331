package javax.constraints.linear;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.text.DecimalFormat;

import javax.constraints.ConstrainedVariable;
import javax.constraints.Constraint;
import javax.constraints.Var;
import javax.constraints.VarReal;
import javax.constraints.impl.AbstractConstrainedVariable;
import javax.constraints.impl.Problem;

public class MpsGenerator {

	Problem problem;

	int objectiveDirection;

	AbstractConstrainedVariable objectiveVar;

	private String[] rowNames;

	private String[] rowTypes; // N, E, L, G

	private double[] rowValues; // The right hand side value of each row

	// Counter for the number of rows, used when naming them
	private int rowCount = 1;

	// The row types
	// objective
	public final static String OBJ = "N";

	public final static String OBJ_ID = "_OBJ_";

	// column start points:
	public final static int[] colStarts = { 0, 4, 14, 24, 39, 49 };

	public final static int[] colLengths = { 10, 15 };

	private boolean integerVariablesOnly;

	// max variable name size
	private int maxNameSize;

	BufferedWriter output;

	public MpsGenerator(Problem problem, int objectiveDirection,
			AbstractConstrainedVariable objectiveVar, File file) {
		this.problem = problem;
		this.objectiveDirection = objectiveDirection;
		this.objectiveVar = objectiveVar;
		
		System.out.println("MpsGenerator for file " + file.getPath());

		setIntegerVariablesOnly(false);

		Constraint[] constraints = problem.getConstraints();
		rowNames = new String[constraints.length];
		rowTypes = new String[constraints.length];
		rowValues = new double[constraints.length];

		maxNameSize = 0;
		Var[] vars = problem.getVars();
		if (vars != null)
			for (int i = 0; i < vars.length; i++) {
				if (vars[i].getName().length() > maxNameSize) {
					maxNameSize = vars[i].getName().length();
				}
			}

		VarReal[] varReals = problem.getVarReals();
		if (varReals != null) {
			for (int i = 0; i < varReals.length; i++) {
				if (varReals[i].getName().length() > maxNameSize) {
					maxNameSize = varReals[i].getName().length();
				}
			}
		}

		double precision = 1; //0.00000001;
		if (isIntegerVariablesOnly())
			precision = 1;
		for (int i = 0; i < constraints.length; i++) {
			javax.constraints.impl.Constraint c = (javax.constraints.impl.Constraint) constraints[i];
			rowNames[i] = c.getName();
			rowValues[i] = c.getValue();
			String oper = c.getOper();
			if (">".equals(oper)) {
				oper = ">=";
				rowValues[i] += precision;
			}
			if ("<".equals(oper)) {
				oper = "<=";
				rowValues[i] -= precision;
			}
			rowTypes[i] = getRowType(oper);
			
			// AbstractConstrainedVariable derivedVar = c.getDerivedVar();
			// if (derivedVar != null) {
			// //rowNames[i] = derivedVar.getName();
			// rowTypes[i] = "N";
			// }
		}
		try {
			output = new BufferedWriter(new FileWriter(file));
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(
					"MpsGenerator cannot write to the given file");
		}
	}

	public int getRowCount() {
		return rowCount;
	}

	public void incrementRowCount() {
		rowCount++;
	}

	void write(String text) {
		try {
			output.write(text);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(
					"MpsGenerator: error writing to the given file");
		}
	}

	public boolean isIntegerVariablesOnly() {
		return integerVariablesOnly;
	}

	public void setIntegerVariablesOnly(boolean integerVariablesOnly) {
		this.integerVariablesOnly = integerVariablesOnly;
	}

	/**
	 * Creates a .mps format representation of this linear problem and places it
	 * within the passed file parameter of the constructor.
	 * 
	 */
	public void generate() {
		long startTime = System.currentTimeMillis();
		try {
			// FileWriter always assumes default encoding is OK!
			write("NAME          " + problem.getName() + "\n");
			// calculate what columns things go in, as excessively long variable
			// names can reach into next column
			int pos = 1;
			// +4 for the first column, and +1 for the space after the name ends
			// before a new column can start
			while (maxNameSize + 5 > colStarts[++pos])
				;
			writeRows();
			writeColumns(colStarts[pos]);
			writeRHS(colStarts[pos]);
			writeBounds(colStarts[pos]);
			write("ENDATA");
			write("\n");
		} finally {
			try {
				output.close();
			} catch (Exception e) {
				e.printStackTrace();
				throw new RuntimeException(
						"MpsGenerator: cannot close the given file");
			}
		}
		long executionTime = System.currentTimeMillis() - startTime;
		System.out.println("MPS file generated in " + executionTime + " msec");
	}

	private void writeRows() {
		write("ROWS\n");
		if (!problem.isDerivedVar(objectiveVar))
			//throw new RuntimeException
			System.out.println("Objective variable "
					+ objectiveVar.getName()
					+ " is not derived from other variables");
		// write(" N  " + objectiveVar.getName() + "\n");
		write(" N  " + OBJ_ID + "\n");

		Constraint[] constraints = problem.getConstraints();
		for (int i = 0; i < constraints.length; i++) {
			// javax.constraints.impl.Constraint c =
			// (javax.constraints.impl.Constraint) constraints[i];
			// if (objectiveVar == c.getDerivedVar())
			// continue;
			write(" " + rowTypes[i] + "  " + rowNames[i] + "\n");
		}
	}

	private void writeColumns(int thirdColStart) {
//		int markerCount = 0;
		write("COLUMNS");

		Constraint[] constraints = problem.getConstraints();
		Var[] vars = problem.getVars();
		
		if (vars != null) {
			startIntegerMarker(0);
			for (int i = 0; i < vars.length; i++) {
				javax.constraints.impl.Var var = (javax.constraints.impl.Var) vars[i];
				var.setId("Vi"+i);
				var.setUsedInConstraints(false);
				int count = 0;
				// One variable can participate in different constraints, but only once in the same constraint!
				for (int j = 0; j < constraints.length; j++) {
					javax.constraints.impl.Constraint c = (javax.constraints.impl.Constraint) constraints[j];
					double[] constraintCoefficients = c.getCoefficients();
					ConstrainedVariable[] constraintVars = c.getVars();
					boolean[] written = new boolean[constraintCoefficients.length];
					for (int w = 0; w < written.length; w++) {
						written[w] = false;
					}
					for (int k = 0; k < constraintVars.length; k++) {
						if (var.equals(constraintVars[k])) {
							if (var.isUsedInConstraints() == false) {
								var.setUsedInConstraints(true);
								// if (isIntegerVariablesOnly()) {
								// // open Marker for Integer variable
								// startIntegerMarker(markerCount);
								// markerCount++;
								// }
							}

							if (count % 2 == 0) // new line every 2 rows
							{
								String str = "    " + var.getId(); //var.getName();
								str = rightPadWhitespace(str, thirdColStart);
								write("\n" + str);
							}
							String rowName = c.getName();
							double d = (double) constraintCoefficients[k];
							writeColumn(rowName, d);
							
							count++;
							break; // 3/17/2019

						} // if
					} // for k ()constrained variables for this constraint
				} // for j (all constraints)
					// if (var.isUsedInConstraints() == true) {
					// if (isIntegerVariablesOnly()) {
					// // close marker
					// endIntegerMarker(markerCount);
					// markerCount++;
					// }
					// }
			} // for i (all variables)
			endIntegerMarker(1);
		}

		VarReal[] varReals = problem.getVarReals();
		if (varReals != null) {
			for (int i = 0; i < varReals.length; i++) {
				javax.constraints.impl.VarReal var = (javax.constraints.impl.VarReal) varReals[i];
				var.setId("Vr"+i);
				var.setUsedInConstraints(false);
				int count = 0;
				for (int j = 0; j < constraints.length; j++) {
					javax.constraints.impl.Constraint c = (javax.constraints.impl.Constraint) constraints[j];
					double[] constraintCoefficients = c.getCoefficients();
					ConstrainedVariable[] constraintVars = c.getVars();
					for (int k = 0; k < constraintVars.length; k++) {
						if (var.equals(constraintVars[k])) {
							if (var.isUsedInConstraints() == false) {
								var.setUsedInConstraints(true);
							}
//							else
//								continue; // JF added on 3/17/2019
							
							if (count % 2 == 0) // new line every 2 rows
							{
								String str = "    " + var.getId(); //var.getName();
								str = rightPadWhitespace(str, thirdColStart);
								write("\n" + str);
							}
							String rowName = c.getName();
							double d = (double) constraintCoefficients[k];
							writeColumn(rowName, d);
							count++;
							break; // 3/17/2019
						} // if
					} // for k ()constrained variables for this constraint
				} // for j (all constraints)
			} // for i (all variables)
		}

		// Add a column for cost referring to OBJ_ID
		// if (isIntegerVariablesOnly()) {
		// startIntegerMarker(markerCount);
		// markerCount++;
		// }
		String str = "    " + objectiveVar.getId(); //objectiveVar.getName();
		str = rightPadWhitespace(str, thirdColStart);
		write("\n" + str);
		writeColumn(OBJ_ID, objectiveDirection);
		// if (isIntegerVariablesOnly()) {
		// endIntegerMarker(markerCount);
		// markerCount++;
		// }
		// endIntegerMarker(1);
		write("\n");
	}

	private void writeColumn(String rowName, double d) {
		rowName = rightPadWhitespace(rowName, colLengths[0]);
		String rCoeff = "";
		if (Math.floor(d) == d) // if it's an integer
		{
			rCoeff += ((int) d) + "   ";
		} else {
			rCoeff += format(d) + "   ";
		}
		rCoeff = leftPadWhitespace(rCoeff, colLengths[1]);
		write(rowName + rCoeff);
	}

	private void startIntegerMarker(int markerCount) {
		String marker = "" + markerCount;
		while (marker.length() < 4) {
			marker = "0" + marker;
		}
		marker = "\n    M" + marker + "     'MARKER'                 'INTORG'";
		write(marker);
	}

	private void endIntegerMarker(int markerCount) {
		String marker = "" + markerCount;
		while (marker.length() < 4) {
			marker = "0" + marker;
		}
		marker = "\n    M" + marker + "     'MARKER'                 'INTEND'";
		write(marker);
	}

	private void writeRHS(int thirdColStart) {
		write("RHS");
		int count = 0;
		for (int r = 0; r < rowNames.length; r++) {
			String rName = rowNames[r];
			rName = rightPadWhitespace(rName, colLengths[0]);

			double d = rowValues[r];
			String rRHS = "";

			if (Math.floor(d) == d) // if it's an integer
			{
				rRHS += ((int) d) + "   ";
			} else {
				rRHS += format(d) + "   ";
			}

			rRHS = leftPadWhitespace(rRHS, colLengths[1]);
			if (d > 0) {
				if (count % 2 == 0) // new line every 2 rows
				{
					String str = "    rhs";
					str = rightPadWhitespace(str, thirdColStart);
					write("\n" + str);
				}
				write(rName + rRHS);
				count++;
			}

		}
		write("\n");
	}

	private void writeBounds(int thirdColStart) {
		write("BOUNDS\n");
		Var[] vars = problem.getVars();
		if (vars != null) {
			for (int i = 0; i < vars.length; i++) {
				javax.constraints.impl.Var var = (javax.constraints.impl.Var) vars[i];
				if (var.isUsedInConstraints()) {
					//writeOneBounds(var.getName(), var.getMin(), var.getMax(), thirdColStart);	
					writeOneBounds(var.getId(), var.getMin(), var.getMax(), thirdColStart);	
//					String str = " UP bnd";
				}
			}
		}
		
		VarReal[] varReals = problem.getVarReals();
		if (varReals != null) {
			for (int i = 0; i < varReals.length; i++) {
				javax.constraints.impl.VarReal varReal = (javax.constraints.impl.VarReal) varReals[i];
				if (varReal.isUsedInConstraints()) {
					//writeOneBounds(varReal.getName(), varReal.getMin(), varReal.getMax(), thirdColStart);	
					writeOneBounds(varReal.getId(), varReal.getMin(), varReal.getMax(), thirdColStart);
//					String str = " UP bnd";
				}
			}
		}
	}

	private void writeOneBounds(String name, double min, double max,
			int thirdColStart) {
		String str = " UP bnd";
		str = rightPadWhitespace(str, thirdColStart);
		String vName = name;
		vName = rightPadWhitespace(vName, colLengths[0]);

		double d = max;
		String vUppBnd = "";

		if (Math.floor(d) == d) // if it's an integer
		{
			vUppBnd += ((int) d) + "   ";
		} else {
			vUppBnd += format(d) + "   ";
		}
		vUppBnd = leftPadWhitespace(vUppBnd, colLengths[1]);

		write(str + vName + vUppBnd + "\n");
		double lb = min;
		if (lb > 0) {
			String str2 = " LO bnd";
			str2 = rightPadWhitespace(str2, thirdColStart);

			String vLowBnd = "";

			if (Math.floor(lb) == lb) // if it's an integer
			{
				vLowBnd += ((int) lb) + "   ";
			} else {
				vLowBnd += format(lb) + "   ";
			}
			vLowBnd = leftPadWhitespace(vLowBnd, colLengths[1]);

			write(str2 + vName + vLowBnd + "\n");
		}
	}

	private String rightPadWhitespace(String str, int length) {
		while (str.length() < length) {
			str += " ";
		}
		return str;
	}

	private String leftPadWhitespace(String str, int length) {
		while (str.length() < length) {
			str = " " + str;
		}
		return str;
	}

	private String getRowType(String oper) {
		if (oper.equals("="))
			return "E";
		if (oper.equals("<="))
			return "L";
		if (oper.equals(">="))
			return "G";
		throw new RuntimeException("Illegal operator " + oper);
	}

	// ================================================
	// FORMATTING METHODS AND VARIABLES
	// ================================================
	// LP decimal format
	final static String LP_DECIMAL_FORMAT = "#0.000";
	static String REAL_FORMAT = "#,##0.00";
	static DecimalFormat DEFAULT_REAL_FORMAT = new DecimalFormat(
			LP_DECIMAL_FORMAT);

	/**
	 * Returns a formatted string representation of the passed double value,
	 * 
	 * @param d
	 *            the double to format
	 * @param fmt
	 *            the desired formatting
	 * @return a formatted string representation of the double.
	 */
	public static String format(double d, String fmt) {
		DecimalFormat df = new DecimalFormat(fmt);
		return df.format(d);
	}

	/**
	 * Returns a formatted string representation of the passed double value,
	 * 
	 * @param d
	 *            the double to format
	 * @return a formatted string representation of the double.
	 */
	public static String format(double d) {
		return DEFAULT_REAL_FORMAT.format(d);
		// return format(d, REAL_FORMAT);
	}

	/**
	 * Sets the output format for displaying decimal values.
	 * 
	 * @param fmt  the new format
	 */
	public static void setRealFormat(String fmt) {
		REAL_FORMAT = fmt;
	}

	/**
	 * Sets the output format for displaying decimal values.
	 * 
	 * @return the new format
	 */
	public static String getRealFormat() {
		return REAL_FORMAT;
	}
}
