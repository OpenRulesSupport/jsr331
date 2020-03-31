package javax.constraints;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Set;

public abstract class ProblemDelegator implements Problem {
	
	private final Problem problem; 
	
	public ProblemDelegator(Problem problem) {
		if (problem == null)
			throw new RuntimeException("Invalid parameter for ProblemDelegator");
		this.problem = problem;
	}

	public String getAPIVersion() {
		return problem.getAPIVersion();
	}

	public String getImplVersion() {
		return problem.getImplVersion();
	}

	public String getName() {
		return problem.getName();
	}

	public void setName(String name) {
		problem.setName(name);
	}

	public Var add(Var var) {
		return problem.add(var);
	}

	public Constraint isOneOfConstraint(int[] array, Var var) {
		return problem.isOneOfConstraint(array, var);
	}

	public Constraint isOneOfConstraint(String[] array, VarString var) {
		return problem.isOneOfConstraint(array, var);
	}

	public Constraint isNotOneOfConstraint(int[] array, Var var) {
		return problem.isNotOneOfConstraint(array, var);
	}

	public Constraint isNotOneOfConstraint(String[] array, VarString var) {
		return problem.isNotOneOfConstraint(array, var);
	}

	public VarBool add(VarBool var) {
		return problem.add(var);
	}

	public Var add(String name, Var var) {
		return problem.add(name, var);
	}

	public VarReal add(VarReal var) {
		return problem.add(var);
	}

	public Constraint post(String name, String symbolicExpression) {
		return problem.post(name, symbolicExpression);
	}

	public void setDomainType(DomainType type) {
		problem.setDomainType(type);
	}

	public Var variable(String name, int min, int max) {
		return problem.variable(name, min, max);
	}
	
	public Var createVariable(String name, int min, int max) {
		return problem.createVariable(name, min, max);
	}

	public Var variable(String name, int min, int max, DomainType domainType) {
		return problem.variable(name, min, max, domainType);
	}

	public Var variable(int min, int max) {
		return problem.variable(min, max);
	}

	public VarBool variableBool(String name) {
		return problem.variableBool(name);
	}

	public VarBool variableBool() {
		return problem.variableBool();
	}

	public void setRealPrecision(double value) {
		problem.setRealPrecision(value);
	}

	public double getRealPrecision() {
		return problem.getRealPrecision();
	}

	public VarReal variableReal(String name, double min, double max) {
		return problem.variableReal(name, min, max);
	}

	public VarReal variableReal(String name) {
		return problem.variableReal(name);
	}

	public VarSet variableSet(String name, int min, int max) throws Exception {
		return problem.variableSet(name, min, max);
	}

	public VarSet variableSet(String name, int[] values) throws Exception {
		return problem.variableSet(name, values);
	}

	public VarSet variableSet(String name, Set set) throws Exception {
		return problem.variableSet(name, set);
	}

	public Var[] variableArray(String name, int min, int max, int size) {
		return problem.variableArray(name, min, max, size);
	}

	public Var variable(String name, int[] domain) {
		return problem.variable(name, domain);
	}

	public Var variable(int[] domain) {
		return problem.variable(domain);
	}

	public Var[] getVars() {
		return problem.getVars();
	}

	public VarBool[] getVarBools() {
		return problem.getVarBools();
	}

	public VarReal[] getVarReals() {
		return problem.getVarReals();
	}

	public VarReal[] getVarSets() {
		return problem.getVarSets();
	}

	public Var getVar(String name) {
		return problem.getVar(name);
	}

	public Constraint getConstraint(String name) {
		return problem.getConstraint(name);
	}

	public Constraint getFalseConstraint() {
		return problem.getFalseConstraint();
	}

	public Constraint getTrueConstraint() {
		return problem.getTrueConstraint();
	}

	public VarReal getVarReal(String name) {
		return problem.getVarReal(name);
	}

	public Var[] getVarArray(String name) {
		return problem.getVarArray(name);
	}

	public Constraint add(Constraint constraint) {
		return problem.add(constraint);
	}

	public Constraint[] getConstraints() {
		return problem.getConstraints();
	}

	public Constraint postElement(int[] array, Var indexVar, String oper,
			int value) {
		return problem.postElement(array, indexVar, oper, value);
	}

	public Constraint postElement(int[] array, Var indexVar, String oper,
			Var var) {
		return problem.postElement(array, indexVar, oper, var);
	}

	public Constraint postElement(Var[] vars, Var indexVar, String oper,
			int value) {
		return problem.postElement(vars, indexVar, oper, value);
	}

	public Constraint postElement(Var[] vars, Var indexVar, String oper, Var var) {
		return problem.postElement(vars, indexVar, oper, var);
	}

	public Constraint postElement(Set[] arrayOfSets, Var indexVar, String oper,
			VarSet var) {
		return problem.postElement(arrayOfSets, indexVar, oper, var);
	}

	@Deprecated
	public Constraint post(int[] array, Var[] vars, String oper, int value) {
		return problem.post(array, vars, oper, value);
	}

	public Constraint post(int[] array, ArrayList<Var> vars, String oper,
			int value) {
		return problem.post(array, vars, oper, value);
	}

	public Constraint postCardinality(ArrayList<Var> vars, int cardValue,
			String oper, int value) {
		return problem.postCardinality(vars, cardValue, oper, value);
	}

	@Deprecated
	public Constraint post(int[] array, Var[] vars, String oper, Var var) {
		return problem.post(array, vars, oper, var);
	}

	public Constraint post(Var[] vars, String oper, int value) {
		return problem.post(vars, oper, value);
	}

	public Constraint post(Var[] vars, String oper, Var var) {
		return problem.post(vars, oper, var);
	}

	public Constraint post(Var var1, Var var2, String oper, int value) {
		return problem.post(var1, var2, oper, value);
	}

	public Constraint post(Var var1, Var var2, String oper, Var var) {
		return problem.post(var1, var2, oper, var);
	}

	public Constraint post(Var var, String oper, int value) {
		return problem.post(var, oper, value);
	}

	public Constraint post(Var var1, String oper, Var var2) {
		return problem.post(var1, oper, var2);
	}

	public Constraint linear(Var var, String oper, int value) {
		return problem.linear(var, oper, value);
	}

	public Constraint linear(Var var1, String oper, Var var2) {
		return problem.linear(var1, oper, var2);
	}

	public Var element(int[] array, Var indexVar) {
		return problem.element(array, indexVar);
	}

	public Var element(Var[] array, Var indexVar) {
		return problem.element(array, indexVar);
	}

	public Var min(Var[] arrayOfVariables) {
		return problem.min(arrayOfVariables);
	}

	public Var min(Var var1, Var var2) {
		return problem.min(var1, var2);
	}

	public Var max(Var[] arrayOfVariables) {
		return problem.max(arrayOfVariables);
	}

	public Var max(Var var1, Var var2) {
		return problem.max(var1, var2);
	}

	public Var sum(Var[] vars) {
		return problem.sum(vars);
	}

	public Var scalProd(int[] arrayOfValues, Var[] arrayOfVariables) {
		return problem.scalProd(arrayOfValues, arrayOfVariables);
	}

	public void post(Constraint c) {
		problem.post(c);
	}

	public Solver getSolver() {
		return problem.getSolver();
	}

	public void setSolver(Solver solver) {
		problem.setSolver(solver);
	}

	public void log(Var[] vars) {
		problem.log(vars);
	}

	public void log(String text) {
		problem.log(text);
	}

	public Constraint postAllDifferent(Var[] vars) {
		return problem.postAllDifferent(vars);
	}

//	public Constraint postAllDifferent(List vars) {
//		return problem.postAllDifferent(vars);
//	}

	public Constraint postAllDiff(Var[] vars) {
		return problem.postAllDiff(vars);
	}

	public Constraint allDiff(Var[] vars) {
		return problem.allDiff(vars);
	}

	public Constraint postCardinality(Var[] vars, int cardValue, String oper,
			int value) {
		return problem.postCardinality(vars, cardValue, oper, value);
	}

	public Constraint postCardinality(Var[] vars, int cardValue, String oper,
			Var var) {
		return problem.postCardinality(vars, cardValue, oper, var);
	}

	public Constraint postCardinality(Var[] vars, Var cardVar, String oper,
			Var var) {
		return problem.postCardinality(vars, cardVar, oper, var);
	}

	public Constraint postCardinality(Var[] vars, Var cardVar, String oper,
			int value) {
		return problem.postCardinality(vars, cardVar, oper, value);
	}

	public Constraint postGlobalCardinality(Var[] vars, int[] values,
			Var[] cardinalityVars) {
		return problem.postGlobalCardinality(vars, values, cardinalityVars);
	}

	public Constraint postGlobalCardinality(Var[] vars, int[] values,
			int[] cardMin, int[] cardMax) {
		return problem.postGlobalCardinality(vars, values, cardMin, cardMax);
	}

	public Constraint postIfThen(Constraint constraint1, Constraint constraint2) {
		return problem.postIfThen(constraint1, constraint2);
	}

	public Constraint postAnd(Constraint c1, Constraint c2) {
		return problem.postAnd(c1, c2);
	}

	public Constraint postOr(Constraint c1, Constraint c2) {
		return problem.postOr(c1, c2);
	}

	public Constraint postMax(Var[] vars, String oper, Var var) {
		return problem.postMax(vars, oper, var);
	}

	public Constraint postMax(Var[] vars, String oper, int value) {
		return problem.postMax(vars, oper, value);
	}

	public Constraint postMin(Var[] vars, String oper, Var var) {
		return problem.postMin(vars, oper, var);
	}

	public Constraint postMin(Var[] vars, String oper, int value) {
		return problem.postMin(vars, oper, value);
	}

	public void loadFromXML(InputStream in) throws Exception {
		problem.loadFromXML(in);
	}

	public void storeToXML(OutputStream os, String comment) throws Exception {
		problem.storeToXML(os, comment);
	}

	public VarMatrix variableMatrix(String name, int min, int max, int rows,
			int columns) {
		return problem.variableMatrix(name, min, max, rows, columns);
	}

	public VarMatrix getVarMatrix(String name) {
		return problem.getVarMatrix(name);
	}

	public void add(VarString var) {
		problem.add(var);
	}

	public VarString[] getVarStrings() {
		return problem.getVarStrings();
	}

	public VarString getVarString(String name) {
		return problem.getVarString(name);
	}

	public VarString variableString(String name, String[] allStrings) {
		return problem.variableString(name, allStrings);
	}

	public Constraint post(VarString var, String oper, String value) {
		return problem.post(var, oper, value);
	}

	public Constraint linear(VarString var, String oper, String value) {
		return problem.linear(var, oper, value);
	}

	public Constraint post(VarString var1, String oper, VarString var2) {
		return problem.post(var1, oper, var2);
	}

	public Constraint post(ArrayList<Var> vars, String oper, int value) {
		return problem.post(vars, oper, value);
	}

	public Constraint post(ArrayList<Var> vars, String oper, Var var) {
		return problem.post(vars, oper, var);
	}

	public Var sum(ArrayList<Var> vars) {
		return problem.sum(vars);
	}

	public Constraint allDiff(ArrayList<Var> vars) {
		return problem.allDiff(vars);
	}

	public Var element(ArrayList<Var> list, Var indexVar) {
		return problem.element(list, indexVar);
	}

//	public ArrayList<Var> variableList() {
//		return problem.variableList();
//	}

//	public Constraint post(int[] array, ArrayList<Var> vars, String oper, int value) {
//		return problem.post(array, vars, oper, value);
//	}

	public Var max(ArrayList<Var> listOfVariables) {
		return problem.max(listOfVariables);
	}

	public Var scalProd(int[] arrayOfValues, ArrayList<Var> listOfVariables) {
		return problem.scalProd(arrayOfValues, listOfVariables);
	}

	public void log(ArrayList<Var> vars) {
		problem.log(vars);
	}

	public Constraint postAllDifferent(ArrayList<Var> vars) {
		return problem.postAllDifferent(vars);
	}

//	public Constraint postCardinality(ArrayList<Var> vars, int cardValue, String oper,
//			int value) {
//		return problem.postCardinality(vars, cardValue, oper, value);
//	}

	public Constraint postCardinality(ArrayList<Var> vars, int cardValue, String oper,
			Var var) {
		return problem.postCardinality(vars, cardValue, oper, var);
	}

	public Constraint postCardinality(ArrayList<Var> vars, Var cardVar, String oper,
			Var var) {
		return problem.postCardinality(vars, cardVar, oper, var);
	}

	public Constraint postCardinality(ArrayList<Var> vars, Var cardVar, String oper,
			int value) {
		return problem.postCardinality(vars, cardVar, oper, value);
	}

	public Constraint postGlobalCardinality(ArrayList<Var> vars, int[] values,
			Var[] cardinalityVars) {
		return problem.postGlobalCardinality(vars, values, cardinalityVars);
	}

	public Constraint postGlobalCardinality(ArrayList<Var> vars, int[] values,
			int[] cardMin, int[] cardMax) {
		return problem.postGlobalCardinality(vars, values, cardMin, cardMax);
	}

	public VarSet element(Set<Integer>[] sets, Var indexVar) throws Exception {
		return problem.element(sets, indexVar);
	}

	public Var min(ArrayList<Var> listOfVariables) {
		return problem.min(listOfVariables);
	}

	public Constraint postAllDiff(ArrayList<Var> vars) {
		return problem.postAllDiff(vars);
	}

	public Constraint post(VarReal var, String oper, int value) {
		return problem.post(var, oper, value);
	}

	public Constraint post(VarReal var1, String oper, VarReal var2) {
		return problem.post(var1, oper, var2);
	}

	public Constraint post(VarReal var1, String oper, Var var2) {
		return problem.post(var1, oper, var2);
	}

	public Constraint post(Var var1, String oper, VarReal var2) {
		return problem.post(var1, oper, var2);
	}

	public Constraint post(double[] coefficients, Var[] vars, String oper,
			double value) {
		return problem.post(coefficients, vars, oper, value);
	}

	public Constraint post(double[] array, VarReal[] vars, String oper,
			VarReal var) {
		return problem.post(array, vars, oper, var);
	}

	public Constraint post(double[] array, VarReal[] vars, String oper,
			double value) {
		return problem.post(array, vars, oper, value);
	}

	public void log(VarReal[] vars) {
		problem.log(vars);
	}

	public Var sum(Var var1, Var var2) {
		return problem.sum(var1, var2);
	}

	public Var sum(Var var1, Var var2, Var var3) {
		return problem.sum(var1, var2, var3);
	}

	public VarReal createVariableReal(String name, double min, double max) {
		return problem.createVariableReal(name, min, max);
	}

	public VarReal scalProd(double[] arrayOfValues, VarReal[] arrayOfVariables) {
		return problem.scalProd(arrayOfValues, arrayOfVariables);
	}

	public Constraint post(VarReal var, String oper, double value) {
		return problem.post(var, oper, value);
	}

	public Constraint post(VarReal[] vars, String oper, double value) {
		return problem.post(vars, oper, value);
	}

	public Constraint post(VarReal[] vars, String oper, VarReal var) {
		return problem.post(vars, oper, var);
	}

	public VarReal sum(VarReal[] vars) {
		return problem.sum(vars);
	}

	public VarReal sum(VarReal var1, VarReal var2) {
		return problem.sum(var1, var2);
	}

	public VarReal sum(VarReal var1, VarReal var2, VarReal var3) {
		return problem.sum(var1, var2, var3);
	}

	public Constraint post(double[] array, ConstrainedVariable[] vars,
			String oper, ConstrainedVariable var) {
		return problem.post(array, vars, oper, var);
	}

	public Constraint post(double[] array, ConstrainedVariable[] vars,
			String oper, double value) {
		return problem.post(array, vars, oper, value);
	}

	public VarReal scalProd(double[] arrayOfValues,
			ConstrainedVariable[] arrayOfVariables) {
		return problem.scalProd(arrayOfValues, arrayOfVariables);
	}

	public Constraint postIfThen(VarBool var1, VarBool var2) {
		return problem.postIfThen(var1, var2);
	}

	public Var getTotalConstraintViolation() {
		return problem.getTotalConstraintViolation();
	}

	public boolean areThereProbabilityConstraints() {
		return problem.areThereProbabilityConstraints();
	}

	public Constraint postAllDiff(VarString[] varStrings) {
		return problem.postAllDiff(varStrings);
	}

	public Constraint linear(VarString var1, String oper, VarString var2) {
		return problem.linear(var1, oper, var2);
	}

	public Constraint postScalProd(Var var, String oper, int[] arrayOfValues,
			Var[] arrayOfVariables) {
		return problem.postScalProd(var, oper, arrayOfValues, arrayOfVariables);
	}

	public Var sum(String name, Var[] vars) {
		return problem.sum(name, vars);
	}

	public Var scalProd(String name, int[] arrayOfValues, Var[] arrayOfVariables) {
		return problem.scalProd(name, arrayOfValues, arrayOfVariables);
	}	

	
}
