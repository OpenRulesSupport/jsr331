package com.openrules.csp;

import java.util.ArrayList;
import java.util.HashMap;

import javax.constraints.Objective;
import javax.constraints.Problem;
import javax.constraints.ProblemFactory;
import javax.constraints.Solution;
import javax.constraints.Solver;
import javax.constraints.Var;
import javax.constraints.VarMatrix;
import javax.constraints.impl.AbstractProblem;

import com.openrules.ruleengine.Decision;

public class CSP {
	
	Decision decision;
	Problem csp;
	HashMap<String,ArrayList<Var>> mapOfVarLists;
	Solution solution;
	
	public CSP(Decision decision) {
		this.decision = decision;
		csp = ProblemFactory.newProblem("CSP");
		mapOfVarLists = new HashMap<String,ArrayList<Var>>();
	}
	
	public Decision getDecision() {
		return decision;
	}
	public void setDecision(Decision decision) {
		this.decision = decision;
	}

	public Problem getCsp() {
		return csp;
	}
	public void setCsp(Problem csp) {
		this.csp = csp;
	}
	public Var getVar(String name) {
		return csp.getVar(name);
	}
	
	public void post(String varName, String oper, int value) {
		Var var = csp.getVar(varName);
		csp.post(var,oper,value);
	}
	
	public void post(String varName, String oper, String valueName) {
//		String name = decision.getString(varName);
//		if (name == null)
//			name = varName;
		Var var = csp.getVar(varName);
		int value;
		try {
			value = Integer.parseInt(valueName);
		} catch (Exception e) {
			value = decision.getInt(valueName);
		}
		csp.post(var,oper,value);
	}

	public final boolean compare(String variableName, String oper, String compareValue) {
		com.openrules.types.Oper op = new com.openrules.types.Oper(oper);
		return decision.compare(variableName, op, compareValue);
	}
	
	public Var addVariable(String name,int min, int max) {
		return csp.variable(name,min,max);
	}
	
	public Var addExpression(String resultName, String varName, String oper, int value) {
		Var var = getVar(varName);
		if (var == null)
			throw new RuntimeException("Cannot find var '" + varName + "'");
		Var result = null;
		if ("+".equals(oper)) 
			result = var.plus(value);
		else
		if ("-".equals(oper)) 
			result = var.minus(value);
		else
		if ("*".equals(oper)) 
			result = var.multiply(value);
		else
		if ("/".equals(oper)) 
			result = var.divide(value);
		if (result == null)
			throw new RuntimeException("Invalid expression operator '" + oper + "'");
		csp.add(resultName,result);
		return result;
	}
	
	public void addVarToList(String listName, String varName) {
		Var var = csp.getVar(varName);
		if (var == null)
			throw new RuntimeException("Cannot find Var " + varName);
		addVarToList(listName, var);
	}
	
	public void addVarToList(String listName, Var var) {
		ArrayList<Var> list = mapOfVarLists.get(listName);
		if (list == null) {
			list = new ArrayList<Var>();
			mapOfVarLists.put(listName, list);
		}
		list.add(var);
	}
	
	public ArrayList<Var> getVarList(String listName) {
		ArrayList<Var> list = mapOfVarLists.get(listName);
		if (list == null) {
			throw new RuntimeException("Cannot find VarList " + listName);
		}
		return list;
	}
	
	public Var[] addVarArray(String name, String elements) {
		String[] varNames = elements.split(",");
		//String[] varNames = StringTool.tokenize(elements,",");
		for (int i = 0; i < varNames.length; i++) {
		varNames[i] = varNames[i].trim();
		}
		AbstractProblem acsp = (AbstractProblem)csp;
		return acsp.addVarArray(name,varNames);
	}
	
	public Var[] createVarArray(String arrayName, String varNames, int min, int max) {
		String[] names;
		try {
			names = decision.getStringArray(varNames);
		} catch (Exception e) {
			names = varNames.split(",");
		}
		
		for (int i = 0; i < names.length; i++) {
			csp.variable(names[i], min, max);
		}
		AbstractProblem acsp = (AbstractProblem)csp;
		return acsp.addVarArray(arrayName,names);
	}

	public Var[] getVarArray(String variables) {
		Var[] arrayOfVariables = null;
		arrayOfVariables = csp.getVarArray(variables);
		if (arrayOfVariables == null) {
			// try a list
			String[] strings = variables.split(",");
			arrayOfVariables = new Var[strings.length];
			for (int i = 0; i < strings.length; i++) {
				String varName = strings[i].trim();
				Var var = csp.getVar(varName);
				if (var == null) {
					csp.log("Cannot find Var " + varName);
					return null;
				}
				arrayOfVariables[i] = var;
			}
		}
		return arrayOfVariables;		
	}
	
	public Var addSum(String name, String variables) {
		ArrayList<Var> list = getVarList(variables);
		if (list != null) {
			Var sum = csp.sum(list);
			csp.add(name,sum);
			return sum;
		}
		Var[] array = getVarArray(variables);
		if (array != null) {
			Var sum = csp.sum(name,array);
			csp.add(name,sum);
			return sum;
		}
		throw new RuntimeException("Cannot find array or list " + variables);
	}
	
	public Var addScalProd(String name, String coefficients, String variables) {
		int[] arrayOfValues = decision.getGlossary().getArrayHandler().getIntArray(coefficients);
		ArrayList<Var> list = getVarList(variables);
		if (list != null) {
			Var scalProd = csp.scalProd(arrayOfValues,list);
			csp.add(name,scalProd);
			return scalProd;
		}
		Var[] array = getVarArray(variables);
		if (array != null) {
			Var scalProd =  csp.scalProd(name, arrayOfValues, array);
			csp.add(name,scalProd);
			return scalProd;
		}
		throw new RuntimeException("Cannot find array or list " + variables);
	}
	
	public Var[] getVars() {
		return csp.getVars();
	}
	
	public Solution optimize(String minimizeMaximize, String objectiveName) {
		Objective type = Objective.MINIMIZE;
		if (minimizeMaximize.equals("Maximize"))
			type = Objective.MAXIMIZE;
		Solver solver = csp.getSolver(); 
		Var objective = csp.getVar(objectiveName);
		if (objective == null)
			throw new RuntimeException("Unknown objective: "+objectiveName );
		solver.traceSolutions(true);
		solution = solver.findOptimalSolution(type, objective); 
		if (solution != null) {
			solution.log();
			int objectiveValue = solution.getValue(objectiveName);
			csp.log("Found Optimal Solution: " + objectiveValue);
			decision.setInt(objectiveName, objectiveValue);
			//solution.save();
			Var[] vars = csp.getVars();
			for (int i = 0; i < vars.length; i++) {
				String varName = vars[i].getName();
				if (decision.isDecisionVariable(varName)) {
					int value = solution.getValue(varName);
					decision.setInt(varName,value);
				}
			}
		}
		return solution;
	}
	
	public Solution getSolution() {
		return solution;
	}


	public VarMatrix createVarMatrix(String matrixName, String rows, String columns, int min, int max) {
		String[] rowNames;
		try {
			rowNames = decision.getStringArray(rows);
		} catch (Exception e) {
			rowNames = rows.split(",");
		}
		
		String[] columnNames;
		try {
			columnNames = decision.getStringArray(columns);
		} catch (Exception e) {
			columnNames = columns.split(",");
		}
		
		VarMatrix matrix = csp.variableMatrix(matrixName, min, max, rowNames.length, columnNames.length);
		return matrix;
	}
	
	public VarMatrix getMatrix(String matrixName) {
		return csp.getVarMatrix(matrixName);
	}

}
