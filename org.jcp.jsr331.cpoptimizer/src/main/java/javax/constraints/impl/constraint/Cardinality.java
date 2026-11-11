package javax.constraints.impl.constraint;

import javax.constraints.Var;
import javax.constraints.impl.Constraint;
import javax.constraints.impl.Problem;

import ilog.concert.IloException;
import ilog.concert.IloIntExpr;

/** Counts occurrences of a value and compares the count. */
public class Cardinality extends Constraint {
    public Cardinality(Var[] variables, int cardValue, String operator, Var value) {
        super(variables[0].getProblem(), "Cardinality",
                create(variables, cardValue, operator, value, null));
    }
    public Cardinality(Var[] variables, Var cardValue, String operator, Var value) {
        super(variables[0].getProblem(), "Cardinality",
                createVariableValue(variables, cardValue, operator, value, null));
    }
    public Cardinality(Var[] variables, int cardValue, String operator, int value) {
        super(variables[0].getProblem(), "Cardinality",
                create(variables, cardValue, operator, null, value));
    }
    public Cardinality(Var[] variables, Var cardValue, String operator, int value) {
        super(variables[0].getProblem(), "Cardinality",
                createVariableValue(variables, cardValue, operator, null, value));
    }

    private static ilog.concert.IloConstraint create(
            Var[] variables, int cardValue, String operator,
            Var variableResult, Integer fixedResult) {
        Problem problem = (Problem) variables[0].getProblem();
        try {
            IloIntExpr count = problem.getCp().count(problem.expressions(variables), cardValue);
            return variableResult == null
                    ? problem.relation(count, operator, fixedResult)
                    : problem.relation(count, operator,
                            ((javax.constraints.impl.Var) variableResult).getCpVar());
        } catch (IloException exception) {
            throw Problem.failure("create cardinality constraint", exception);
        }
    }

    private static ilog.concert.IloConstraint createVariableValue(
            Var[] variables, Var cardValue, String operator,
            Var variableResult, Integer fixedResult) {
        Problem problem = (Problem) variables[0].getProblem();
        IloIntExpr[] matches = new IloIntExpr[variables.length];
        for (int index = 0; index < variables.length; index++) {
            matches[index] = problem.reify(problem.relation(
                    ((javax.constraints.impl.Var) variables[index]).getCpVar(), "=",
                    ((javax.constraints.impl.Var) cardValue).getCpVar()));
        }
        try {
            IloIntExpr count = problem.getCp().sum(matches);
            return variableResult == null
                    ? problem.relation(count, operator, fixedResult)
                    : problem.relation(count, operator,
                            ((javax.constraints.impl.Var) variableResult).getCpVar());
        } catch (IloException exception) {
            throw Problem.failure("create variable-value cardinality constraint", exception);
        }
    }
}
