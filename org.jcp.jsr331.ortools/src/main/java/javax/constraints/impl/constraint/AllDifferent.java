package javax.constraints.impl.constraint;

import java.util.ArrayList;
import java.util.List;

import javax.constraints.ConsistencyLevel;
import javax.constraints.Var;
import javax.constraints.impl.Constraint;
import javax.constraints.impl.Problem;

import com.google.ortools.sat.Literal;

/** Pairwise-reified all-different constraint. */
public class AllDifferent extends Constraint {
    public AllDifferent(Var[] variables) {
        super(variables[0].getProblem(), "AllDiff", createTruth(variables));
    }

    private static com.google.ortools.sat.BoolVar createTruth(Var[] variables) {
        Problem problem = (Problem) variables[0].getProblem();
        List<Literal> different = new ArrayList<>();
        for (int left = 0; left < variables.length; left++) {
            for (int right = left + 1; right < variables.length; right++) {
                different.add(problem.relation(
                        ((javax.constraints.impl.Var) variables[left]).getCpVar(), "!=",
                        ((javax.constraints.impl.Var) variables[right]).getCpVar()));
            }
        }
        return problem.andTruth(different.toArray(new Literal[0]));
    }

    @Override
    public void post(ConsistencyLevel consistencyLevel) {
        post();
    }
}
