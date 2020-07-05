package jsetl.lib;

import jsetl.*;
import jsetl.exception.NotDefConstraintException;

/* 
 * User-defined constraints implementing operations
 * on directed graphs of elements of any type,
 * possibly exploiting nondeterminism.
 * 
 */

public class LGraphOps extends NewConstraints {
	public LGraphOps(SolverClass solver) {
		super(solver);
	}
	
	public ConstraintClass path(LSet G, LVar s, LVar d, LList p) {
			return new ConstraintClass("path", G, s, d, p);
	}
	
	protected void user_code(ConstraintClass constraint)
	throws NotDefConstraintException {
		if(constraint.getName() == "path") path(constraint);
		else throw new NotDefConstraintException();
	}
	
	private void path(ConstraintClass c) {
		LSet G  = (LSet)c.getArg(1);
		LVar s  = (LVar)c.getArg(2);
		LVar d 	= (LVar)c.getArg(3);
		LList p = (LList)c.getArg(4);
		switch(c.getAlternative())
		{
		case 0: solver.addChoicePoint(c);
				LList sdArc = LList.empty().ins(d).ins(s);
				solver.add(G.contains(sdArc));
				solver.add(p.eq(LList.empty().ins(sdArc)));
				break;
		case 1: LVar  t = new LVar();
				LList stArc = LList.empty().ins(t).ins(s);
				solver.add(s.neq(t));  // NEW
				LList tdPath = new LList();
				solver.add(G.contains(stArc));
				solver.add(path(G,t,d,tdPath));
				solver.add(p.eq(tdPath.ins(stArc)));
		}
		return;
	}

}
