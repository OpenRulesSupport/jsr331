package javax.constraints.impl.constraint;

import java.util.Arrays;

import javax.constraints.Var;
import javax.constraints.impl.Constraint;
import javax.constraints.impl.Problem;

import org.chocosolver.solver.variables.IntVar;

public class Element extends Constraint {
    public Element(int[] array, Var index, String oper, int value) {
        super(index.getProblem(), "Element");
        Problem p = (Problem) index.getProblem();
        checkIndex(index, array.length);
        IntVar element = p.getChocoModel().intVar("element", Arrays.stream(array).min().orElse(0),
                Arrays.stream(array).max().orElse(0));
        p.getChocoModel().element(element, array, ((javax.constraints.impl.Var) index).getChocoVar(), 0).post();
        setImpl(p.compare(element, oper, value));
    }

    public Element(int[] array, Var index, String oper, Var value) {
        super(index.getProblem(), "Element");
        Problem p = (Problem) index.getProblem();
        checkIndex(index, array.length);
        IntVar element = p.getChocoModel().intVar("element", Arrays.stream(array).min().orElse(0),
                Arrays.stream(array).max().orElse(0));
        p.getChocoModel().element(element, array, ((javax.constraints.impl.Var) index).getChocoVar(), 0).post();
        setImpl(p.compare(element, oper, ((javax.constraints.impl.Var) value).getChocoVar()));
    }

    public Element(Var[] array, Var index, String oper, int value) {
        super(index.getProblem(), "Element");
        Problem p = (Problem) index.getProblem();
        checkIndex(index, array.length);
        IntVar[] vars = p.createIntVarArray(array);
        IntVar element = resultVar(p, vars);
        p.getChocoModel().element(element, vars, ((javax.constraints.impl.Var) index).getChocoVar(), 0).post();
        setImpl(p.compare(element, oper, value));
    }

    public Element(Var[] array, Var index, String oper, Var value) {
        super(index.getProblem(), "Element");
        Problem p = (Problem) index.getProblem();
        checkIndex(index, array.length);
        IntVar[] vars = p.createIntVarArray(array);
        IntVar element = resultVar(p, vars);
        p.getChocoModel().element(element, vars, ((javax.constraints.impl.Var) index).getChocoVar(), 0).post();
        setImpl(p.compare(element, oper, ((javax.constraints.impl.Var) value).getChocoVar()));
    }

    private static IntVar resultVar(Problem p, IntVar[] vars) {
        int min = Integer.MAX_VALUE, max = Integer.MIN_VALUE;
        for (IntVar var : vars) { min = Math.min(min, var.getLB()); max = Math.max(max, var.getUB()); }
        return p.getChocoModel().intVar("element", min, max);
    }

    private static void checkIndex(Var index, int length) {
        if (length == 0 || index.getMin() > length - 1 || index.getMax() < 0)
            throw new IllegalArgumentException("elementAt: invalid index variable");
    }
}
