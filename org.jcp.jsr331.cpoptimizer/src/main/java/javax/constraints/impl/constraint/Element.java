package javax.constraints.impl.constraint;

import java.util.Arrays;

import javax.constraints.Var;
import javax.constraints.impl.Constraint;
import javax.constraints.impl.Problem;

import ilog.concert.IloException;
import ilog.concert.IloIntExpr;

/** Zero-based CP Optimizer element constraint. */
public class Element extends Constraint {
    public Element(int[] array, Var index, String operator, int value) {
        super(index.getProblem(), "Element", create(array, index, operator, value, null));
    }
    public Element(int[] array, Var index, String operator, Var value) {
        super(index.getProblem(), "Element", create(array, index, operator, 0, value));
    }
    public Element(Var[] array, Var index, String operator, int value) {
        super(index.getProblem(), "Element", create(array, index, operator, value, null));
    }
    public Element(Var[] array, Var index, String operator, Var value) {
        super(index.getProblem(), "Element", create(array, index, operator, 0, value));
    }

    private static ilog.concert.IloConstraint create(
            int[] array, Var index, String operator, int constant, Var variable) {
        checkIndex(index, array.length);
        Problem problem = (Problem) index.getProblem();
        try {
            IloIntExpr element = problem.getCp().element(
                    array, ((javax.constraints.impl.Var) index).getCpVar());
            return variable == null
                    ? problem.relation(element, operator, constant)
                    : problem.relation(element, operator,
                            ((javax.constraints.impl.Var) variable).getCpVar());
        } catch (IloException exception) {
            throw Problem.failure("create integer element constraint", exception);
        }
    }

    private static ilog.concert.IloConstraint create(
            Var[] array, Var index, String operator, int constant, Var variable) {
        checkIndex(index, array.length);
        Problem problem = (Problem) index.getProblem();
        try {
            IloIntExpr element = problem.getCp().element(problem.expressions(array),
                    ((javax.constraints.impl.Var) index).getCpVar());
            return variable == null
                    ? problem.relation(element, operator, constant)
                    : problem.relation(element, operator,
                            ((javax.constraints.impl.Var) variable).getCpVar());
        } catch (IloException exception) {
            throw Problem.failure("create variable element constraint", exception);
        }
    }

    private static void checkIndex(Var index, int length) {
        if (length == 0 || index.getMin() > length - 1 || index.getMax() < 0) {
            throw new IllegalArgumentException("elementAt: invalid index variable");
        }
    }
}
