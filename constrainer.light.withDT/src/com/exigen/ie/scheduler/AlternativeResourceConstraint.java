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

import com.exigen.ie.constrainer.ConstraintImpl;
import com.exigen.ie.constrainer.EventOfInterest;
import com.exigen.ie.constrainer.Failure;
import com.exigen.ie.constrainer.Goal;
import com.exigen.ie.constrainer.IntExp;
import com.exigen.ie.constrainer.IntVar;
import com.exigen.ie.constrainer.Observer;
import com.exigen.ie.constrainer.Subject;

public class AlternativeResourceConstraint extends ConstraintImpl
{

	private Schedule _schedule;
	private Resource[] _rset;
	private Resource _assigned;
	private Job _job;
	private int _capacity;
	private IntVar _capacityVar;
	private boolean[] _possibleAssignment;

	private Observer _observer;
	private Goal _goal = null;

	private IntExp _flag = null;

	class AltResConstrObserver extends Observer
	{
		public void update(Subject var, EventOfInterest interest) throws Failure
		{
			IntExp event_var = (IntExp) var;
			//Debug.on(); Debug.print("AllDiffObserver("+var+") "+interest+" "+_bits); Debug.off();
			_goal.execute();
		}

		public int subscriberMask()
		{
			return EventOfInterest.VALUE;
		}
		public String toString()
		{
			return "AlternativeResourceConstraintObserver";
		}
		public Object master()
		{
			return this;
		}
	}
	// END: AltResConstrObserver

	public AlternativeResourceConstraint(
		Job j,
		AlternativeResourceSet res,
		int capacity)
	{
		this(j, res.toArray(), capacity, null);
	}

	public AlternativeResourceConstraint(
		Job j,
		AlternativeResourceSet res,
		IntVar capacity)
	{
		this(j, res.toArray(), -1, capacity);
	}

	public AlternativeResourceConstraint(
		Job j,
		Resource[] res,
		int capacity,
		IntVar capacityVar)
	{
		super(j.constrainer(), "AlternativeResourceConstraint");
		_job = j;
		_schedule = _job.schedule();
		_rset = res;
		_assigned = null;
		_capacity = capacity;
		_capacityVar = capacityVar;
		_possibleAssignment = new boolean[_rset.length];
		java.util.Arrays.fill(_possibleAssignment, true);
		_schedule.addRequirement(this);
	}

	public Goal execute() throws Failure
	{
		_goal = new GoalAssignAlternative(this, _possibleAssignment);

		_job.getStartVariable().attachObserver(
			_observer = new AltResConstrObserver());
		//        _goal.execute();
		return null;
	}

	public void setNotPossible(Resource r)
	{
		for (int i = 0; i < _rset.length; i++)
			if (_rset[i] == r)
			{
				_possibleAssignment[i] = false;
				return;
			}
	}

	public void assignResource(Resource res)
	{
		_assigned = res;
	}

	public void flush()
	{
		_assigned = null;
	}

	public Job getJob()
	{
		return _job;
	}

	public Resource getResource(int n)
	{
		return _rset[n];
	}

	public Resource getResource()
	{
		return _assigned;
	}

	public Resource[] resources()
	{
		return _rset;
	}

	public int getCapacity()
	{
		return _capacity;
	}

	public IntVar getCapacityVar()
	{
		return _capacityVar;
	}

	public String toString()
	{
		StringBuffer out = new StringBuffer();
		out.append(_job + " requires [ ");
		for (int i = 0; i < _rset.length; i++)
		{
			if (_possibleAssignment[i])
			{
				out.append(_rset[i] + " ");
			}
		}
		out.append("]");
		return out.toString();
	}
}