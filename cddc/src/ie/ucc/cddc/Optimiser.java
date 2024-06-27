package ie.ucc.cddc;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.Map.Entry;

import javax.constraints.Oper;
import javax.constraints.Solution;
import javax.constraints.Var;
import javax.constraints.impl.Problem;

public class Optimiser {	

	// The containers (disks, servers, ...)
	static private Set<ProblemElement> problemContainers;
	
	// The number of movables in each container
	static private Map<ProblemElement, Integer> numMovables;

	// Assign each container an internal id for easy reference
	static private Map<ProblemElement, Integer> containerIds;

	// The maximum load limit we allow for each container
	static private Map<ProblemElement, Map<String, Double> > loadLimits;

	// The items we can move around
	static private Vector<ProblemElement> problemMovables;
	
	// The CSP we're building;
	static private Problem csp = null;
	
	// Keep track of how we can access the movables
	static private String partId;	
	
	// Keep track of how many containers we have
	static private int numContainers;

	static private boolean solveIncrementally = true;
	
	public static void solveIncremental() {
		solveIncrementally = true;
	}

	public static void solveFully() {
		solveIncrementally = false;
	}

	public static boolean hasProblem() 
	{
		if ( problemContainers == null )
			return false;
		return ( problemContainers.size() > 0 ); 
	}
	
	public static void next(boolean b)
	{
		if ( b ) 
		{
			
			problemContainers = new HashSet<ProblemElement>();
			problemMovables = new Vector<ProblemElement>(); 
			numMovables = new HashMap<ProblemElement, Integer>();
			loadLimits = new HashMap<ProblemElement, Map<String, Double>>();
			containerIds = new HashMap<ProblemElement, Integer>();
			
			csp = new Problem("GenericReallocator");
			partId =  "";
			numContainers = 0;
		}
	}
	
	public static void next()
	{
		if ( solveIncrementally || csp == null)
			next(true);
	}
	
	static public void add(ProblemElement container, String part)
	{
		if ( !problemContainers.contains(container) )
		{
			problemContainers.add(container);
			partId = part;
			
			Iterator<ProblemElement> movableIter = container.iterator(part);
	        int cnt = 0;
	        while( movableIter.hasNext() ) {
	        	++cnt;
	            ProblemElement po = (ProblemElement) movableIter.next();
	            problemMovables.add( po );
	        }
	        numMovables.put(container, new Integer(cnt));
	        containerIds.put(container,	new Integer(numContainers++));
	        loadLimits.put(container, new HashMap<String, Double>());
		}
	}
	
	static public void addLimit(ProblemElement container, String sensor, double max)
	{
		if ( !loadLimits.containsKey(container) )
			throw new RuntimeException("Container not found");
		loadLimits.get(container).put(sensor, max);
	}

	static public void solve()
	{
		if ( solveIncrementally )
		{
			System.out.println("Solve");
			findSolution();
		}
	}

	static int movableId(String s)
	{
		for(int i=0; i<problemMovables.size(); ++i)
			if ( problemMovables.get(i).getId().equals(s) )
				return i;
		
		return -1;
	}
	
	static private void findSolution()
	{
		//ie.ucc.cddc.Problem.dump();

		// some counters
		int i, movable;

		Var[][] currentConfiguration = new Var[problemContainers.size()][problemMovables.size()];
		Var[][] newConfiguration = new Var[problemContainers.size()][problemMovables.size()];
	
		/* 
		* Create a binary decision variable for each container/movable combination, indicating
		* whether the movable is located in that container in the new situation.
		*
		* Also create variables for the current state and assign these either 0 or 1, depending
		* on the locations of the movables.
		*
		*/
		for( ProblemElement container: problemContainers )
		{
			int id = containerIds.get(container);
			
		    for(movable=0; movable<problemMovables.size(); ++movable) {
		        currentConfiguration[id][movable] = csp.var("C " + ((ProblemElement) problemMovables.get(movable)).getId() + "@" + container.getId(), 0, 1);
		        newConfiguration[id][movable] = csp.var("N " + ((ProblemElement) problemMovables.get(movable)).getId() + "@" + container.getId(), 0, 1);
	
		        if( container.contains(partId, (ProblemElement) problemMovables.get(movable)) )
		            currentConfiguration[id][movable].eq(1).post();
		        else
		            currentConfiguration[id][movable].eq(0).post();
		    }
		}
		
		/*
		* Set up the constraints on the variables.
		*
		* First: we have to assign as many movables to each container as are currently
		* assigned to it.
		*/
		for( ProblemElement container: problemContainers )
		{
			int id = containerIds.get(container);
		    Var [] vars = new Var[problemMovables.size()];
	
		    for(movable=0; movable<problemMovables.size(); ++movable)
		        vars[movable] = newConfiguration[id][movable];
	
		    csp.linear( vars, Oper.EQ, ((Integer)numMovables.get(container)).intValue() ).post();
		}
			
		/*
		* Assign only one container to each movable
		*/
		for(movable=0; movable<problemMovables.size(); ++movable)
		{
		    Var [] vars = new Var[problemContainers.size()];
	
		    for(int c=0; c<numContainers; ++c)
		        vars[c] = newConfiguration[c][movable];
	
		    csp.linear( vars, Oper.EQ, 1 ).post();
		}

		/*
		 * Deal with inconsistencies:
		 * 
		 * for all groups of pairwise inconsistent movables
		 *   for all of the items in that group
		 *   	find the ProblemObject that corresponds to the item
		 *         iterate over all other items in the group, and exclude the two items to be on the same disk 
		 */
		Set<String []> inconsistencies = ie.ucc.cddc.Problem.getInconsistencies(partId);
		if ( inconsistencies != null )
			for( String [] group : inconsistencies )
				for( movable=0; movable<problemMovables.size(); ++movable)
					for ( int groupMember = 0; groupMember < group.length; ++groupMember )
						if( group[groupMember].equals( problemMovables.get(movable).getId()) )
							for( int others = 0; others<group.length;++others )
								if( others != groupMember )
								{
									int thisId  = movableId(group[groupMember]);
									int otherId = movableId(group[others]);
									if ( thisId < 0 )
										throw new RuntimeException("Cannot find movable " + group[groupMember]);
									if ( otherId < 0 )
										throw new RuntimeException("Cannot find movable " + group[others]);

									for( int c=0; c<problemContainers.size(); ++c )
									{
										newConfiguration[c][thisId].add(newConfiguration[c][otherId]).le(1);
									}
								}

		
		Set<String> accumulatorIds  = new HashSet<String>();
		Set<String> accumulatorTags = new HashSet<String>();
		
		for( ProblemElement movableObject: problemMovables )
		{
			for(String accId: movableObject.getAccumulators() )
			{
				accumulatorIds.add(accId);
				
				for( String accTag: movableObject.getAccumulatorTags(accId) )
					accumulatorTags.add(accTag);
			}
		}
		
		/*
		* The load of the movables assigned to a container cannot exceed the maximum
		*/

		for( String accId: accumulatorIds )
			for( String accTag: accumulatorTags )
			{
				for( ProblemElement container: problemContainers )
				{
					int id = containerIds.get(container);

					Var[] diskLoads = new Var[problemContainers.size()];
					Var [] vars = new Var[problemMovables.size()];
				    int [] loads = new int[problemMovables.size()];
			
				    diskLoads[id] = csp.var(accId+"."+accTag+"@" + container.getId(), 0, 100);
				    for(movable=0; movable<problemMovables.size(); ++movable) {
				        vars[movable] = newConfiguration[id][movable];
				        loads[movable] = (int) ((ProblemElement) problemMovables.get(movable)).getAccumulatorValue(accId, accTag).doubleValue();
				    }
			
				    csp.linear( loads, vars, Oper.EQ, diskLoads[id] ).post();
				    diskLoads[id].le( loadLimits.get(container).get(accTag).intValue()).post();
				}
			}
	
		
		/*
		* Compute the number of similarities between the current and new situations
		*/
		Var similarities = csp.var("similarities", 0, (problemContainers.size())*(problemMovables.size()));
	
		Var [] vars = new Var[(problemContainers.size())*(problemMovables.size())];
		i=0;
	    for(int c=0; c<numContainers; ++c)
		    for(movable=0; movable<problemMovables.size(); ++movable)
		        vars[i++] = currentConfiguration[c][movable].eq(newConfiguration[c][movable]).asBool();
		csp.linear( vars, Oper.EQ, similarities).post();
	
		/*
		* Now we can solve the problem
		*/
		Solution s = ProblemElement.solve( csp.getSolver(), similarities) ;
		System.out.println("Solved!");
		s.log();
	
		Set<String> differences = new HashSet<String>();
		for( ProblemElement container: problemContainers )
		{
		    for(movable=0; movable<problemMovables.size(); ++movable) {
		        int currVal = s.getValue("C " + ((ProblemElement) problemMovables.get(movable)).getId() + "@" + container.getId());
		        int newVal = s.getValue("N " + ((ProblemElement) problemMovables.get(movable)).getId() + "@" + container.getId());
	
		        if ( currVal != newVal )
		            differences.add( ((ProblemElement) problemMovables.get(movable)).getId());
		    }
		}
		
		Iterator<String> diffList = differences.iterator();
		while( diffList.hasNext() )
		{
		    String movableId = diffList.next();
	
		    ProblemElement movablePO = ie.ucc.cddc.Problem.find(partId, movableId);
		    
		    System.out.print( movableId + " should move from ");
		    Map<String, Double> accumulatorValues = null;
			for( ProblemElement container: problemContainers )
	            if ( s.getValue("C " + movableId + "@" + container.getId()) == 1 )
	            {
	                System.out.print( container.getId() );

	                accumulatorValues = container.remove(partId, movablePO);
	                break;
	            }
	
		    System.out.print(" to ");
			for( ProblemElement container: problemContainers )
	            if ( s.getValue("N " + movableId + "@" + container.getId()) == 1 )
	            {
	                System.out.println( container.getId() );

	                container.add(partId, movablePO);
	                if ( accumulatorValues == null )
	                	break;
	                
	                for(Entry<String, Double> e: accumulatorValues.entrySet() )
	                	container.accumulate(e.getKey(), e.getValue());
	                
	                break;
	            }
			
			
		}
	
		//ie.ucc.cddc.Problem.dump();
	}
	
	public static boolean solveFull()
	{
		if (solveIncrementally)
			return false;

		System.out.println("SolveFull");

		findSolution();
		return(true);		
	}
	
	public static boolean incrementalSolutions()
	{
		return solveIncrementally;
	}
}
