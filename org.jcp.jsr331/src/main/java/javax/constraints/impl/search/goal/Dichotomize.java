package javax.constraints.impl.search.goal;

import java.util.Calendar;

import javax.constraints.Problem;
import javax.constraints.ProblemState;
import javax.constraints.SearchStrategy;
import javax.constraints.Solution;
import javax.constraints.SearchStrategy.SearchStrategyType;
import javax.constraints.impl.search.AbstractSolver;
import javax.constraints.Var;
import javax.constraints.impl.AbstractProblem;
import javax.constraints.impl.AbstractVar;

/**
 * This class is used by the Solver's method "findOptimalSolutionDichotomize". Its method
 * "execute" is trying to find a solution that minimizes the parameter
 * "objective" using the current search strategy.
 * The "tolerance" specifies a difference between different optimal
 * solutions that can be ignored. It may be considered as a precision of the
 * search algorithm.
 *
 */

public class Dichotomize {

	SolverWithGoals solver;
	Var objective;
	//SearchStrategy searchStrategy;
	Goal searchGoal;
	int objectiveMin;
	int objectiveMax;
	int tolerance;
	int numberOfSolutions;
	int numberOfChoicePoints;
	int numberOfFailures;
	int numberOfTries;
	boolean checkLowerHalf;
	Solution solution;
	AbstractProblem p;
	int prevMax;
	int midObjective;
	int totalTimeLimit; // in seconds. 0 - means no total time limit
	long startTime;

	public Dichotomize(SolverWithGoals solver, Var objective) {
		this.solver = solver;
		p = (AbstractProblem)solver.getProblem();
		searchGoal = solver.combineSearchStrategies();
		searchGoal = searchGoal.and(new GoalSaveSolution(solver));
		this.objective = objective;
		if (objective.getName().isEmpty())
			objective.setName("Objective"); 
		if (p.getVar(objective.getName()) == null) {
			p.add(objective);
		}
		
		objectiveMin = objective.getMin();
		objectiveMax = objective.getMax();
		this.tolerance = solver.getOptimizationTolerance();
		this.totalTimeLimit = solver.getTimeLimit();
		startTime = System.currentTimeMillis();
		checkLowerHalf = false;
		numberOfTries = 0;
		solution = null;
		prevMax = 0;
		midObjective = 0;
	}

	/**
	 * The actual minimization algorithm executes a dichotomized search. During
	 * the search it modifies an interval [objectiveMin; objectiveMax]. First it
	 * is trying to find a solution in the [objectiveMin; objectiveMid]. If it
	 * fails, it is looking at [objectiveMid+1; objectiveMax]. During this
	 * process it switches the search target: one time in looks at in the upper
	 * half of the selected interval, another time - to the lower half.
	 * Successful search stops when (objectiveMax - objectiveMin) is less or equal to tolerance.
	 */
	public Solution execute() {

		p.debug("Dichotomize with objective within [" + objectiveMin + ";" + objectiveMax + "]");

		numberOfTries++;

		// dichotomized search
		solver.setTimeLimitStart(); // reset TimeLimit for one solution search
		Solution newSolution = null;
		Goal minGoal = solver.goalVarGeValue(objective, objectiveMin);
		Goal maxGoal = solver.goalVarLeValue(objective, objectiveMax);
		Goal runGoal = minGoal.and(maxGoal).and(searchGoal);
		try {
			if (solver.execute(runGoal,ProblemState.RESTORE))
				newSolution = solver.getSolution();			
		} catch (Exception e) {
			if (solver.isTimeLimitExceeded()) {
				p.log("WARNING: Time limit " + solver.getTimeLimit() + 
						" mills for one solution search has been exceeded");
			}
			else {
				solver.log("ERROR: Unexpected search interruption!");
			}
		}

		if (newSolution != null) {
			// success
			// if there is a solution in this interval:
			// 1) objectiveMax = objectiveValue - 1
			// 2) check tolerance condition, if satisfied return solution, else
			// 3) split current interval in lower and upper half
			// 4) consider lower part
			numberOfSolutions++;
			solution = newSolution;
			solution.setSolutionNumber(numberOfSolutions);
			//TODO fix this for VarReal objectives..
			int objectiveValue = solution.getValue(objective.getName());
			if (solver.isTraceSolutions())
				p.log("Found solution #" + numberOfSolutions + " objective=" + objectiveValue
						+ ". " + Calendar.getInstance().getTime());
			//solution.log();

			objectiveMax = objectiveValue - tolerance;

			if (java.lang.Math.abs(objectiveValue - objectiveMin) <= 0) {
				p.debug("This solution is optimal!");
				return solution; // THE END !!!
			}
			// Check MaxNumnerOfSolutions
			if (solver.getMaxNumberOfSolutions() > 0
				&& numberOfSolutions == solver.getMaxNumberOfSolutions()) {
				p.log("The search is interrupted: MaxNumberOfSolutions has been reached");
				return solution; // THE END !!!
			}

			midObjective = (int) Math.floor((objectiveMin + objectiveMax) / 2);
			//p.debug(objective.toString());
			//p.debug("Try objective [" + objectiveMin + ";" + midObjective + "]");
			prevMax = objectiveMax;
			objectiveMax = midObjective;
			checkLowerHalf = true;
		} else {
			// failure
			// if there is no solution in this interval:
			// 1) if we are checking upper half and solution != null, return
			// solution, else
			// 2) check upper half
			//p.debug("Failure!");
			p.debug("No solutions within [" + objectiveMin + ";" + objectiveMax +"]");
			// if (objectiveMax - objectiveMin <= tolerance+1) {
			// String text = "No solutions";
			// if (solution != null)
			// text = "Last solution was optimal!";
			// p.debug(text);
			// return solution; // previously found solution or null
			// }

			if (checkLowerHalf) {
				midObjective++;
				objectiveMax = prevMax-1;
				if(midObjective > objectiveMax)
					return solution;
				//p.debug("Try objective [" + midObjective + ";" + objectiveMax + "]");
				objectiveMin = midObjective;
				checkLowerHalf = false;
			} else {
				String text = "No solutions";
				if (solution != null)
					text = "Last solution was optimal!";
				p.debug(text);
				return solution; // previously found solution or null
			}

		}
		return execute();
	}
	
}
