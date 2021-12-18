package it.ssc.samples;

/*
Consider the LP problem reported in the previous example and given by:

    max  X1 +   3X2
    
         X1 +    X2 ≥-1
         X1 + 1.4X2 ≤ 6
       -5X1 +   3X2 = 5
       
    with X1, X2 ≥ 0

The previous example problem can also be expressed through the use of vectors and matrices. 
The representation format used is similar to that of simplex solver Apache. In this case 
it is necessary to define the matrix A of coefficients, the vector c of the coefficients 
of o.f. and the vector b of the RHS values.

Defined such entities [lines 18-23] you create a LinearObjectiveFunction object [line 27] 
which represents the objective function and Constraint objects [line 31] that represent 
the constraints. The type of constraint (LE, GE, EQ) is specified through the elements of 
the vector of the rel relations [line 25]. Finally, the list of constraints and the objective 
function are enough parameters to instantiate an object of class LP [line 34].

*/

import it.ssc.log.SscLogger;
import it.ssc.pl.milp.ConsType;
import it.ssc.pl.milp.Constraint;
import it.ssc.pl.milp.GoalType;
import it.ssc.pl.milp.LP;
import it.ssc.pl.milp.LinearObjectiveFunction;
import it.ssc.pl.milp.Solution;
import it.ssc.pl.milp.SolutionType;
import it.ssc.pl.milp.Variable;

import java.util.ArrayList;

public class Example12 {

    public static void main(String[] args) throws Exception {

        double A[][] = { { 1.0, 1.0 }, { 1.0, 1.4 }, { -5.0, 3.0 } };
        double b[] = { -1.0, 6.0, 5.0 };
        double c[] = { 1.0, 3.0 };

        ConsType[] rel = { ConsType.GE, ConsType.LE, ConsType.EQ };

        LinearObjectiveFunction fo = new LinearObjectiveFunction(c, GoalType.MAX);

        ArrayList<Constraint> constraints = new ArrayList<Constraint>();
        for (int i = 0; i < A.length; i++) {
            constraints.add(new Constraint(A[i], rel[i], b[i]));
        }

        LP lp = new LP(fo, constraints);
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
