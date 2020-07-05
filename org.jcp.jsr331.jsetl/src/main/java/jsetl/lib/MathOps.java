package jsetl.lib;

import jsetl.*;
import jsetl.exception.NotDefConstraintException;

/* 
 * User-defined constraints implementing operations
 * on integer numbers.
 * 
 */

public class MathOps extends NewConstraints {
    public MathOps(SolverClass slv)
    {
        super(slv);
    }
  
    public ConstraintClass absTest(IntLVar x, IntLVar y) {
        return new ConstraintClass("absTest", x, y);
    }

    public ConstraintClass abs(IntLVar x, IntLVar y) {
        return new ConstraintClass("absND", x, y);
    }

    protected void user_code(ConstraintClass constraint) throws NotDefConstraintException
    {
        if (constraint.getName() == "absTest") absTest(constraint);
        else if (constraint.getName() == "absND") abs(constraint);
        else throw new NotDefConstraintException();
    }
    
    private void absTest(ConstraintClass c)  // x = |y|
     {
        IntLVar x = (IntLVar)c.getArg(1); 
        IntLVar y = (IntLVar)c.getArg(2); 
        if (!x.isBound()) {            // irreducible case
            c.notSolved(); 
            return;
        };  
        if (x.getValue() >= 0) 
            solver.add(x.eq(y));
        else
            solver.add(x.eq(new IntLVar(0).sub(y)));
        return;
    }
    
    private void abs(ConstraintClass c)  // x = |y|
     {  
        IntLVar x = (IntLVar)c.getArg(1); 
        IntLVar y = (IntLVar)c.getArg(2); 
        switch (c.getAlternative()) {
        case 0:
            solver.addChoicePoint(c);
            solver.add(x.ge(0).and(x.eq(y)));
            break;
        case 1:
            solver.add(x.lt(0).and(x.eq(new IntLVar(0).sub(y))));
        }
        return;
    }

}
