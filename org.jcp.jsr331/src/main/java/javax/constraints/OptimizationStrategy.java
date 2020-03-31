//=============================================
// J A V A  C O M M U N I T Y  P R O C E S S
// 
// J S R  3 3 1
// 
// Specification
// 
//=============================================
package javax.constraints;

/**
 * These strategies are used by an optimization process implemented within the Solver's
 * method findOptimalSolution(Objective objective, Var objectiveVar, OptimizationStrategy optStrategy)
 * <br>BASIC strategy iterates through solution assuming that each next solution has optimization objectives 
 * better than in a previous solution.
 * <br>DICHOTOMIZE strategy implements a dichotomized search of an optimal solution. 
 * During the search it modifies an interval [objectiveMin; objectiveMax]. 
 * First it is trying to find a solution in the [objectiveMin; objectiveMid]. 
 * If it fails, it is looking at [objectiveMid+1; objectiveMax]. During this 
 * process it switches the search target: one time in looks at in the upper
 * half of the selected interval, another time - to the lower half. 
 * <br>NATIVE strategy implements a search strategy that is the best for a concrete implementation.
 */

public enum OptimizationStrategy {
	BASIC, 
	DICHOTOMIZE,
	NATIVE
}