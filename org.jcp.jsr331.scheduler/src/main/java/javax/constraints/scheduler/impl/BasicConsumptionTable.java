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
	    Schedule s = resource.getSchedule();
//	    System.out.println();
//	    if (time == 1)
//	        System.out.println("*** time == 1");
//	    
//	    for(int j=0; j<time;j++) {
//            System.out.print("-");
//        }
//	    System.out.print("[" + time +"] " + var);
	    table[time].add(var);
	    
	    if (time < to) {
	        for(int t = time+1; t < to; t++ ) {
//	            System.out.println();
//	            for(int j=0; j<t;j++) {
//	                System.out.print("-");
//	            }
//	            System.out.print("[" + t +"] " + var);
	            table[t].add(var);
	        }
	    }
	}
	
	/**
     * Adds consumption variables starting from the column "from" 
     * to each column "time" between from and from+duration-1 using consumption
     * equal to var.multiply(time+1) 
     * @param from int
     * @param var Var
     * @param duration int
     */
    public void consume(int from, Var var, int duration) {
        Schedule s = resource.getSchedule();
        for(int time=0; time < duration; time++) {
            int column = from + time;
            Var consumption = var.multiply(time+1);
            s.add("["+column+"]"+var.getName(), consumption);
//            System.out.println();
//            for(int j=0; j<column;j++) {
//                System.out.print("-");
//            }
//            System.out.print(consumption);
            table[column].add(consumption);
        }
               
        if (from + duration < to) {
            int column = from+duration-1;
            String name = "[" + column + "]" + var.getName();
            Var maxConsumption = s.getVar(name);
            //Var maxConsumption = var.multiply(duration);)
            for(int t = from + duration; t < to; t++ ) {
//                System.out.println();
//                for(int j=0; j<t;j++) {
//                    System.out.print("-");
//                }
//                System.out.print("["+t+"]"+maxConsumption);
                table[t].add(maxConsumption);
            }
        }
        
    }
	
	/**
     * Adds a var to the list of variables for the column "time" only if it has not been already added
     * @param time
     * @param var
     */
    public void addVarUnique(int time, Var var) {
        Schedule s = resource.getSchedule();
        if (table[time].addUnique(var))
            s.log("Added "+var+" [" + time +"]");
        if (time < to) {
            for(int t = time+1; t < to; t++ ) {
                if (table[t].addUnique(var))
                    s.log("Added "+var+" [" + t + "]");
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
//        s.log("\n=== postConsumptionConstraints");
        for(int time = from; time < to; time++) {
            VectorVar column = getColumn(time);
            if (column.size() > 0) {
                Var columnCapacity = s.sum(column.toArray());
                s.add("TotalConsumption[" + time + "]$", columnCapacity);
//                s.log("Post " + columnCapacity + " <= " + resource.getCapacityMax(time));
                s.post(columnCapacity,"<=",resource.getCapacityMax(time));
            }
        }
    }
}
