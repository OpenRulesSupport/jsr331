package jsr331.biz;

/*----------------------------------------------------------------
 * Copyright Cork Constraint Computation Centre, 2006-2007
 * University College Cork, Ireland, www.4c.ucc.ie
 *---------------------------------------------------------------*/

/**
 * This class represents constrained boolean variables. 
 * @author jacob
 *
 */

abstract public class BizBool extends BizObject {
	
	public BizBool(BizProblem csp) {
		this(csp, "BizBool");
	}

	public BizBool(BizProblem csp, String name) {
		super(csp, name);
	}

}
