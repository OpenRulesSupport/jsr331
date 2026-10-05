package javax.constraints.linear.impl;

import java.util.HashMap;

import javax.constraints.ConstrainedVariable;
import javax.constraints.Constraint;
import javax.constraints.Objective;
import javax.constraints.Problem;
import javax.constraints.Solution;
import javax.constraints.Var;
import javax.constraints.VarReal;

import com.javasolver.SCIP;
import com.javasolver.SCIPExpression;
import com.javasolver.SCIPVariable;
import com.javasolver.SCIP.Status;

public class LinearSolver extends javax.constraints.linear.LinearSolver {

    static public final String JSR331_LINEAR_SOLVER_VERSION = "SCIP-JAVA v.1.0.0 using SCIP Optimization Suite 10.0";

    Problem problem;
    com.javasolver.SCIP model;
    int numberOfRoundings;

    public LinearSolver() {
        numberOfRoundings = 0;
    }

    public void init() {
        problem = getProblem();

        model = new SCIP(); // .verbose(1);

        // Add SCIPVariables (int + real)
        Var[] vars = problem.getVars();
        int intSize = 0;
        if (vars != null)
            intSize = vars.length;
        VarReal[] varReals = problem.getVarReals();
        int realSize = 0;
        if (varReals != null)
            realSize = varReals.length;
        SCIPVariable[] scipVars = new SCIPVariable[intSize + realSize];
        int n = 0;
        if (intSize > 0) {
            for (Var var : vars) {
            	SCIPVariable scipVar = model.addVariable().name(var.getName()).integer().bounds(var.getMin(),var.getMax()); 
                var.setObject(scipVar);
                scipVars[n++] = scipVar;
            }
        }
        if (realSize > 0) {
            for (VarReal var : varReals) {
                SCIPVariable scipVar = model.addVariable().lb(var.getMin()).ub(var.getMax()).name(var.getName());
                var.setObject(scipVar);
                scipVars[n++] = scipVar;
            }
        }

        // Add constraints
        double precision = 1e-7;
//      if (isIntegerVariablesOnly())
//          precision = 1;
        Constraint[] constraints = problem.getConstraints();
        for (Constraint constraint : constraints) {
            javax.constraints.impl.Constraint c = (javax.constraints.impl.Constraint) constraint;
            double[] constraintCoefficients = c.getCoefficients();
            ConstrainedVariable[] constraintVars = c.getVars();
            String oper = c.getOper();
            double rhs = c.getValue();
//            log("Constraint: ");
//            for (ConstrainedVariable v : constraintVars)
//                log(" " + v);
//            for (double d : constraintCoefficients)
//                log("  " + d);
//            log(oper + " " + rhs);
            SCIPExpression expression = model.createExpression();
            for (int i = 0; i < constraintVars.length; i++) {
                ConstrainedVariable var = constraintVars[i];
                SCIPVariable scipVar = (SCIPVariable) var.getObject();
                if (scipVar == null) {
                    throw new RuntimeException(
                            "The variable " + var.getName() + " does not have an associated SCIP variable");
                }
                Double coef = constraintCoefficients[i];
                expression.add(coef,scipVar);
            }
            
            if ("=".equals(oper)) {
            	expression.eq(rhs);
            }
            else if (">=".equals(oper)) {
            	expression.geq(rhs);
            }
            else if (">".equals(oper)) {
                rhs += precision;
                expression.geq(rhs);
            } else if ("<".equals(oper)) {
                rhs -= precision;
                expression.leq(rhs);
            } else if ("<=".equals(oper)) {
            	expression.leq(rhs);
            }
//            else if ("!=".equals(oper)) {
//                // not implemented
//            }
            else {
                throw new RuntimeException("SCIP: Unknown linear operator: " + oper);
            }

        }
    }

    public Solution optimize(Objective objectiveDirection, ConstrainedVariable objectiveVar) {
        // Set Objective
        SCIPVariable scipObjective = (SCIPVariable) objectiveVar.getObject();
        model.createExpression().add(scipObjective).asObjective();

        Status status;
        if (Objective.MAXIMIZE.equals(objectiveDirection)) {
            status = model.maximize();
        } else if (Objective.MINIMIZE.equals(objectiveDirection)) {
            status = model.minimize();
        } else {
            throw new RuntimeException("Uknown optimization direction: " + objectiveDirection);
        }

        if (!status.equals(Status.OPTIMAL)) {
            System.out.println("SCIP cannot find an optimal solution");
            return null;
        }

        // double obj = model.getObjectiveValue();

        Solution solution = createSolution();
        
        return solution;
    }
    
    public Solution createSolution() {

        Var[] vars = problem.getVars();
        if (vars != null) {
            for (Var v : vars) {
                javax.constraints.impl.Var var = (javax.constraints.impl.Var) v;
                SCIPVariable scipVar = (SCIPVariable) var.getObject();
                double doubleValue = scipVar.getSolution();
                int value = (int) Math.floor(doubleValue);
                if (doubleValue != value) {
                    log("WARNING: " + var.getName() + " = " + doubleValue + " rounded to " + value);
                    numberOfRoundings++;
                }
                var.setValue(value);
            }
        }
        VarReal[] realVars = problem.getVarReals();
        if (realVars != null) {
            for (VarReal v : realVars) {
                javax.constraints.impl.VarReal var = (javax.constraints.impl.VarReal) v;
                SCIPVariable scipVar = (SCIPVariable) var.getObject();
                double value = scipVar.getSolution();
                //log(var.getName() + " = " + value);
                var.setValue(value);
            }
        }
        return new javax.constraints.impl.search.Solution(this, 1);
    }

    @Override
    public Solution findOptimalSolution(Objective objectiveDirection, Var objectiveVar) {
        init();
        problem.add(objectiveVar);
        return optimize(objectiveDirection, objectiveVar);
    }

    @Override
    public Solution findOptimalSolution(Objective objectiveDirection, VarReal objectiveVar) {
        init();
        problem.add(objectiveVar);
        return optimize(objectiveDirection, objectiveVar);
    }

    public String getCommanLine() {
        throw new RuntimeException("getCommanLine() should not be used for SCIP");
    }

    public String getVersion() {
        return JSR331_LINEAR_SOLVER_VERSION;
    }

    /**
     * SCIP minimizes by default
     */
    public Objective getDefaultOptimizationObjective() {
        return Objective.MINIMIZE;
    }

    public HashMap<String, String> readResults() {
        throw new RuntimeException("readResults() should not be used for SCIP");
    }
}
