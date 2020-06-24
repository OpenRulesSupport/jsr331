package jsetl.lib;

import jsetl.*;
import jsetl.exception.NotDefConstraintException;
import jsetl.SolverClass;
import java.util.Vector;

public class GlobalConstraints extends jsetl.NewConstraints {

    //Constructor
    public GlobalConstraints(SolverClass slv) {
        super(slv);
    }

    //Public Methods
    public ConstraintClass occurrence(Vector<IntLVar> l, IntLVar v, IntLVar n) {
        LList lList = new LList(l);
        IntLVar[] intLVars = new IntLVar[l.size()];
        for(int i = 0; i < l.size(); ++i)
            intLVars[i] = l.get(i);
        return new LArrayOps(solver).occurrence(intLVars,v,n);
    }

    protected void user_code(ConstraintClass c) throws NotDefConstraintException {
        if (c.getName().equals("occurrencegc")) {
            this.occurrence(c);
        } else {
            throw new NotDefConstraintException();
        }
    }

    private void occurrence(ConstraintClass constraint) {
        Vector<IntLVar> intLVars = (Vector) constraint.getArg(1);
        IntLVar v = (IntLVar) constraint.getArg(2);
        IntLVar n = (IntLVar) constraint.getArg(3);

        if (intLVars.isEmpty()) {
            this.solver.add(n.eq(0));
        }
        else {
            int l_dim = intLVars.size();
            int n_val = 0;
            int i;
            if (n.isBound()) {
                n_val = n.getValue();
                if (n_val < 0) {
                    constraint.fail();
                }
                else {
                    if (n_val == 0) {
                        for(i = 0; i < l_dim; ++i) {
                            this.solver.add(((IntLVar)intLVars.get(i)).neq(v));
                        }

                        return;
                    }

                    if (n_val > l_dim) {
                        constraint.fail();
                    }
                    else if (n_val == l_dim) {
                        for(i = 0; i < l_dim; ++i) {
                            this.solver.add((intLVars.get(i)).eq(v));
                        }

                        return;
                    }
                }
            }

            //n_val = 0;
            i = 0;
            MultiInterval dom_v = v.getDomain();
            MultiInterval dom_n = n.getDomain();
            boolean reduce_dom_v = false;
            MultiInterval dom_v_reduced = new MultiInterval();
            if (!v.isBound() && !dom_n.contains(0) && constraint.firstCall()) {
                reduce_dom_v = true;
            }

            Vector<IntLVar> l_new = new Vector();

            for(i = 0; i < l_dim; ++i) {
                if ((intLVars.get(i)).equals(v)) {
                    ++i;
                } else {
                    MultiInterval dom_li = (intLVars.get(i)).getDomain();
                    MultiInterval dom_liv = dom_li.intersect(dom_v);
                    if (!dom_liv.isEmpty()) {
                        ++n_val;
                        l_new.add(intLVars.get(i));
                    }

                    if (reduce_dom_v) {
                        dom_v_reduced = dom_v_reduced.union(dom_liv);
                    }
                }
            }

            if (n_val == l_dim) {
                constraint.notSolved();
                if (!n.isBound() && constraint.firstCall()) {
                    constraint.notFirstCall();
                    this.solver.add(n.dom(0, l_dim));
                }

                if (reduce_dom_v) {
                    constraint.notFirstCall();
                    this.solver.add(v.dom(dom_v_reduced));
                }
            } else if (i == 0) {
                this.solver.add(this.occurrence(l_new, v, n));
            } else {
                IntLVar m = new IntLVar();
                this.solver.add(this.occurrence(l_new, v, m).and(n.eq(m.sum(i))));
            }

        }
    }
}
