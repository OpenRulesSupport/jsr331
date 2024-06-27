package openrules.manners;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.constraints.SearchStrategy;
import javax.constraints.Solution;
import javax.constraints.Solver;
import javax.constraints.Var;
import javax.constraints.VarSet;

public class MannersSolverHard extends MannersSolver {

	public static void main(String[] args) {
		long start = System.currentTimeMillis();
		String size = "16";
		if (args.length > 0)
			size = args[0];
		boolean badData = false;
		MannersSolverHard solver = new MannersSolverHard(size, badData);
		if (solver.define()) {
			int millsPerSolution = 60000; // 1 min
			Solution solution = solver.solve(millsPerSolution);
			if (solution != null)
				solver.print(solution);
		}
		// solver.solve();
		System.out.println("Total elapsed time: "
				+ (System.currentTimeMillis() - start) + " ms");
	}

	public MannersSolverHard(String size, boolean badData) {
		super("=== MannersSolverHard: ", size, badData);
	}

	@Override
	public boolean define() {
		try {
			// Prepare an array of N genders and an array of N hobby sets
			int[]guestGenders = new int[N];
			Set<Integer> hobbySet = new HashSet<Integer>();
			for (int i = 0; i < N; i++) {
				Guest g = guests[i];
				guestGenders[i] = g.getGender().equals("m")? 0 : 1;
				//hobbySets[i] = new HashSet<Integer>();
				for (int j = 0; j < g.getHobbies().length; j++) {
					//hobbySets[i].add(new Integer(g.getHobbies()[j]));
					hobbySet.add(new Integer(g.getHobbies()[j]));
				}
			}
			Set<Integer>[] hobbySets = new Set[N];
			for (int i = 0; i < N; i++) {
				hobbySets[i] = new HashSet<Integer>();
				Iterator<Integer> iter = hobbySet.iterator();
				while (iter.hasNext()) {
					hobbySets[i].add(iter.next());
				}
			}
			// Define problem variables for guest's seats
			guestVars = new Var[N];
			genderVars = new Var[N];
			hobbyVars = new VarSet[N];
			for (int i = 0; i < N; i++) {
				guestVars[i] = problem.variable("guest-" + guests[i].getName(), 0, N - 1);
				genderVars[i] = problem.variable("gender-" + (i+1), 0, 1);
				problem.postElement(guestGenders,guestVars[i],"=",genderVars[i]);
				hobbyVars[i] = problem.variableSet("hobbies-" + (i+1),  hobbySet);
				problem.postElement(hobbySets,guestVars[i],"=",hobbyVars[i]);
			}

			// Define seat variables: guestVar[i] seats at the seatVar[i]
			postSimilarGuestConstraints();

			// Start with the first guest to remove symmetry (a round table!)
			problem.post(guestVars[0], "=", 0);

			// Post constraint "All allGuests occupy different seats"
			problem.postAllDiff(guestVars);

			// Post Gender and Hobby Constraints
			for (int i = 0; i < N; i++) {
				Var currentGender = genderVars[i];
				Var nextGender;
				VarSet currentHobbySet = hobbyVars[i];
				VarSet nextHobbySet;
				if (i == N - 1) {
					nextGender = genderVars[0];
					nextHobbySet = hobbyVars[0];
				} else {
					nextGender = genderVars[i + 1];
					nextHobbySet = hobbyVars[i + 1];
				}
				// post constraints: Seat everyone next to a member of the
				// opposite sex
				problem.post(currentGender, "!=", nextGender);
				// post constraints: Match people with the common hobbies
				currentHobbySet.intersection(nextHobbySet).setEmpty(false);
				// symmetry avoiding constraints
			}
			return true;
		} catch (Exception e) {
			problem.log("Problem is over-constrained");
			return false;
		}

	}

	@Override
	public Solution solve(int milliseconds) {
		if (violationMin() > 0) {
			log("There are at least " + violationMin()
					+ " constraint violations");
			return null;
		}
		Solver solver = problem.getSolver();
		solver.logStats();
		solver.setTimeLimit(milliseconds); // mills
		// Find One Solution
		SearchStrategy strategy1 = solver.getSearchStrategy();
		strategy1.setVars(guestVars);
		SearchStrategy strategy2 = solver.newSearchStrategy();
		strategy2.setVars(hobbyVars);
		solver.addSearchStrategy(strategy2);

		// One solution
		Solution solution = solver.findSolution();
		if (solution == null) {
			problem.log("No Solutions");
		}
		else {
			//solution.log();
			for (int i = 0; i < N; i++) {
				Guest guest = guests[guestVars[i].getValue()];
				guest.setSeat(i+1);
				System.out.println("Seat " + (i+1) + ": " + guest);
			}
		}
		solver.logStats();
		return solution;
	}

	@Override
	public void print(Solution solution) {
		// solution.log();
		for (int seat = 0; seat < N; seat++) {
			int guestIndex = guestVars[seat].getValue();
			Guest guest = guests[guestIndex];
			guest.setSeat(seat + 1);
			problem.log("Seat " + (seat + 1) + ": " + guest); // +
															// " seatVar="+seatVars[guestIndex].getValue());
		}
	}

	// @Override
	// public boolean solveAll() {
	// if (violationMin() > 0) {
	// log("There are at least " + violationMin() + " constraint violations");
	// return false;
	// }
	// int MaxNumberOfSolutions = 3;
	// csp.profileOn();
	// Goal goal1 = csp.goalGenerate(guestVars);
	// Goal goal2 = csp.goalGenerate(hobbyVars);
	// Goal goal = goal1.and(goal2);
	// // all solutions
	// csp.setMaxNumberOfSolutions(MaxNumberOfSolutions);
	// int numberOfSolutions = csp.solveAll(goal);
	// csp.log("Found " + numberOfSolutions + " solutions");
	// for(int i=0; i < numberOfSolutions; i++) {
	// Solution solution = csp.getSolutions()[i];
	// //solution.log();
	// System.out.println("SOLUTION "+(i+1));
	// for (int j = 0; j < N; j++) {
	// int value = solution.getValue("guest-"+(j+1));
	// Guest guest = guests[value];
	// guest.setSeat(j+1);
	// System.out.println("Seat " + (j+1) + ": " + guest);
	// }
	// csp.profileLog();
	// }
	// return numberOfSolutions > 0;
	// }

}
