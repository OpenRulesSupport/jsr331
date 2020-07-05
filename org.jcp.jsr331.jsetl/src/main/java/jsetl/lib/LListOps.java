package jsetl.lib;

import jsetl.*;
import jsetl.exception.NotDefConstraintException;

/* 
 * A collection of user-defined constraints implementing operations
 * on (possibly partially specified) logical lists of elements of
 * ANY TYPE, exploiting nondeterminism.
 * 
 */
public class LListOps extends NewConstraints {
    public LListOps(SolverClass slv)
    {
        super(slv);
    }

    // True if x is a member of l
    public ConstraintClass member(LList l, LVar x) {
        return new ConstraintClass("member", l, x);
    }
    
    // True if x is not a member of l
    public ConstraintClass nmember(LList l, LVar x) {
        return new ConstraintClass("nmember", l, x);
    }
    
    // True if l3 is the concatenation of l1 and l2
    public ConstraintClass concat(LList l1, LList l2, LList l3) {
            return new ConstraintClass("concat",l1, l2, l3);
    }

    // True if x is the i'th element of l. Counting starts at 0.  
    public ConstraintClass ithElem(LList l, IntLVar i, LVar x) {
        return new ConstraintClass("ithElem", l, i, x);
    }
    
    // True if p is a prefix of l 
    public ConstraintClass prefix(LList l, LList p) {
            return new ConstraintClass("prefix", l, p);
    }
    
    // True if sl is a sublist of l
    public ConstraintClass sublist(LList l, LList sl) {
            return new ConstraintClass("sublist", l, sl);
    }

    // True if the list l contains exactly n elements with value v 
    public ConstraintClass occurrence(LList l, LVar v, IntLVar n) {
            return new ConstraintClass("occurrenceND", l, v, n);
    }

    // True if the list l1 is split in exactly two lists l2 and l3 
    public ConstraintClass split(LList l1, LList l2, LList l3) {
            return new ConstraintClass("split", l1, l2, l3);
    }

    // True if 'sum' is the sum of all elements of 'l' 
    // ('l': list of known integers)
    public ConstraintClass sumIntList(LList l, LVar sum) {
        return new ConstraintClass("sumIntList", l, sum);
    }
  
    // True if 'sum' is the sum of the lengths of all strings in 'l' 
    // ('l': list of known strings)
    public ConstraintClass sumStringList(LList l, LVar sum) {
        return new ConstraintClass("sumStringList", l, sum);
    }
 
    // True if 'fsum' is the sum of all elements of 'l' 
    // ('l': list of either known or unknown integers)
    public ConstraintClass sumIntListND(LList l, IntLVar fsum) {
        return sumIntListND(l, new IntLVar(0), fsum); 
    }
    private ConstraintClass sumIntListND(LList l, IntLVar psum, IntLVar fsum) {
        return new ConstraintClass("sumIntListND", l, psum, fsum);
    }
    
    protected void user_code(ConstraintClass constraint) throws  NotDefConstraintException
    {
        if (constraint.getName() == "member") member(constraint);
        else if(constraint.getName() == "nmember") nmember(constraint);
        else if(constraint.getName() == "concat") concat(constraint);
        else if(constraint.getName() == "prefix") prefix(constraint);
        else if(constraint.getName() == "sublist") sublist(constraint);
        else if(constraint.getName() == "ithElem") ithElem(constraint);
        else if(constraint.getName() == "occurrenceND") occurrenceND(constraint);
        else if(constraint.getName() == "split") split(constraint);
        else if(constraint.getName() == "sumIntList") sumIntList(constraint);
        else if(constraint.getName() == "sumStringList") sumStringList(constraint);
        else if(constraint.getName() == "sumIntListND") sumIntListND(constraint);
        else throw new NotDefConstraintException();
    }
        
    private void member(ConstraintClass c)  // using a LList
     {  
        LList l = (LList)c.getArg(1); 
        if (!l.isBound()) {c.notSolved(); return;};  // irreducible case
        if (l.isEmpty()) c.fail(); 
        LVar z = (LVar)c.getArg(2);  
        switch(c.getAlternative())
        {
        case 0: solver.addChoicePoint(c);
                solver.add(z.eq(l.get(0)));
                break;
        case 1: LList r = new LList();
                solver.add(l.eq(r.ins(new LVar())));
                solver.add(member(r,z));
                break;
        }
        return;
    }

    private void nmember(ConstraintClass c)  // using a LList
     {  
        LList l = (LList)c.getArg(1); 
        if (!l.isBound()) {c.notSolved(); return;};  // irreducible case
        if (l.isEmpty()) return; 
        LVar z = (LVar)c.getArg(2);  
        LList r = new LList();
        LVar x = new LVar();
        solver.add(l.eq(r.ins(x)).and(x.neq(z)));
        solver.add(x.neq(z).and(nmember(r,z)));
        return;
    }

    private void concat(ConstraintClass c)
    {
        LList l1    = (LList)c.getArg(1);
        LList l2    = (LList)c.getArg(2);
        LList l3    = (LList)c.getArg(3);

        switch(c.getAlternative())
        {
        case 0: solver.addChoicePoint(c);
                solver.add(l1.eq(LList.empty()));
                solver.add(l2.eq(l3));
                break;
        case 1: LVar  x = new LVar();
                LList l1_aux = new LList();
                LList l3_aux = new LList();
                solver.add(l1.eq(l1_aux.ins(x)));
                solver.add(l3.eq(l3_aux.ins(x)));
                solver.add(concat(l1_aux,l2,l3_aux));
                break;
        }
    }

    private void ithElem(ConstraintClass c)
     {
        LList l = (LList)c.getArg(1); 
        IntLVar i = (IntLVar)c.getArg(2);
        LVar x = (LVar)c.getArg(3);
        if (!l.isBound()) {c.notSolved(); return;};  // irreducible case
        if (!i.isBound()) {c.notSolved(); return;};  // irreducible case
        if (l.isEmpty()) c.fail(); 
        solver.add(x.eq(l.get(i.getValue())));
        return;
    }
    
    private void sublist(ConstraintClass c)
    {
        LList L    = (LList)c.getArg(1);
        LList SL   = (LList)c.getArg(2);
        switch(c.getAlternative())
        {
        case 0: solver.addChoicePoint(c);
                solver.add(prefix(L,SL));
                break;
        case 1: LVar  x = new LVar();
                LList RL = new LList();
                LList SRL = new LList();
                solver.add(L.eq(RL.ins(x)));        // L = [x|RL]
                solver.add(sublist(RL,SRL));
                
//              solver.add(SRL.neq(LList.empty())); // SRL neq [] not implemented yet
                LList aux = new LList();            // replaced by SRL = [_x|_aux]
                solver.add(SRL.eq(aux.ins(new LVar())));
                
                solver.add(SL.eq(SRL));
                break;
        }
    }

    private void prefix(ConstraintClass c)
    {
        LList L   = (LList)c.getArg(1);
        LList P   = (LList)c.getArg(2);
        switch(c.getAlternative())
        {
        case 0: solver.addChoicePoint(c);
                solver.add(P.eq(LList.empty())); //P = []
                break;
        case 1: LVar  x = new LVar();
                LList RL = new LList();
                LList RP = new LList();
                solver.add(L.eq(RL.ins(x)));  // L = [x|RL]
                solver.add(prefix(RL,RP));    // prefix(RL,RP)
                solver.add(P.eq(RP.ins(x)));  // P = [x|RP]
                break;
        }
    }
   
    private void occurrenceND(ConstraintClass c)
    {
        LList l    = (LList)c.getArg(1);
        LVar v  = (LVar)c.getArg(2);
        IntLVar n  = (IntLVar)c.getArg(3);
        if (!l.isBound()) {c.notSolved(); return;};  // irreducible case
        if (l.isEmpty())  {
            solver.add(n.eq(0));
            return;
        }
        if(l.isClosed() && n.isBound() && l.getSize() < n.getValue()) {
            c.fail();
            return; // never reached
        }

        int alternative = c.getAlternative();
        if(alternative == 0 && (v.isGround() && LObject.isGround(l.getOne()) && !LObject.equals(v,l.getOne())))
            alternative = 1;

        switch(alternative)  {
            case 0:
                    if(!v.equals(l.getOne()))
                        solver.addChoicePoint(c);
                    LList r1 = l.removeOne();
                    IntLVar m = new IntLVar();

                    solver.add(n.neq(0));
                    solver.add(v.eq(l.getOne()));
                    solver.add(occurrence(r1,v,m));
                    if(n.isBound())
                        m.setValue(n.getValue()-1);
                    else
                        solver.add(n.eq(m.sum(1)));
                    break;
            case 1: LList r2 = l.removeOne();
                    //LVar x = new LVar();
                    //solver.add(n.neq(0));
                    solver.add(v.neq(l.getOne()));
                    solver.add(occurrence(r2,v,n));
                    break;
        }
        return;
    }

    private void split(ConstraintClass c)
    {
        LList l1    = (LList)c.getArg(1);
        LList l2    = (LList)c.getArg(2);
        LList l3    = (LList)c.getArg(3);

        switch(c.getAlternative())
        {
        case 0: solver.addChoicePoint(c);
                solver.add(l1.eq(LList.empty()));
                solver.add(l2.eq(LList.empty()));
                solver.add(l3.eq(LList.empty()));
                break;
        case 1: {
        		solver.addChoicePoint(c);
            	LVar  x = new LVar();
                LList l1_aux = new LList();
                LList l2_aux = new LList();
                solver.add(l1.eq(l1_aux.ins(x)));
                solver.add(split(l1_aux,l2_aux,l3));
                solver.add(l2.eq(l2_aux.ins(x)));
                break;
        		}
        case 2: {
        		LVar  x = new LVar();
        		LList l1_aux = new LList();
        		LList l3_aux = new LList();
        		solver.add(l1.eq(l1_aux.ins(x)));
        		solver.add(split(l1_aux,l2,l3_aux));
        		solver.add(l3.eq(l3_aux.ins(x)));
        		break;
        		}
        }
    }
    
    private void sumIntList(ConstraintClass c)
    {
        LList l    = (LList)c.getArg(1);
        LVar sum    = (LVar)c.getArg(2); 

        if (!l.isBound() || !l.isGround()) {
            c.notSolved();
            return;
        }
        int s = 0;
        for (int i=0; i < l.getSize(); i++)
        	s = s + (Integer)((LVar)l.get(i)).getValue();
        solver.add(sum.eq(s));

    }
    
    private void sumStringList(ConstraintClass c)
    {
        LList l    = (LList)c.getArg(1);
        LVar sum    = (LVar)c.getArg(2); 

        if (!l.isBound() || !l.isGround()) {
            c.notSolved();
            return;
        }
        int s = 0;
        for (int i=0; i < l.getSize(); i++) {
        	String elem;
        	if (l.get(i) instanceof LVar)
        	     elem = (String)((LVar)l.get(i)).getValue();
        	else elem = (String)l.get(i);
        	s = s + elem.length();
        }
        solver.add(sum.eq(s));
    }

    private void sumIntListND(ConstraintClass c)
    {
        LList l    = (LList)c.getArg(1);
        IntLVar psum    = (IntLVar)c.getArg(2);  // previous sum
        IntLVar fsum    = (IntLVar)c.getArg(3);  // final sum

        switch(c.getAlternative())
        {
        case 0: solver.addChoicePoint(c);
                solver.add(l.eq(LList.empty()));
                solver.add(fsum.eq(psum));
                break;
        case 1: {
                IntLVar tsum = new IntLVar();
                IntLVar x = new IntLVar();
            	LList l_aux = new LList();
                solver.add(l.eq(l_aux.ins(x)));
                solver.add(tsum.eq(psum.sum(x)));
                solver.add(sumIntListND(l_aux,tsum,fsum));
            	}
        }
    }      
}


   
