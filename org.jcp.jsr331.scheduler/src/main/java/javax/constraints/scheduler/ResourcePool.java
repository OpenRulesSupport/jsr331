package javax.constraints.scheduler;

/**
 * This interface represents a pool of alternative resources. When an activity
 * requires ResourcePool it means one and only one resource from this pool 
 * will be actually used by the activity.
 * @author JF
 *
 */
public interface ResourcePool {
	
	public String getName(); 
	
	public Resource get(int index);
	
	public void add(int index,Resource resource);

}
