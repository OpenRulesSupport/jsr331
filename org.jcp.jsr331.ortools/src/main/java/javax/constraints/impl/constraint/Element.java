package javax.constraints.impl.constraint;

import java.util.Arrays;

import javax.constraints.Var;
import javax.constraints.impl.Constraint;
import javax.constraints.impl.Problem;

import com.google.ortools.sat.IntVar;

/** Zero-based element constraint. */
public class Element extends Constraint {
    public Element(int[] array, Var index, String operator, int value) {
        super(index.getProblem(), "Element",
                create(array, index, operator, value, null));
    }

    public Element(int[] array, Var index, String operator, Var value) {
        super(index.getProblem(), "Element",
                create(array, index, operator, 0, value));
    }

    public Element(Var[] array, Var index, String operator, int value) {
        super(index.getProblem(), "Element",
                create(array, index, operator, value, null));
    }

    public Element(Var[] array, Var index, String operator, Var value) {
        super(index.getProblem(), "Element",
                create(array, index, operator, 0, value));
    }

    private static com.google.ortools.sat.BoolVar create(
            int[] array, Var index, String operator, int constant, Var variable) {
        checkIndex(index, array.length);
        Problem problem = (Problem) index.getProblem();
        int minimum = Arrays.stream(array).min().orElseThrow();
        int maximum = Arrays.stream(array).max().orElseThrow();
        IntVar element = problem.getCpModel().newIntVar(
                minimum, maximum, problem.nextName("element"));
        problem.getCpModel().addElement(
                ((javax.constraints.impl.Var) index).getCpVar(), array, element);
        return variable == null
                ? problem.relation(element, operator, constant)
                : problem.relation(element, operator,
                        ((javax.constraints.impl.Var) variable).getCpVar());
    }

    private static com.google.ortools.sat.BoolVar create(
            Var[] array, Var index, String operator, int constant, Var variable) {
        checkIndex(index, array.length);
        Problem problem = (Problem) index.getProblem();
        int minimum = Integer.MAX_VALUE;
        int maximum = Integer.MIN_VALUE;
        for (Var item : array) {
            minimum = Math.min(minimum, item.getMin());
            maximum = Math.max(maximum, item.getMax());
        }
        IntVar element = problem.getCpModel().newIntVar(
                minimum, maximum, problem.nextName("element"));
        problem.getCpModel().addElement(
                ((javax.constraints.impl.Var) index).getCpVar(),
                problem.createIntVarArray(array), element);
        return variable == null
                ? problem.relation(element, operator, constant)
                : problem.relation(element, operator,
                        ((javax.constraints.impl.Var) variable).getCpVar());
    }

    private static void checkIndex(Var index, int length) {
        if (length == 0 || index.getMin() > length - 1 || index.getMax() < 0) {
            throw new IllegalArgumentException("elementAt: invalid index variable");
        }
    }
}
