package javax.constraints.impl.constraint;

import javax.constraints.Var;
import javax.constraints.impl.Constraint;
import javax.constraints.impl.Problem;

import com.google.ortools.sat.BoolVar;
import com.google.ortools.sat.LinearExpr;

/** Counts occurrences of a value and compares the count. */
public class Cardinality extends Constraint {
    public Cardinality(Var[] variables, int cardValue, String operator, Var value) {
        super(variables[0].getProblem(), "Cardinality",
                create(variables, cardValue, null, operator, value, null));
    }

    public Cardinality(Var[] variables, Var cardValue, String operator, Var value) {
        super(variables[0].getProblem(), "Cardinality",
                create(variables, 0, cardValue, operator, value, null));
    }

    public Cardinality(Var[] variables, int cardValue, String operator, int value) {
        super(variables[0].getProblem(), "Cardinality",
                create(variables, cardValue, null, operator, null, value));
    }

    public Cardinality(Var[] variables, Var cardValue, String operator, int value) {
        super(variables[0].getProblem(), "Cardinality",
                create(variables, 0, cardValue, operator, null, value));
    }

    private static BoolVar create(
            Var[] variables,
            int fixedCardValue,
            Var variableCardValue,
            String operator,
            Var variableResult,
            Integer fixedResult) {
        Problem problem = (Problem) variables[0].getProblem();
        BoolVar[] matches = new BoolVar[variables.length];
        for (int i = 0; i < variables.length; i++) {
            matches[i] = variableCardValue == null
                    ? problem.relation(
                            ((javax.constraints.impl.Var) variables[i]).getCpVar(),
                            "=", fixedCardValue)
                    : problem.relation(
                            ((javax.constraints.impl.Var) variables[i]).getCpVar(),
                            "=", ((javax.constraints.impl.Var) variableCardValue).getCpVar());
        }
        return variableResult == null
                ? problem.relation(LinearExpr.sum(matches), operator, fixedResult)
                : problem.relation(LinearExpr.sum(matches), operator,
                        ((javax.constraints.impl.Var) variableResult).getCpVar());
    }
}
