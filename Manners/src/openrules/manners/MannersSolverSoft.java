package openrules.manners;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.constraints.SearchStrategy;
import javax.constraints.Solution;
import javax.constraints.Solver;
import javax.constraints.Var;
import javax.constraints.VarSet;

public class MannersSolverSoft extends MannersSolver {

	Var[] violations;
	Var totalViolation;
	int totalSeconds = 0; // 0 - unlimited

	public static void main(String[] args) {
		long start = System.currentTimeMillis();
		String size = "32";
		if (args.length > 0)
			size = args[0];
		boolean badData = true;
		MannersSolverSoft solver = new MannersSolverSoft(size,badData);
		solver.totalSeconds = 300;
		if (solver.define()) {
			int millsPerSolution = 90000;
			Solution solution = solver.solve(millsPerSolution);
			if (solution != null)
				solver.print(solution);
		}
		System.out.println("Total elapsed time: " + (System.currentTimeMillis() - start) + " ms");
	}

	public MannersSolverSoft(String size, boolean badData) {
		super("=== MannersSolverSoft: ",size,badData);
	}

	@Override
	public boolean define() {
		try {
			// Prepare an array of N genders and an array of N hobby sets
			//int[] seats = new int[N];
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
			// seat i is occupied by guestVar[i]
			// guestVar[i] seats at the seat i
			// if guest1 = guest2 then seatVars[guest1] < seatVars[guest2]
			postSimilarGuestConstraints();

			// Start with the first guest to remove symmetry (a round table!)
			problem.post(guestVars[0], "=", 0);

			// Post constraint "All allGuests occupy different seats"
			problem.postAllDiff(guestVars);

			// Post Gender and Hobby Constraints
			violations = new Var[N];
			for (int i = 0; i < N; i++) {
				Var currentGender = genderVars[i];
				Var nextGender;
				VarSet currentHobbySet = hobbyVars[i];
				VarSet nextHobbySet;
				if (i == N - 1) {
					nextGender = genderVars[0];
					nextHobbySet = hobbyVars[0];
				}
				else {
					nextGender = genderVars[i+1];
					nextHobbySet = hobbyVars[i+1];
				}
				// post constraints: Seat everyone next to a member of the opposite sex
				violations[i] = problem.linear(currentGender,"=",nextGender).asBool();
				// post constraints: Match people with the common hobbies
				currentHobbySet.intersection(nextHobbySet).setEmpty(false);
			}
			//totalViolation = problem.sum(violations);
			// Do not allow violations[0]
			problem.post(violations[0],"=",0);
			Var[] violationCosts = new Var[N];
			for (int i = 0; i < N; i++) {
				if (i==0)
					violationCosts[i] = violations[i];
				else {
					Var newCost = violationCosts[i-1].plus(1);
					violationCosts[i] = violations[i].multiply(newCost);
				}
			}
			totalViolation = problem.sum(violationCosts);
			problem.post(totalViolation,">=",violationMin());
			return true;
		}
		catch (Exception e) {
			problem.log("Problem is over-constrained");
			return false;
		}

	}
	
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
		Solution solution = //solver.findSolution();
			solver.findOptimalSolution(totalViolation); //,tolerance,totalSeconds);
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
		//solution.log();
		problem.log("SOLUTION "+ solution.getSolutionNumber() +
				" Total Violation=" + totalViolation);
		for (int j = 0; j < N; j++) {
			int value = solution.getValue("guest-"+(j+1));
			Guest guest = guests[value];
			guest.setSeat(j+1);
			problem.log("Seat " + (j+1) + ": " + guest);
		}

	}
}
