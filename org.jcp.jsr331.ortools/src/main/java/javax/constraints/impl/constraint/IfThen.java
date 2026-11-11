package javax.constraints.impl.constraint;

import javax.constraints.impl.Constraint;
import javax.constraints.impl.Problem;

public class IfThen extends Constraint {
    public IfThen(
            javax.constraints.Constraint antecedent,
            javax.constraints.Constraint consequent) {
        super(antecedent.getProblem(), "IfThen", implication(antecedent, consequent));
    }

    private static com.google.ortools.sat.BoolVar implication(
            javax.constraints.Constraint antecedent,
            javax.constraints.Constraint consequent) {
        Problem problem = (Problem) antecedent.getProblem();
        return problem.orTruth(
                problem.notTruth(((Constraint) antecedent).getTruthVar()),
                ((Constraint) consequent).getTruthVar());
    }
}
