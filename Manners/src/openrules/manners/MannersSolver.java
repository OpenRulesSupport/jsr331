package openrules.manners;

import javax.constraints.Constraint;
import javax.constraints.Problem;
import javax.constraints.ProblemFactory;
import javax.constraints.Solution;
import javax.constraints.Var;
import javax.constraints.VarSet;

import com.openrules.ruleengine.OpenRulesEngine;


//import cp.choco.Problem;

/*
 * “Miss Manners” is a notorious benchmark for rule engines.
 * The problem is to find an acceptable seating arrangement
 * for allGuests at a dinner party.  It should match people with
 * the same hobbies, and to seat everyone next to a member of
 * the opposite sex.
 *
 */
abstract public class MannersSolver {

	Guest[] guests;
	int N;
	Problem problem;
	Var[] guestVars;
	Var[] genderVars;
	VarSet[] hobbyVars;
	Var[] seatVars; // for symmetry avoiding constraints
	OpenRulesEngine engine;
	int violationMin;

	public MannersSolver(String name, String size, boolean badData) {
		engine = new OpenRulesEngine("file:rules/Manners.xls");
		problem = ProblemFactory.newProblem("Manners");
		log(name + " Size=" + size + " Data=" + (badData ? "bad" : "good"));
		guests = getGuests(size, badData);
		N = guests.length;
		violationMin = -1;
	}

	public int violationMin() {
		if (violationMin < 0) {
			int males = 0;
			int females = 0;
			for (int i = 0; i < guests.length; i++) {
				if (guests[i].getGender().equalsIgnoreCase("m"))
					males++;
				else
					females++;
			}
			violationMin = Math.abs(males - females);
			log("Minimal constraint violation = " + violationMin);
		}
		return violationMin;
	}

	public void log(String text) {
		problem.log(text);
	}

	abstract public boolean define();

	public Guest[] getGuests(String size, boolean badData) {
		String bad = ""; // good data
		if (badData)
			bad = "bad";// bad data
		return (Guest[]) engine.run("getGuests" + size + bad);
	}

	public void postSimilarGuestConstraints() {
		// Define seatVars
		seatVars = problem.variableArray("Seats", 0, N - 1, N);
		for (int seat = 0; seat < N; seat++) {
			for (int guest = 0; guest < N; guest++) {
				Constraint c1 = problem.linear(guestVars[seat], "=", guest);
				Constraint c2 = problem.linear(seatVars[guest], "=", seat);
				problem.postIfThen(c1, c2);
				// problem.log("SeatVar["+guest+"]: "+seatVars[guest]);
			}
		}
		// Build groups of similar guests
		GuestGroups guestGroups = new GuestGroups(guests);
		GuestGroup group = guestGroups.next();
		int g = 1;
		while (group != null && group.getNumberOfGuests() > 1) {
			problem.log(g + "-" + group.toString());
			// order guestVars inside groups to avoid symmetry
			for (int j = 1; j < group.getNumberOfGuests(); j++) {
				int prev = group.getGuests()[j - 1];
				int current = group.getGuests()[j];
				// problem.log("Post constraint: " + guests[prev].getName()
				// + " <= " + guests[current].getName());
				// problem.log("Member " + current + ": " + seatVars[current]);
				// problem.log("Member " + prev + ": " + seatVars[prev]);
				problem.post(seatVars[prev],"<",seatVars[current]);
			}
			group = guestGroups.next();
			g++;
		}
	}

	abstract public Solution solve(int milliseconds);

//	public boolean solveAll() {
//		int MaxNumberOfSolutions = 3;
//		problem.profileOn();
//		Goal goal1 = problem.goalGenerate(guestVars);
//		Goal goal2 = problem.goalGenerate(hobbyVars);
//		Goal goal = goal1.and(goal2);
//		// all solutions
//		problem.setMaxNumberOfSolutions(MaxNumberOfSolutions);
//		int numberOfSolutions = problem.solveAll(goal);
//		problem.log("Found " + numberOfSolutions + " solutions");
//		for (int i = 0; i < numberOfSolutions; i++) {
//			Solution solution = problem.getSolutions()[i];
//			print(solution);
//			problem.profileLog();
//		}
//		return numberOfSolutions > 0;
//	}

	abstract public void print(Solution solution);

}
