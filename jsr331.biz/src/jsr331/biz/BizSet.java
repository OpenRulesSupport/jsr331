package jsr331.biz;

/*----------------------------------------------------------------
 * Copyright Cork Constraint Computation Centre, 2006-2007
 * University College Cork, Ireland, www.4c.ucc.ie
 *---------------------------------------------------------------*/
/**
 * This class represents constrained set variables. 
 * @author jacob
 *
 */

abstract public class BizSet extends BizObject {
	
	public BizSet(BizProblem csp) {
		this(csp, "BizSet");
	}

	public BizSet(BizProblem csp, String name) {
		super(csp, name);
	}

}
