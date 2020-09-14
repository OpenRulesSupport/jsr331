package javax.constraints.linear.impl;

import java.util.HashMap;
import java.util.Map;

import javax.constraints.ConstrainedVariable;
import javax.constraints.Constraint;
import javax.constraints.Objective;
import javax.constraints.Problem;
import javax.constraints.Solution;
import javax.constraints.Var;
import javax.constraints.VarReal;

import com.quantego.clp.CLP;
import com.quantego.clp.CLP.STATUS;
import com.quantego.clp.CLPConstraint;
import com.quantego.clp.CLPConstraint.TYPE;
import com.quantego.clp.CLPVariable;

public class LinearSolver extends javax.constraints.linear.LinearSolver {

    static public final String JSR331_LINEAR_SOLVER_VERSION = "CLP v.1.16.10 using clp-java";

    Problem problem;
    CLP model;
    int numberOfRoundings;

    public LinearSolver() {
        numberOfRoundings = 0;
    }

    public void init() {
        problem = getProblem();

        // model = new CLP().buffer(10).presolve(false).maxIterations(1);
        model = new CLP(); // .verbose(1);

        // Add CLPVariables (int + real)
        Var[] vars = problem.getVars();
        int intSize = 0;
        if (vars != null)
            intSize = vars.length;
        VarReal[] varReals = problem.getVarReals();
        int realSize = 0;
        if (varReals != null)
            realSize = varReals.length;
        CLPVariable[] clpVars = new CLPVariable[intSize + realSize];
        int n = 0;
        if (intSize > 0) {
            for (Var var : vars) {
                CLPVariable clpVar = model.addVariable().lb(var.getMin()).ub(var.getMax()).name(var.getName());
                var.setObject(clpVar);
                clpVars[n++] = clpVar;
            }
        }
        if (realSize > 0) {
            for (VarReal var : varReals) {
                CLPVariable clpVar = model.addVariable().lb(var.getMin()).ub(var.getMax()).name(var.getName());
                var.setObject(clpVar);
                clpVars[n++] = clpVar;
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
            Map<CLPVariable, Double> lhs = new HashMap<CLPVariable, Double>();
            for (int i = 0; i < constraintVars.length; i++) {
                ConstrainedVariable var = constraintVars[i];
                CLPVariable clpVar = (CLPVariable) var.getObject();
                if (clpVar == null) {
                    throw new RuntimeException(
                            "The variable " + var.getName() + " does not have an associated CLP variable");
                }
                Double coef = constraintCoefficients[i];
                lhs.put(clpVar, coef);
            }
            TYPE type = CLPConstraint.TYPE.EQ;
            if ("=".equals(oper))
                type = CLPConstraint.TYPE.EQ;
            else if (">=".equals(oper))
                type = CLPConstraint.TYPE.GEQ;
            else if (">".equals(oper)) {
                type = CLPConstraint.TYPE.GEQ;
                rhs += precision;
            } else if ("<".equals(oper)) {
                type = CLPConstraint.TYPE.LEQ;
                rhs -= precision;
            } else if ("<=".equals(oper))
                type = CLPConstraint.TYPE.LEQ;
            else if ("!=".equals(oper))
                type = CLPConstraint.TYPE.NEQ;
            else {
                throw new RuntimeException("Uknown linear operator: " + oper);
            }

            model.addConstraint(lhs, type, rhs);
        }
    }

    public Solution optimize(Objective objectiveDirection, ConstrainedVariable objectiveVar) {
        // Set Objective
        CLPVariable clpObjective = (CLPVariable) objectiveVar.getObject();
        model.createExpression().add(clpObjective).asObjective();

        STATUS status;
        if (Objective.MAXIMIZE.equals(objectiveDirection)) {
            status = model.maximize();
        } else if (Objective.MINIMIZE.equals(objectiveDirection)) {
            status = model.minimize();
        } else {
            throw new RuntimeException("Uknown optimization direction: " + objectiveDirection);
        }

        if (!status.equals(STATUS.OPTIMAL)) {
            System.out.println("CLP cannot find an optimal solution");
            return null;
        }

        // double obj = model.getObjectiveValue();

        Solution solution = createSolution();
        if (numberOfRoundings > 0) {
            log("\n=================================================================");
            log("ATTENTION: CLP does not support MIP with integer variables!");
            log("The following solution is probably infeasible!");
            log("The number of rounded variables: " + numberOfRoundings);
            log("You have the following options:");
            log("1) Switch to a diffrent solver;");
            log("2) Replace all integer variables with real variables;");
            log("3) Adjust this 'solution' manually or apply a 'feasibility pump'");
            log("=================================================================\n");
        }
        
        return solution;
    }
    
    public Solution createSolution() {

        Var[] vars = problem.getVars();
        if (vars != null) {
            for (Var v : vars) {
                javax.constraints.impl.Var var = (javax.constraints.impl.Var) v;
                CLPVariable clpVar = (CLPVariable) var.getObject();
                double doubleValue = clpVar.getSolution();
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
                CLPVariable clpVar = (CLPVariable) var.getObject();
                double value = clpVar.getSolution();
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
        throw new RuntimeException("getCommanLine() should not be used for CLP");
    }

    public String getVersion() {
        return JSR331_LINEAR_SOLVER_VERSION;
    }

    /**
     * CLP minimizes by default
     */
    public Objective getDefaultOptimizationObjective() {
        return Objective.MINIMIZE;
    }

    public HashMap<String, String> readResults() {
        throw new RuntimeException("readResults() should not be used for CLP");
    }
}
