package com.exigen.ie.ccc;

///////////////////////////////////////////////////////////////////////////////
/*
 * Copyright Exigen Group 1998, 1999, 2000, 2002
 * 320 Amboy Ave., Metuchen, NJ, 08840, USA, www.exigengroup.com
 *
 * The copyright to the computer program(s) herein
 * is the property of Exigen Group, USA. All rights reserved.
 * The program(s) may be used and/or copied only with
 * the written permission of Exigen Group
 * or in accordance with the terms and conditions
 * stipulated in the agreement/contract under which
 * the program(s) have been supplied.
 */
///////////////////////////////////////////////////////////////////////////////

/**
 * Class CccConstraint.
 * Creation date: (2/10/2000 11:54:58 AM)
 * @author:
 */

import com.exigen.ie.constrainer.FloatVar;
import com.exigen.ie.constrainer.IntBoolExp;
import com.exigen.ie.constrainer.lpsolver.ConstrainerLP;

public class CccConstraint extends CccExecutable
{
	private FloatVar _violation_var;
	private double _importance;
	private ConstrainerLP simplex = null;  
	private IntBoolExp boolExp = null;

	public CccConstraint(CccCore core, String name) throws Exception
	{
		super(core, name);
		_violation_var = core.constrainer().addFloatVar(0.0, 1.0);
		_importance = 1.0;
		setType(TM_CONSTRAINT);
		status(STATUS_INACTIVE);
	}

	public FloatVar violationVar()
	{
		return _violation_var;
	}

	public void importance(double i)
	{
		_importance = i;
	}

	public double importance()
	{
		return _importance;
	}

	synchronized public boolean activate(int solution_number)
	{
		return core().activateConstraint(getId());
	}

	synchronized public void deactivate()
	{
		core().deactivateConstraint(getId());
	}

	public void fetchConstrainerState()
	{
		if (core().isActivated(getId()))
		{
			if (core().isIncompatible(getId()))
				status(STATUS_INCOMPATIBLE);
			else
				status(STATUS_ACTIVE);
		}
		else
		{
			status(STATUS_INACTIVE);
		}
	}
	public ConstrainerLP getSimplex()
	{
		return simplex;
	}

	public void setSimplex(ConstrainerLP constrainerLP)
	{
		simplex = constrainerLP;
	}

	public IntBoolExp getBoolExp()
	{
		return boolExp;
	}

	/**
	 * @param exp
	 */
	public void setBoolExp(IntBoolExp exp)
	{
		boolExp = exp;
	}

}
