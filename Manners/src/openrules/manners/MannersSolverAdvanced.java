package openrules.manners;

import javax.constraints.Solution;

/*
 * “Miss Manners” is a notorious benchmark for rule engines.
 * The problem is to find an acceptable seating arrangement
 * for allGuests at a dinner party.  It should match people with
 * the same hobbies, and to seat everyone next to a member of
 * the opposite sex.
 *
 */
public class MannersSolverAdvanced {

	public static void main(String[] args)
	{
		long start = System.currentTimeMillis();
		String size = "32";
		if (args.length > 0)
			size = args[0];
		boolean badData = true;
		boolean found = false;
		// First try hard constraints
		MannersSolverHard hardSolver = new MannersSolverHard(size,badData);
		if (hardSolver.define()) {
			int millsPerSolution = 120000;
			Solution solution = hardSolver.solve(millsPerSolution);
			if (solution != null) {
				hardSolver.print(solution);
				found = true;
			}
		}
		// If hard fails, try soft constraints minimizing their violations
		if (!found) {
			System.out.println("MannersSolverHard failed. Try MannersSolverSoft");
			MannersSolverSoft softSolver = new MannersSolverSoft(size,badData);
			softSolver.totalSeconds = 480; // seconds
			if (softSolver.define()) {
				int millsPerSolution = 600000;
				Solution solution = softSolver.solve(millsPerSolution);
				if (solution != null) {
					softSolver.print(solution);
					found = true;
				}
			}
		}
		System.out.println("Total elapsed time: " + (System.currentTimeMillis() - start) + " ms");
	}

}
