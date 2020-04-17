package javax.constraints.scheduler.impl;

import java.util.ArrayList;

import javax.constraints.scheduler.Resource;
import javax.constraints.scheduler.ResourcePool;

public class BasicResourcePool extends ArrayList<Resource> implements ResourcePool {
	
	String name;
	
	public BasicResourcePool(String name) {
		super();
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public Resource get(int index) {
		return (Resource)get(index);
	}
	
	public void add(int index,Resource resource) {
		add(index,resource);
	}

}
