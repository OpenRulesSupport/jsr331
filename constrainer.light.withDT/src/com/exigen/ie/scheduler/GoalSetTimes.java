package com.exigen.ie.scheduler;

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

import com.exigen.ie.constrainer.Failure;
import com.exigen.ie.constrainer.Goal;
import com.exigen.ie.constrainer.GoalGenerate;
import com.exigen.ie.constrainer.GoalImpl;
import com.exigen.ie.constrainer.IntExpArray;
import com.exigen.ie.constrainer.IntValueSelectorMin;
import com.exigen.ie.constrainer.IntVarSelector;
import com.exigen.ie.constrainer.IntVarSelectorMinMin;
import com.exigen.ie.tools.FastVector;

/**
 * This goal instantiates specified jobs
 *
 * @see JobVariableSelector
 */
public class GoalSetTimes extends GoalImpl
{
	FastVector _jobs;
	JobVariableSelector _sel;

	class DefaultSelector implements JobVariableSelector
	{
		public IntVarSelector getSelector(IntExpArray vars)
		{
			return new IntVarSelectorMinMin(vars);
		}
	}

	public GoalSetTimes(FastVector jobs)
	{
		this(jobs, null);
	}

	public GoalSetTimes(FastVector jobs, JobVariableSelector sel)
	{
		super(((Job) jobs.elementAt(0)).constrainer(), "GoalSetTimes");
		_jobs = jobs;
		_sel = sel != null ? sel : new DefaultSelector();
	}

	public Goal execute() throws Failure
	{
		IntExpArray vars = new IntExpArray(constrainer(), _jobs.size());
		for (int i = 0; i < _jobs.size(); ++i)
		{
			Job job = (Job) _jobs.elementAt(i);
			vars.set(job.getStartVariable(), i);
		}

		return new GoalGenerate(
			vars,
			_sel.getSelector(vars),
			new IntValueSelectorMin());
	}

}
