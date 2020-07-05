package jsetl.lib;

import java.util.ArrayList;

import jsetl.*;
import jsetl.exception.NotDefConstraintException;

/* 
 * A collection of user-defined constraints implementing operations
 * on arrays of integer logical variables (IntLVar).
 * 
 */

public class LArrayOps extends NewConstraints {
    public LArrayOps(SolverClass slv)
    {
        super(slv);
    }

    // True if x is the i'th element of array a. Counting starts at 0.  
    public ConstraintClass ithElem(IntLVar[] a, IntLVar i, IntLVar x) {
        return new ConstraintClass("ithElem", a, i, x);
    }
    
    // True if array a contains exactly n elements with value v
    public ConstraintClass occurrence(IntLVar[] a, IntLVar v, IntLVar n) {
      return new ConstraintClass("occurrence", a, v, n);
    }

    // True if v is the scalar product of arrays v1 and v2.  
    public ConstraintClass scalarProdArray(IntLVar[] a, IntLVar[] b, IntLVar v) {
       return new ConstraintClass("scalarProdArray", a, b, v);
    }

	protected void user_code(ConstraintClass constraint) throws NotDefConstraintException
    {
        if (constraint.getName().equals("ithElem")) ithElem(constraint);
        else if (constraint.getName().equals("occurrence")) occurrence(constraint);
        else if (constraint.getName().equals("scalarProdArray")) scalarProdArray(constraint);
        else throw new NotDefConstraintException();
    }
        
    private void ithElem(ConstraintClass c) {
    	IntLVar[] a = (IntLVar[])c.getArg(1); 
        IntLVar i = (IntLVar)c.getArg(2);
        IntLVar x = (IntLVar)c.getArg(3);
        if (!i.isBound()) {c.notSolved(); return;};  // irreducible case
        if (a.length == 0) c.fail(); 
        solver.add(x.eq(a[i.getValue()]));
        return;
    }
       
    private void occurrence(ConstraintClass c) {
        IntLVar[] l = (IntLVar[]) c.getArg(1);
        IntLVar v = (IntLVar) c.getArg(2);
        IntLVar n = (IntLVar) c.getArg(3);
        if (l.length == 0) {
          solver.add(n.eq(0));
          return;
        }
        int l_dim = l.length;
        if (n.isBound()) {
          int n_val = n.getValue();
          if (n_val < 0) {
            c.fail();
          } else if (n_val == 0) {
            for (int i = 0; i < l_dim; i++) {
              solver.add(l[i].neq(v));
            }
            return;
          } else if (n_val > l_dim) {
            c.fail();
          } else if (n_val == l_dim) {
            for (int i = 0; i < l_dim; i++) {
              solver.add(l[i].eq(v));
            }
            return;
          }
        }
        // n unbound OR n bound and n.getValue() < l_dim          
        int count = 0, identicals = 0;
        MultiInterval dom_v = v.getDomain();
        MultiInterval dom_n = n.getDomain();
        boolean reduce_dom_v = false;
        MultiInterval dom_v_reduced = new MultiInterval();
        if (!v.isBound() && !dom_n.contains(0) && c.firstCall()) {
          reduce_dom_v = true;
        }
        ArrayList<IntLVar> l_new = new ArrayList<IntLVar>();
        for (int i = 0; i < l_dim; i++) {
          if (l[i].equals(v)) {
            identicals++;
          } else {
            MultiInterval dom_li = l[i].getDomain();
            MultiInterval dom_liv = dom_li.intersect(dom_v);
            if (!dom_liv.isEmpty()) {
              count++;
              l_new.add(l[i]);
            }
            if (reduce_dom_v) {
              dom_v_reduced = dom_v_reduced.union(dom_liv);
            }
          }
        }
        if (count == l_dim) {  // irreducible form
          c.notSolved();
          if (!n.isBound() && c.firstCall()) {
            c.notFirstCall();
            solver.add(n.dom(0, l_dim));   // to be improved!
          }
          if (reduce_dom_v) {
            c.notFirstCall();
            solver.add(v.dom(dom_v_reduced));
          }
        } else {
          IntLVar[] l_new_array = new IntLVar[l_new.size()];
          for(int i=0; i<l_new.size(); i++) 
        	  l_new_array[i] = (IntLVar)l_new.get(i);
          if (identicals == 0) {
            solver.add(occurrence(l_new_array,v,n));
          } else {
            IntLVar m = new IntLVar();
            solver.add(occurrence(l_new_array,v,m).and(n.eq(m.sum(identicals))));
          }
        }
        return;
      }

    private void scalarProdArray(ConstraintClass c) {
    	IntLVar[] a = (IntLVar[]) c.getArg(1);
    	IntLVar[] b = (IntLVar[]) c.getArg(2);
        IntLVar v = (IntLVar) c.getArg(3);

	    if (a.length==0 || b.length==0) {
	      c.fail();
	    }
	    if (a.length != b.length) {
	      c.fail();
	    }
	    int v_sum = 0;
	    for (int i = 0; i < a.length; ++i) {
	      if (a[i].isBound() && b[i].isBound()) {
	        v_sum += a[i].getValue() * b[i].getValue();
	      } else {
	        c.notSolved();
	        return ;
	      }
	    }
	    solver.add(v.eq(v_sum));
	    return;
	}
}


   
