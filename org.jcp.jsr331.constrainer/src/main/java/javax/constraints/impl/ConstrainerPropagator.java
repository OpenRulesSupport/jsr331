//================================================================
// J A V A  C O M M U N I T Y  P R O C E S S
// 
// J S R  3 3 1 
// 
// CONSTRAINER-BASED REFERENCE IMPLEMENTATION
//
// Copyright (c) Cork Constraint Computation Centre, 2010
// University College Cork, Cork, Ireland, www.4c.ucc.ie
// Constrainer is copyrighted by Exigen Group, USA.
// 
//================================================================
package javax.constraints.impl;


import javax.constraints.extra.PropagationEvent;
import javax.constraints.extra.Propagator;

import com.exigen.ie.constrainer.EventOfInterest;
import com.exigen.ie.constrainer.Failure;
import com.exigen.ie.constrainer.Observer;
import com.exigen.ie.constrainer.Subject;


public class ConstrainerPropagator extends Observer {
	Propagator propagator;
	javax.constraints.Constraint cpConstraint;
	PropagationEvent event;

	public ConstrainerPropagator(Propagator propagator, PropagationEvent event) {
		super();
		this.propagator = propagator;
		this.event = event;
	}
	
	public void update(Subject exp, EventOfInterest interest) throws Failure{
//		PropagationEvent event = PropagationEvent.ANY;
//		if(interest.isValueEvent())
//			event = PropagationEvent.VALUE;
//		else
//			if(interest.isMinEvent())
//				event = PropagationEvent.MIN;
//			else
//				if(interest.isMaxEvent())
//					event = PropagationEvent.MAX;
//				else
//					if(interest.isRemoveEvent())
//						event = PropagationEvent.REMOVE;
//		//Range and Any events may not be recognized inside PropagatorI
		try {
			propagator.propagate(event);
		} catch (Exception e) {
			throw new Failure();
		}
	}
	
	public int subscriberMask(){
			int eventMask = -1;
			switch (event) {
				case VALUE:
					eventMask = EventOfInterest.VALUE;
					break;
				case MIN:
					eventMask = EventOfInterest.MIN;
					break;
				case MAX:
					eventMask = EventOfInterest.MAX;
					break;
				case REMOVE:
					eventMask = EventOfInterest.REMOVE;
					break;
				case RANGE:
					eventMask = EventOfInterest.MINMAX;
					break;
				case ANY:
					eventMask = EventOfInterest.ALL;
			}
			if (eventMask == -1)
				throw new RuntimeException("ERROR: unknown event "+event);
		return eventMask;
	}
	
	public Object master(){
		return this;
	}
}
