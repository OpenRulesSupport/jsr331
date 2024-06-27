package openrules.manners;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.constraints.ProblemFactory;
import javax.constraints.SearchStrategy;
import javax.constraints.Solution;
import javax.constraints.SolutionIterator;
import javax.constraints.Solver;
import javax.constraints.Var;
import javax.constraints.Problem;

import com.openrules.ruleengine.OpenRulesEngine;

public class NoSetMannersSolver {

	Guest[] guests;
	int N;
	Problem csp;
//	Var[] rightOfGuestVars;
	Var[] seatVars;


	public NoSetMannersSolver(Guest[] guests) {
		this.guests = guests;
		N = guests.length;
	}

	public void define() {
		csp = ProblemFactory.newProblem("Manners");
		try {
			// Prepare an array of N genders and an array of N hobby sets
			int[]guestGenders = new int[N];
			Set<Integer>[] hobbySets = new Set[N];
			for (int i = 0; i < N; i++) {
				Guest g = guests[i];
				guestGenders[i] = g.getGender().equals("m")? 0 : 1;
				hobbySets[i] = new HashSet<Integer>();
				for (int j = 0; j < g.getHobbies().length; j++) {
					hobbySets[i].add(new Integer(g.getHobbies()[j]));
				}
			}
			// Define problem variables for guest's seats
	//		rightOfGuestVars = new Var[N]; //var array storing who sits to right of guest <index>
			seatVars = new Var[N]; //var storing the seat of guest <index>
			for (int i = 0; i < N; i++) {
	//			rightOfGuestVars[i] = csp.addVar("right of guest-" + allGuests[i].getName(), 0, N-1);
				seatVars[i] = csp.variable("guest-" + guests[i].getName(), 0, N-1);
				//post hobby and gender constraints
				for(int j = 0; j < N; j++ )
				{
					if( guestGenders[i] == guestGenders[j])
					{
	//					rightOfGuestVars[i].ne(j).post();
					}
					else
					{
						Iterator iter = hobbySets[i].iterator();
						boolean matchingHobby = false;
						while( iter.hasNext() )
						{
							if( hobbySets[j].contains(iter.next()) )
							{
								matchingHobby = true;
								break;
							}
						}
						if(!matchingHobby)
						{
	//						rightOfGuestVars[i].ne(j).post();
							csp.post(seatVars[i].plus(1).mod(N),"!=",seatVars[j]);
						}
					}
				}
			}

			for (int i = 0; i < N; i++) {
				//post hobby and gender constraints
				for(int j = i+1; j < N; j++ )
				{
					if( guestGenders[i] == guestGenders[j])
					{
						csp.post(seatVars[i].plus(1).mod(N),"!=",seatVars[j]);
					}
				}
			}

			// Post constraint "All allGuests occupy different seats"
	//		csp.allDiff(rightOfGuestVars).post();
			csp.postAllDiff(seatVars);


			// channelling constraint: if [j] is to the right of [i], then seat of [i] + 1 = seat of [j]
/*			for (int i = 0; i < N; i++) {
				//post hobby and gender constraints
				for(int j = 0; j < N; j++ )
				{
					if( i != j )
					{
						csp.ifThen( rightOfGuestVars[i].eq(j), seatVars[i].add(1).mod(N).eq(seatVars[j]) ).post();
					}
				}
			} */
		}
		catch (Exception e) {
			csp.log("Problem is over-constrained");
		}
	}

	public boolean solve() {
		Solver solver = csp.getSolver();
		solver.logStats();
		// Find One Solution
		SearchStrategy strategy1 = solver.getSearchStrategy();
		strategy1.setVars(seatVars);
		Solution solution = solver.findSolution();
		if (solution == null) {
			csp.log("No Solutions");
			return false;
		}
		else {
			//Since we have Vars indexed by guest, but want a list of solution values listed by seats
			//we input them into a Map and then print in order of the seats.
			HashMap map = new HashMap();
			for (int i = 0; i < N; i++) {
				int seat = seatVars[i].getValue();
				map.put(new Integer(seat), new Integer(i));
			}
			for(int s = 0; s < N; s++)
			{
				Guest guest = guests[((Integer)map.get(new Integer(s))).intValue()];
				System.out.println("Seat: " + (s+1) + ": " + guest);
			}
			solver.logStats();
			return true;
		}
	}

	public boolean solveAll() {
		int MaxNumberOfSolutions = 3;
		Solver solver = csp.getSolver();
		solver.logStats();
		SearchStrategy strategy1 = solver.getSearchStrategy();
		strategy1.setVars(seatVars);
		// all solutions
		solver.setMaxNumberOfSolutions(MaxNumberOfSolutions);
		SolutionIterator iter = solver.solutionIterator();
		int numberOfSolutions = 0;
		while (iter.hasNext()) {
			Solution solution = iter.next();
			numberOfSolutions++;
			if (numberOfSolutions > MaxNumberOfSolutions)
				break;
			solution.log();
			for (int j = 0; j < N; j++) {
				int value = solution.getValue("guest-"+(j+1));
				Guest guest = guests[value];
				guest.setSeat(j+1);
				System.out.println("Seat " + (j+1) + ": " + guest);
			}
		}
		return numberOfSolutions > 0;
	}

	public static void main(String[] args)
	{
		OpenRulesEngine engine = new OpenRulesEngine("file:rules/Manners.xls");
		String size = "64";//"128";
		if (args.length > 0)
			size = args[0];
		Guest[] guests = (Guest[]) engine.run("getGuests"+size);
		long start = System.currentTimeMillis();
		NoSetMannersSolver solver = new NoSetMannersSolver(guests);
		solver.define();
		solver.solveAll();
		System.out.println("Total elapsed time: " + (System.currentTimeMillis() - start) + " ms");
	}

}
