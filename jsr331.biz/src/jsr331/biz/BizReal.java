package jsr331.biz;

/*----------------------------------------------------------------
 * Copyright Cork Constraint Computation Centre, 2006-2007
 * University College Cork, Ireland, www.4c.ucc.ie
 *---------------------------------------------------------------*/

/**
 * This class represents constrained real variables.
 * @author JF
 *
 */

abstract public class BizReal extends BizObject {
	
	public BizReal(BizProblem csp) {
		this(csp, "realvar");
	}

	public BizReal(BizProblem csp, String name) {
		super(csp, name);
	}

}
