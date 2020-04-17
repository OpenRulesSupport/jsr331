package javax.constraints.scheduler;

public interface ResourceDisjunctivePool extends ResourcePool {

	public ResourceDisjunctive get(int i);
	
	public void add(int index,ResourceDisjunctive resource);
}
