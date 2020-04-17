package javax.constraints.scheduler.impl;

import javax.constraints.scheduler.ResourceDisjunctive;
import javax.constraints.scheduler.ResourceDisjunctivePool;

public class BasicResourceDisjunctivePool extends BasicResourcePool implements ResourceDisjunctivePool {
	
	public BasicResourceDisjunctivePool(String name) {
		super(name);
	}

	public ResourceDisjunctive get(int index) {
		return (ResourceDisjunctive)get(index);
	}
	
	public void add(int index,ResourceDisjunctive resource) {
		add(index,resource);
	}

}
