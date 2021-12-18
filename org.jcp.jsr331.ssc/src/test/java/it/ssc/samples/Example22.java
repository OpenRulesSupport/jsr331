package it.ssc.samples;

import it.ssc.log.SscLogger;
import it.ssc.pl.milp.ConsType;
import it.ssc.pl.milp.Constraint;
import it.ssc.pl.milp.GoalType;
import it.ssc.pl.milp.LinearObjectiveFunction;
import it.ssc.pl.milp.MILP;
import it.ssc.pl.milp.Solution;
import it.ssc.pl.milp.SolutionType;
import it.ssc.pl.milp.Variable;
import static it.ssc.pl.milp.LP.NaN;
import java.util.ArrayList;

public class Example22 {
    public static void main(String[] args) throws Exception {

        double A[][] = { 
                { 1.0, 1.0 }, 
                { 1.0, 1.4 }, 
                { -5.0, 3.0 }, 
                { 1.0, 1.0 }, // rigo della matrice per la definizione degli integer
        };
        double b[] = { 1.0, 6.0, 5.0, NaN };
        double c[] = { 1.0, 3.0 };

        ConsType[] rel = { ConsType.GE, ConsType.LE, ConsType.LE, ConsType.INT };

        LinearObjectiveFunction f = new LinearObjectiveFunction(c, GoalType.MAX);

        ArrayList<Constraint> constraints = new ArrayList<Constraint>();
        for (int i = 0; i < A.length; i++) {
            constraints.add(new Constraint(A[i], rel[i], b[i]));
        }

        MILP lp = new MILP(f, constraints);
        SolutionType solution_type = lp.resolve();

        if (solution_type == SolutionType.OPTIMUM) {
            Solution solution = lp.getSolution();
            for (Variable var : solution.getVariables()) {
                SscLogger.log("Variable name :" + var.getName() + " value:" + var.getValue());
            }
            SscLogger.log("o.f. value:" + solution.getOptimumValue());
        }
    }
}
