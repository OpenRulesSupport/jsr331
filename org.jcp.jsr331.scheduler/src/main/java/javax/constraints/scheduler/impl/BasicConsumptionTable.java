//=============================================
// J A V A  C O M M U N I T Y  P R O C E S S
// 
// J S R  3 3 1
// 
// Specification
// 
//============================================= 
package javax.constraints.scheduler.impl;

import java.util.ArrayList;

import javax.constraints.Var;
import javax.constraints.scheduler.Resource;
import javax.constraints.scheduler.Schedule;
import javax.constraints.scheduler.ConsumptionTable;

/**
 * This is a basic implementation of consumption tables used by resources.
 * A consumption table consists of columns of lists with constrained integer variables
 * representing consumption of this resource by various activities.
 * 
 */

public class BasicConsumptionTable implements ConsumptionTable {
    
    Resource resource;
    int from;
    int to;
    VectorVar[] table;
    
    public BasicConsumptionTable(Resource resource, int from, int to) {
        this.resource = resource;
        this.from = from;
        this.to = to;
        int size = to - from +1;
        table = new VectorVar[size];
        for(int time = from; time < to; time++) {
            table[time] = new VectorVar();
        }
    }

	/**
	 * 
	 * @return a related resource
	 */
	public Resource getResource() {
	    return null;
	}
	
	/**
	 * Adds a var to the list of variables for the column "time" 
	 * @param time
	 * @param var
	 */
	public void addVar(int time, Var var) {
	    table[time].addUnique(var);
	    if (time < to-1) {
	        for(int t = time+1; t < to-1; t++ ) {
	            table[t].addUnique(var);
	        }
	    }
	}
	
	public VectorVar getColumn(int time) {
	    return table[time];
	}
	
	/**
     * Posts constraints for the current state of the timetable:
     * for each time 
     *    column sum of column's variables <= resource capacity at "time"
     */
    public void postConstraints() {
        Schedule s = resource.getSchedule();
        String resourceName = resource.getName().trim();
        for(int time = from; time < to; time++) {
            VectorVar column = getColumn(time);
            if (column.size() > 0) {
                Var columnCapacity = s.sum(column.toArray());
                s.add("TotalConsumptionOf" + resourceName + "AtTime" + time, columnCapacity);
                s.log("Post " + columnCapacity + " <= " + resource.getCapacityVar(time));
                s.post(columnCapacity,"<=",resource.getCapacityVar(time));
            }
        }
    }
}
