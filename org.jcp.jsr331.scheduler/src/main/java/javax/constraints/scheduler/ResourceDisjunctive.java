//=============================================
// J A V A  C O M M U N I T Y  P R O C E S S
// 
// J S R  3 3 1
// 
// Specification
// 
//============================================= 
package javax.constraints.scheduler;

/**
 * This is an interface for discrete resources of capacity 1 (one) 
 * introduced for reasons of expressiveness and efficiency. 
 * 
 */

public interface ResourceDisjunctive extends Resource {
	
	public void setUnavailable(int time) throws Exception;

	public void setUnavailable(int time1, int time2) throws Exception;
}
