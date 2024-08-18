//=============================================
// J A V A  C O M M U N I T Y  P R O C E S S
// 
// J S R  3 3 1
// 
// Specification
// 
//============================================= 
package javax.constraints.scheduler;

import javax.constraints.Var;

/**
 * This is an interface for consumption tables used by resources.
 * A consumption table consists of columns of lists with constrained integer variables
 * representing consumption of this resource by various activities.
 * 
 */

public interface ConsumptionTable {

	/**
	 * 
	 * @return a related resource
	 */
	public Resource getResource();
	
	/**
     * Adds consumption variables starting from the column "from" 
     * to each column "time" between from and from+duration-1 using consumption
     * equal to var.multiply(time+1) 
     * @param from int
     * @param var Var
     * @param duration int
     */
    public void consume(int from, Var var, int duration);
	
	/**
	 * Adds a var to the list of variables for the column "time" 
	 * @param time
	 * @param var
	 */
	public void addVar(int time, Var var);
	
	/**
	 * Posts constraints for the current state of the timetable:
	 * for each time 
	 *    column sum of column's variables <= resource capacity at "time"
	 */
	public void postConstraints();
	
	
}
