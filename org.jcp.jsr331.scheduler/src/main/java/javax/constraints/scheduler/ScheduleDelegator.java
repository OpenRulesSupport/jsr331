package javax.constraints.scheduler;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Set;
import java.util.Vector;

import javax.constraints.ConstrainedVariable;
import javax.constraints.Constraint;
import javax.constraints.DomainType;
import javax.constraints.SearchStrategy;
import javax.constraints.Solution;
import javax.constraints.Solver;
import javax.constraints.Var;
import javax.constraints.VarBool;
import javax.constraints.VarMatrix;
import javax.constraints.VarReal;
import javax.constraints.VarSet;
import javax.constraints.VarString;


public abstract class ScheduleDelegator implements	Schedule {

		private final Schedule schedule;
		
		public ScheduleDelegator(Schedule schedule) {
			if (schedule == null)
				throw new RuntimeException("Invalid parameter for ScheduleDelegator");
			this.schedule = schedule;
		}

//		public void save(Solution solution) {
//			schedule.save(solution);
//		}

		public Activity add(Activity activity) {
			return schedule.add(activity);
		}

		public String getAPIVersion() {
			return schedule.getAPIVersion();
		}

		public int getDuration() {
			return schedule.getDuration();
		}

		public String getImplVersion() {
			return schedule.getImplVersion();
		}

		public Activity activity(String name) {
			return schedule.activity(name);
		}

		public Vector<Activity> getActivities() {
			return schedule.getActivities();
		}

		public String getName() {
			return schedule.getName();
		}

		public Activity getActivity(String name) {
			return schedule.getActivity(name);
		}

		public void setName(String name) {
			schedule.setName(name);
		}

		public Var add(Var var) {
			return schedule.add(var);
		}

		public int getEnd() {
			return schedule.getEnd();
		}

		public VarBool add(VarBool var) {
			return schedule.add(var);
		}

		public void setEnd(int end) {
			schedule.setEnd(end);
		}

		public Vector<Resource> getResources() {
			return schedule.getResources();
		}

		public Var add(String name, Var var) {
			return schedule.add(name, var);
		}

		public Resource getResource(String name) {
			return schedule.getResource(name);
		}

		public int getStart() {
			return schedule.getStart();
		}

		public VarReal add(VarReal var) {
			return schedule.add(var);
		}

		public void setStart(int start) {
			schedule.setStart(start);
		}

		public Activity activity(String name, int from, int to) {
			return schedule.activity(name, from, to);
		}

		public Activity activity(String name, int duration) {
			return schedule.activity(name, duration);
		}

		public Constraint post(String name, String symbolicExpression) {
			return schedule.post(name, symbolicExpression);
		}

		public Resource resource(String name, int capacityMax) {
			return schedule.resource(name, capacityMax);
		}

		public Resource resource(String name, int capacityMax, ResourceType type) {
			return schedule.resource(name, capacityMax, type);
		}

		public ResourceDisjunctive resourceDisjunctive(String name) {
			return schedule.resourceDisjunctive(name);
		}

		public SearchStrategy strategyScheduleActivities() {
			return schedule.strategyScheduleActivities();
		}

		public SearchStrategy strategyAssignResources() {
			return schedule.strategyAssignResources();
		}

		public Solution scheduleActivities() {
			return schedule.scheduleActivities();
		}

		public Solution scheduleActivitiesAndAssignResources() {
			return schedule.scheduleActivitiesAndAssignResources();
		}

		public void setDomainType(DomainType type) {
			schedule.setDomainType(type);
		}

		public Var createVariable(String name, int min, int max) {
			return schedule.createVariable(name, min, max);
		}

		public Solution assignResources() {
			return schedule.assignResources();
		}

		public void logActivities() {
			schedule.logActivities();
		}

		public void logResources() {
			schedule.logResources();
		}

		public Constraint post(Activity act1, String oper, Activity act2,
				int offset) {
			return schedule.post(act1, oper, act2, offset);
		}

		public Var variable(String name, int min, int max) {
			return schedule.variable(name, min, max);
		}

		public Var variable(String name, int min, int max, DomainType domainType) {
			return schedule.variable(name, min, max, domainType);
		}

		public Constraint post(Activity act1, String oper, Activity act2) {
			return schedule.post(act1, oper, act2);
		}

		public Var variable(int min, int max) {
			return schedule.variable(min, max);
		}

		public VarBool variableBool(String name) {
			return schedule.variableBool(name);
		}

		public VarBool variableBool() {
			return schedule.variableBool();
		}

		public void setRealPrecision(double value) {
			schedule.setRealPrecision(value);
		}

		public double getRealPrecision() {
			return schedule.getRealPrecision();
		}

		public VarReal variableReal(String name, double min, double max) {
			return schedule.variableReal(name, min, max);
		}

		public VarReal variableReal(String name) {
			return schedule.variableReal(name);
		}

		public VarSet variableSet(String name, int min, int max)
				throws Exception {
			return schedule.variableSet(name, min, max);
		}

		public VarSet variableSet(String name, int[] values) throws Exception {
			return schedule.variableSet(name, values);
		}

		public VarSet variableSet(String name, Set set) throws Exception {
			return schedule.variableSet(name, set);
		}

		public Var[] variableArray(String name, int min, int max, int size) {
			return schedule.variableArray(name, min, max, size);
		}

		public Var variable(String name, int[] domain) {
			return schedule.variable(name, domain);
		}

		public Var variable(int[] domain) {
			return schedule.variable(domain);
		}

		public Var[] getVars() {
			return schedule.getVars();
		}

		public VarBool[] getVarBools() {
			return schedule.getVarBools();
		}

		public VarReal[] getVarReals() {
			return schedule.getVarReals();
		}

		public VarReal[] getVarSets() {
			return schedule.getVarSets();
		}

		public Var getVar(String name) {
			return schedule.getVar(name);
		}

		public Constraint getConstraint(String name) {
			return schedule.getConstraint(name);
		}

		public Constraint getFalseConstraint() {
			return schedule.getFalseConstraint();
		}

		public Constraint getTrueConstraint() {
			return schedule.getTrueConstraint();
		}

		public VarReal getVarReal(String name) {
			return schedule.getVarReal(name);
		}

		public Var[] getVarArray(String name) {
			return schedule.getVarArray(name);
		}

		public Constraint add(Constraint constraint) {
			return schedule.add(constraint);
		}

		public Constraint[] getConstraints() {
			return schedule.getConstraints();
		}

		public Constraint postElement(int[] array, Var indexVar, String oper,
				int value) {
			return schedule.postElement(array, indexVar, oper, value);
		}

		public Constraint postElement(int[] array, Var indexVar, String oper,
				Var var) {
			return schedule.postElement(array, indexVar, oper, var);
		}

		public Constraint postElement(Var[] vars, Var indexVar, String oper,
				int value) {
			return schedule.postElement(vars, indexVar, oper, value);
		}

		public Constraint postElement(Var[] vars, Var indexVar, String oper,
				Var var) {
			return schedule.postElement(vars, indexVar, oper, var);
		}

		public Constraint postElement(Set[] arrayOfSets, Var indexVar,
				String oper, VarSet var) {
			return schedule.postElement(arrayOfSets, indexVar, oper, var);
		}

		public Constraint post(int[] array, Var[] vars, String oper, int value) {
			return schedule.post(array, vars, oper, value);
		}

		public Constraint post(int[] array, Var[] vars, String oper, Var var) {
			return schedule.post(array, vars, oper, var);
		}

		public Constraint post(Var[] vars, String oper, int value) {
			return schedule.post(vars, oper, value);
		}

		public Constraint post(Var[] vars, String oper, Var var) {
			return schedule.post(vars, oper, var);
		}

		public Constraint post(Var var1, Var var2, String oper, int value) {
			return schedule.post(var1, var2, oper, value);
		}

		public Constraint post(Var var1, Var var2, String oper, Var var) {
			return schedule.post(var1, var2, oper, var);
		}

		public Constraint post(Var var, String oper, int value) {
			return schedule.post(var, oper, value);
		}

		public Constraint post(Var var1, String oper, Var var2) {
			return schedule.post(var1, oper, var2);
		}

		public Constraint linear(Var var, String oper, int value) {
			return schedule.linear(var, oper, value);
		}

		public Constraint linear(Var var1, String oper, Var var2) {
			return schedule.linear(var1, oper, var2);
		}

		public Var element(int[] array, Var indexVar) {
			return schedule.element(array, indexVar);
		}

		public Var element(Var[] array, Var indexVar) {
			return schedule.element(array, indexVar);
		}

		public VarSet element(Set<Integer>[] sets, Var indexVar)
				throws Exception {
			return schedule.element(sets, indexVar);
		}

		public Var min(Var[] arrayOfVariables) {
			return schedule.min(arrayOfVariables);
		}

		public Var min(Var var1, Var var2) {
			return schedule.min(var1, var2);
		}

		public Var max(Var[] arrayOfVariables) {
			return schedule.max(arrayOfVariables);
		}

		public Var max(Var var1, Var var2) {
			return schedule.max(var1, var2);
		}

		public Var sum(Var[] vars) {
			return schedule.sum(vars);
		}

		public Var scalProd(int[] arrayOfValues, Var[] arrayOfVariables) {
			return schedule.scalProd(arrayOfValues, arrayOfVariables);
		}

		public void post(Constraint c) {
			schedule.post(c);
		}

		public Solver getSolver() {
			return schedule.getSolver();
		}

		public void setSolver(Solver solver) {
			schedule.setSolver(solver);
		}

		public void log(Var[] vars) {
			schedule.log(vars);
		}

		public void log(String text) {
			schedule.log(text);
		}

		public Constraint postAllDifferent(Var[] vars) {
			return schedule.postAllDifferent(vars);
		}

		public Constraint postAllDiff(Var[] vars) {
			return schedule.postAllDiff(vars);
		}

		public Constraint allDiff(Var[] vars) {
			return schedule.allDiff(vars);
		}

		public Constraint postAllDiff(ArrayList<Var> vars) {
			return schedule.postAllDiff(vars);
		}

		public Constraint postCardinality(Var[] vars, int cardValue,
				String oper, int value) {
			return schedule.postCardinality(vars, cardValue, oper, value);
		}

		public Constraint postCardinality(Var[] vars, int cardValue,
				String oper, Var var) {
			return schedule.postCardinality(vars, cardValue, oper, var);
		}

		public Constraint postCardinality(Var[] vars, Var cardVar, String oper,
				Var var) {
			return schedule.postCardinality(vars, cardVar, oper, var);
		}

		public Constraint postCardinality(Var[] vars, Var cardVar, String oper,
				int value) {
			return schedule.postCardinality(vars, cardVar, oper, value);
		}

		public Constraint postGlobalCardinality(Var[] vars, int[] values,
				Var[] cardinalityVars) {
			return schedule
					.postGlobalCardinality(vars, values, cardinalityVars);
		}

		public Constraint postGlobalCardinality(Var[] vars, int[] values,
				int[] cardMin, int[] cardMax) {
			return schedule.postGlobalCardinality(vars, values, cardMin,
					cardMax);
		}

		public Constraint postIfThen(Constraint constraint1,
				Constraint constraint2) {
			return schedule.postIfThen(constraint1, constraint2);
		}

		public Constraint postAnd(Constraint c1, Constraint c2) {
			return schedule.postAnd(c1, c2);
		}

		public Constraint postOr(Constraint c1, Constraint c2) {
			return schedule.postOr(c1, c2);
		}

		public Constraint postMax(Var[] vars, String oper, Var var) {
			return schedule.postMax(vars, oper, var);
		}

		public Constraint postMax(Var[] vars, String oper, int value) {
			return schedule.postMax(vars, oper, value);
		}

		public Constraint postMin(Var[] vars, String oper, Var var) {
			return schedule.postMin(vars, oper, var);
		}

		public Constraint postMin(Var[] vars, String oper, int value) {
			return schedule.postMin(vars, oper, value);
		}

		public void loadFromXML(InputStream in) throws Exception {
			schedule.loadFromXML(in);
		}

		public void storeToXML(OutputStream os, String comment)
				throws Exception {
			schedule.storeToXML(os, comment);
		}

		public VarMatrix variableMatrix(String name, int min, int max,
				int rows, int columns) {
			return schedule.variableMatrix(name, min, max, rows, columns);
		}

		public VarMatrix getVarMatrix(String name) {
			return schedule.getVarMatrix(name);
		}

		public void add(VarString var) {
			schedule.add(var);
		}

		public VarString[] getVarStrings() {
			return schedule.getVarStrings();
		}

		public VarString getVarString(String name) {
			return schedule.getVarString(name);
		}

		public VarString variableString(String name, String[] allStrings) {
			return schedule.variableString(name, allStrings);
		}

		public Constraint post(VarString var, String oper, String value) {
			return schedule.post(var, oper, value);
		}

		public Constraint linear(VarString var, String oper, String value) {
			return schedule.linear(var, oper, value);
		}

		public Constraint post(VarString var1, String oper, VarString var2) {
			return schedule.post(var1, oper, var2);
		}

//		public ArrayList<Var> variableList() {
//			return schedule.variableList();
//		}

		public Constraint post(int[] array, ArrayList<Var> vars, String oper, int value) {
			return schedule.post(array, vars, oper, value);
		}

		public Constraint post(ArrayList<Var> vars, String oper, int value) {
			return schedule.post(vars, oper, value);
		}

		public Constraint post(ArrayList<Var> vars, String oper, Var var) {
			return schedule.post(vars, oper, var);
		}

		public Var element(ArrayList<Var> list, Var indexVar) {
			return schedule.element(list, indexVar);
		}

		public Var min(ArrayList<Var> listOfVariables) {
			return schedule.min(listOfVariables);
		}

		public Var max(ArrayList<Var> listOfVariables) {
			return schedule.max(listOfVariables);
		}

		public Var sum(ArrayList<Var> vars) {
			return schedule.sum(vars);
		}

		public Var scalProd(int[] arrayOfValues, ArrayList<Var> listOfVariables) {
			return schedule.scalProd(arrayOfValues, listOfVariables);
		}

		public void log(ArrayList<Var> vars) {
			schedule.log(vars);
		}

		public Constraint postAllDifferent(ArrayList<Var> vars) {
			return schedule.postAllDifferent(vars);
		}

		public Constraint allDiff(ArrayList<Var> vars) {
			return schedule.allDiff(vars);
		}

//		public Constraint postCardinality(ArrayList<Var> vars, int cardValue,
//				String oper, int value) {
//			return schedule.postCardinality(vars, cardValue, oper, value);
//		}

		public Constraint postCardinality(ArrayList<Var> vars, int cardValue,
				String oper, Var var) {
			return schedule.postCardinality(vars, cardValue, oper, var);
		}

		public Constraint postCardinality(ArrayList<Var> vars, Var cardVar,
				String oper, Var var) {
			return schedule.postCardinality(vars, cardVar, oper, var);
		}

		public Constraint postCardinality(ArrayList<Var> vars, Var cardVar,
				String oper, int value) {
			return schedule.postCardinality(vars, cardVar, oper, value);
		}

		public Constraint postGlobalCardinality(ArrayList<Var> vars, int[] values,
				Var[] cardinalityVars) {
			return schedule
					.postGlobalCardinality(vars, values, cardinalityVars);
		}

		public Constraint postGlobalCardinality(ArrayList<Var> vars, int[] values,
				int[] cardMin, int[] cardMax) {
			return schedule.postGlobalCardinality(vars, values, cardMin,
					cardMax);
		}

		public VarReal createVariableReal(String name, double min, double max) {
			return schedule.createVariableReal(name, min, max);
		}

		public Constraint post(VarReal var, String oper, int value) {
			return schedule.post(var, oper, value);
		}

		public Constraint post(VarReal var, String oper, double value) {
			return schedule.post(var, oper, value);
		}

		public Constraint post(VarReal var1, String oper, VarReal var2) {
			return schedule.post(var1, oper, var2);
		}

		public Constraint post(VarReal[] vars, String oper, double value) {
			return schedule.post(vars, oper, value);
		}

		public Constraint post(VarReal[] vars, String oper, VarReal var) {
			return schedule.post(vars, oper, var);
		}

		public Constraint post(double[] array, VarReal[] vars, String oper,
				VarReal var) {
			return schedule.post(array, vars, oper, var);
		}

		public Constraint post(double[] array, VarReal[] vars, String oper,
				double value) {
			return schedule.post(array, vars, oper, value);
		}

		public Constraint post(double[] array, ConstrainedVariable[] vars,
				String oper, ConstrainedVariable var) {
			return schedule.post(array, vars, oper, var);
		}

		public Constraint post(double[] array, ConstrainedVariable[] vars,
				String oper, double value) {
			return schedule.post(array, vars, oper, value);
		}

		public Constraint post(VarReal var1, String oper, Var var2) {
			return schedule.post(var1, oper, var2);
		}

		public Constraint post(Var var1, String oper, VarReal var2) {
			return schedule.post(var1, oper, var2);
		}

		public Var sum(Var var1, Var var2) {
			return schedule.sum(var1, var2);
		}

		public Var sum(Var var1, Var var2, Var var3) {
			return schedule.sum(var1, var2, var3);
		}

		public VarReal sum(VarReal[] vars) {
			return schedule.sum(vars);
		}

		public VarReal sum(VarReal var1, VarReal var2) {
			return schedule.sum(var1, var2);
		}

		public VarReal sum(VarReal var1, VarReal var2, VarReal var3) {
			return schedule.sum(var1, var2, var3);
		}

		public VarReal scalProd(double[] arrayOfValues,
				VarReal[] arrayOfVariables) {
			return schedule.scalProd(arrayOfValues, arrayOfVariables);
		}

		public VarReal scalProd(double[] arrayOfValues,
				ConstrainedVariable[] arrayOfVariables) {
			return schedule.scalProd(arrayOfValues, arrayOfVariables);
		}

		public void log(VarReal[] vars) {
			schedule.log(vars);
		}

		public Constraint postCardinality(ArrayList<Var> vars, int cardValue,
				String oper, int value) {
			return schedule.postCardinality(vars, cardValue, oper, value);
		}

		public Constraint postIfThen(VarBool var1, VarBool var2) {
			return schedule.postIfThen(var1, var2);
		}

		public Var getTotalConstraintViolation() {
			return schedule.getTotalConstraintViolation();
		}

		public boolean areThereProbabilityConstraints() {
			return schedule.areThereProbabilityConstraints();
		}

		public void log(Solution solution) {
			schedule.log(solution);
		}

		public Var[] getConstraintCapacites(Resource resource) {
			return schedule.getConstraintCapacites(resource);
		}

		public Vector<Var> getScheduleVars() {
			return schedule.getScheduleVars();
		}

		public SearchStrategy strategyScheduleVars() {
			return schedule.strategyScheduleVars();
		}

		public void save(Solution solution) {
			schedule.save(solution);
		}

		public Constraint postAllDiff(VarString[] varStrings) {
			return schedule.postAllDiff(varStrings);
		}

		public Constraint linear(VarString var1, String oper, VarString var2) {
			return schedule.linear(var1, oper, var2);
		}

		public Var sum(String name, Var[] vars) {
			return schedule.sum(name, vars);
		}

		public Var scalProd(String name, int[] arrayOfValues,
				Var[] arrayOfVariables) {
			return schedule.scalProd(name, arrayOfValues, arrayOfVariables);
		}

		public Constraint postScalProd(Var var, String oper,
				int[] arrayOfValues, Var[] arrayOfVariables) {
			return schedule.postScalProd(var, oper, arrayOfValues,
					arrayOfVariables);
		}

		public void post(Activity act1, String oper, int time) {
			schedule.post(act1, oper, time);
		}

		public void postAllDiff(Activity[] activities) {
			schedule.postAllDiff(activities);
		}

		public SearchStrategy strategyAssignResources(int time) {
			return schedule.strategyAssignResources(time);
		}

		public boolean areAllActivitiesBound() {
			return schedule.areAllActivitiesBound();
		}

		public Constraint isOneOfConstraint(int[] array, Var var) {
			return schedule.isOneOfConstraint(array, var);
		}

		public Constraint isOneOfConstraint(String[] array, VarString var) {
			return schedule.isOneOfConstraint(array, var);
		}

		public Constraint isNotOneOfConstraint(int[] array, Var var) {
			return schedule.isNotOneOfConstraint(array, var);
		}

		public Constraint isNotOneOfConstraint(String[] array, VarString var) {
			return schedule.isNotOneOfConstraint(array, var);
		}



}
