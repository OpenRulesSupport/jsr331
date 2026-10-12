package javax.constraints.impl.constraint;

import javax.constraints.Var;
import javax.constraints.impl.Constraint;
import javax.constraints.impl.Problem;

import org.chocosolver.solver.variables.IntVar;

public class Linear extends Constraint {
    public Linear(Var left, String oper, Var right) {
        super(left.getProblem(), "Linear");
        Problem p = (Problem) left.getProblem();
        setImpl(p.compare(((javax.constraints.impl.Var) left).getChocoVar(), oper,
                ((javax.constraints.impl.Var) right).getChocoVar()));
    }

    public Linear(Var var, String oper, int value) {
        super(var.getProblem(), "Linear");
        Problem p = (Problem) var.getProblem();
        setImpl(p.compare(((javax.constraints.impl.Var) var).getChocoVar(), oper, value));
    }

    public Linear(int[] values, Var[] vars, String oper, int value) {
        super(vars[0].getProblem(), "Linear");
        Problem p = (Problem) vars[0].getProblem();
        setImpl(p.getChocoModel().scalar(p.createIntVarArray(vars), values,
                Problem.normalizeOperator(oper), value));
    }

    public Linear(Var[] vars, String oper, int value) {
        super(vars[0].getProblem(), "Linear");
        Problem p = (Problem) vars[0].getProblem();
        setImpl(p.getChocoModel().sum(p.createIntVarArray(vars), Problem.normalizeOperator(oper), value));
    }

    public Linear(int[] values, Var[] vars, String oper, Var var) {
        super(vars[0].getProblem(), "Linear");
        Problem p = (Problem) vars[0].getProblem();
        IntVar right = ((javax.constraints.impl.Var) var).getChocoVar();
        setImpl(p.getChocoModel().scalar(p.createIntVarArray(vars), values,
                Problem.normalizeOperator(oper), right));
    }

    public Linear(Var[] vars, String oper, Var var) {
        super(vars[0].getProblem(), "Linear");
        Problem p = (Problem) vars[0].getProblem();
        setImpl(p.getChocoModel().sum(p.createIntVarArray(vars), Problem.normalizeOperator(oper),
                ((javax.constraints.impl.Var) var).getChocoVar()));
    }
}
