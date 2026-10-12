package javax.constraints.impl.constraint;

import javax.constraints.Var;
import org.maxicp.cp.CPFactory;
import org.maxicp.cp.engine.core.CPIntVar;

public class Element extends javax.constraints.impl.Constraint {
    public Element(int[] array, Var index, String oper, int value) {
        super(index.getProblem(), javax.constraints.impl.Problem.compare(
                CPFactory.element(array, Linear.unwrap(index)), oper, value));
    }

    public Element(int[] array, Var index, String oper, Var value) {
        super(index.getProblem(), javax.constraints.impl.Problem.compare(
                CPFactory.element(array, Linear.unwrap(index)), oper, Linear.unwrap(value)));
    }

    public Element(Var[] array, Var index, String oper, int value) {
        super(index.getProblem(), javax.constraints.impl.Problem.compare(
                element(array, index), oper, value));
    }

    public Element(Var[] array, Var index, String oper, Var value) {
        super(index.getProblem(), javax.constraints.impl.Problem.compare(
                element(array, index), oper, Linear.unwrap(value)));
    }

    private static CPIntVar element(Var[] array, Var index) {
        return CPFactory.element(
                ((javax.constraints.impl.Problem) index.getProblem()).toMaxiCP(array),
                Linear.unwrap(index));
    }
}
