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
	int timeLimit; // for one solution in seconds. 0 - means no time limit
	int timeLimitGlobal; // for all solutions in seconds. 0 - means no total time limit
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
		this.timeLimit = solver.getTimeLimit();
		if (timeLimit <= 0) {
		    p.log("Use default time limit per solution: 25 seconds");
		    solver.setTimeLimit(25);
		}
		this.timeLimitGlobal = solver.getTimeLimitGlobal();
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
	 * @return a solution
	 */
	public Solution execute() {
	    long currentTime = System.currentTimeMillis();
	    // Check Total Time Limit
        if (currentTime - startTime > timeLimitGlobal) {
            p.log("The search is interrupted by Time Limit Global " + timeLimitGlobal + " milliseconds");
            return solution; // THE END !!!
        }
		p.log("Dichotomize with objective " + objective + " within [" + objectiveMin + ";" + objectiveMax + "]");
		numberOfTries++;

		// dichotomized search
		solver.setTimeLimitStart(); // reset TimeLimit for one solution search
		
		Goal minGoal = solver.goalVarGeValue(objective, objectiveMin);
		Goal maxGoal = solver.goalVarLeValue(objective, objectiveMax);
		//Goal backtrackGoal = new GoalBacktrack(solver);
		Goal runGoal = minGoal.and(maxGoal).and(searchGoal);
		
		Solution newSolution = null;
		try {
			if (solver.execute(runGoal,ProblemState.RESTORE)) {
			    // Solution found
				newSolution = solver.getSolution();
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
	            // Check MaxNumberOfSolutions
	            int maxSolutions = solver.getMaxNumberOfSolutions();
	            if (maxSolutions > 0 && numberOfSolutions == maxSolutions) {
	                p.log("The search is interrupted: MaxNumberOfSolutions " + maxSolutions + " has been reached");
	                return solution; // THE END !!!
	            }
	            
	            midObjective = (int) Math.floor((objectiveMin + objectiveMax) / 2);
	            if (midObjective == objectiveMax) { // JF 2024-04-04
	                midObjective = objectiveMin;
	            }
	            //p.debug(objective.toString());
	            //p.debug("Try objective [" + objectiveMin + ";" + midObjective + "]");
	            prevMax = objectiveMax;
	            objectiveMax = midObjective;
	            checkLowerHalf = true;
	            return execute();
			}
		} catch (Exception e) {
		    if (solver.getTimeLimit() > 0) {
                p.log("Dichotomize: Time limit " + solver.getTimeLimit() + " mills for one solution search has been exceeded");
            }
		}
		
		// newSolution == null  - No solution found
        if (checkLowerHalf) {
            midObjective++;
            objectiveMax = prevMax-1;
            if(midObjective > objectiveMax)
                return solution;
            objectiveMin = midObjective;
            checkLowerHalf = false;
            //p.log("Try to find a solution within [" + midObjective + ";" + objectiveMax + "]");
            return execute();
        } else {
            String text = "No solutions";
            if (solution != null)
                text = "Last solution was optimal!";
            p.debug(text);
            return solution; // previously found solution or null
        }
	}
	
}
