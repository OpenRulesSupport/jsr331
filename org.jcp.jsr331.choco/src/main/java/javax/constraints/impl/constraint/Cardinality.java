package javax.constraints.impl.constraint;

import javax.constraints.Var;
import javax.constraints.impl.Constraint;
import javax.constraints.impl.Problem;

import org.chocosolver.solver.variables.IntVar;

public class Cardinality extends Constraint {
    public Cardinality(Var[] vars, int cardValue, String oper, Var value) {
        super(vars[0].getProblem(), "Cardinality");
        Problem p = (Problem) vars[0].getProblem();
        IntVar count = p.getChocoModel().intVar("cardinality", 0, vars.length);
        p.getChocoModel().count(cardValue, p.createIntVarArray(vars), count).post();
        setImpl(p.compare(count, oper, ((javax.constraints.impl.Var) value).getChocoVar()));
    }

    public Cardinality(Var[] vars, Var cardValue, String oper, Var value) {
        super(vars[0].getProblem(), "Cardinality");
        Problem p = (Problem) vars[0].getProblem();
        IntVar count = p.getChocoModel().intVar("cardinality", 0, vars.length);
        p.getChocoModel().count(((javax.constraints.impl.Var) cardValue).getChocoVar(),
                p.createIntVarArray(vars), count).post();
        setImpl(p.compare(count, oper, ((javax.constraints.impl.Var) value).getChocoVar()));
    }

    public Cardinality(Var[] vars, int cardValue, String oper, int value) {
        super(vars[0].getProblem(), "Cardinality");
        Problem p = (Problem) vars[0].getProblem();
        IntVar count = p.getChocoModel().intVar("cardinality", 0, vars.length);
        p.getChocoModel().count(cardValue, p.createIntVarArray(vars), count).post();
        setImpl(p.compare(count, oper, value));
    }

    public Cardinality(Var[] vars, Var cardValue, String oper, int value) {
        super(vars[0].getProblem(), "Cardinality");
        Problem p = (Problem) vars[0].getProblem();
        IntVar count = p.getChocoModel().intVar("cardinality", 0, vars.length);
        p.getChocoModel().count(((javax.constraints.impl.Var) cardValue).getChocoVar(),
                p.createIntVarArray(vars), count).post();
        setImpl(p.compare(count, oper, value));
    }
}
