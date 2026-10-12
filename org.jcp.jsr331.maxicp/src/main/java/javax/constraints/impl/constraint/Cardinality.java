package javax.constraints.impl.constraint;

import javax.constraints.Var;
import org.maxicp.cp.CPFactory;
import org.maxicp.cp.engine.core.CPBoolVar;
import org.maxicp.cp.engine.core.CPIntVar;

public class Cardinality extends javax.constraints.impl.Constraint {
    public Cardinality(Var[] vars, int cardValue, String oper, Var var) {
        super(var.getProblem(), javax.constraints.impl.Problem.compare(
                count(vars, cardValue), oper, Linear.unwrap(var)));
    }

    public Cardinality(Var[] vars, Var cardVar, String oper, Var var) {
        super(var.getProblem(), javax.constraints.impl.Problem.compare(
                count(vars, cardVar), oper, Linear.unwrap(var)));
    }

    public Cardinality(Var[] vars, int cardValue, String oper, int value) {
        super(vars[0].getProblem(), javax.constraints.impl.Problem.compare(
                count(vars, cardValue), oper, value));
    }

    public Cardinality(Var[] vars, Var cardVar, String oper, int value) {
        super(vars[0].getProblem(), javax.constraints.impl.Problem.compare(
                count(vars, cardVar), oper, value));
    }

    static CPIntVar count(Var[] vars, int value) {
        CPBoolVar[] matches = new CPBoolVar[vars.length];
        for (int i = 0; i < vars.length; i++)
            matches[i] = CPFactory.isEq(Linear.unwrap(vars[i]), value);
        return CPFactory.sum(matches);
    }

    static CPIntVar count(Var[] vars, Var value) {
        CPBoolVar[] matches = new CPBoolVar[vars.length];
        for (int i = 0; i < vars.length; i++)
            matches[i] = CPFactory.isEq(Linear.unwrap(vars[i]), Linear.unwrap(value));
        return CPFactory.sum(matches);
    }
}
