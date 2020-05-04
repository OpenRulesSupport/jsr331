package it.ssc.samples;

/*
 
Resolution of a LP problem with bounded variables and free formulated through matrices

Consider the following LP problem reported in the previous example:

     max  X1 +   3X2
     
          X1 +    X2 ≥ 1
          X1 + 1.4X2 ≤ 6
        -5X1 +   3X2 = 5
        
     with -1 ≤ X1 ≤ +1
         -∞ ≤ X2 ≤ +∞  

Let's solve the previous example using the matrix format. In order to represent the variable X1,
which is bounded both inferiorly and superiorly, it is necessary to add to the matrix A a line 
for the upper bounds and one for the lower bounds [lines 21-22]. In the case of the variable X2, 
or of a free variable without limits, it is sufficient to specify a lower bound and an upper 
bound indefinite with the value NaN.

Recall that in order to set the free variables, or variables that can assume negative values, 
must be set a lower bound infinity (-∞) or negative, or an upper bound negative.

It is then necessary to add in the vector of relations [line 27] the constants that declare that 
the last two lines of the matrix A are relative to the upper and lower bounds (add ConsType.UPPER 
and ConsType.LOWER). Finally, the size of vector b must also be updated (adding two NaN values) 
which must be equal to the number of staves of the matrix A.
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
import static it.ssc.pl.milp.LP.NaN;
import java.util.ArrayList;

public class Example14 {
     
    public static void main(String[] args) throws Exception {
 
        double A[][]={ 
                { 1.0 , 1.0  },
                { 1.0 , 1.4  },
                {-5.0 , 3.0  },
                { 1.0 , NaN },
                {-1.0 , NaN }} ;
         
        double b[]= { 1.0, 6.0 ,5.0, NaN, NaN };
        double c[]= { 1.0, 3.0  };  
 
        ConsType[] rel= {ConsType.GE, ConsType.LE, ConsType.EQ, ConsType.UPPER, ConsType.LOWER};
 
        LinearObjectiveFunction f = new LinearObjectiveFunction(c, GoalType.MAX);
 
        ArrayList< Constraint > constraints = new ArrayList< Constraint >();
        for(int i=0; i < A.length; i++) {
            constraints.add(new Constraint(A[i], rel[i], b[i]));
        }
 
        LP lp = new LP(f,constraints); 
        SolutionType solution_type=lp.resolve();
 
        if(solution_type==SolutionType.OPTIMUM) { 
            Solution solution=lp.getSolution();
            for(Variable var:solution.getVariables()) {
                SscLogger.log("Variable name :"+var.getName() + " value:"+var.getValue());
            }
            SscLogger.log("Value :"+solution.getOptimumValue());
        }   
    }
}
             
             
 
            
