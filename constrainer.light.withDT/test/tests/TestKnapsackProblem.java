package tests;

import com.exigen.ie.constrainer.Constrainer;
import com.exigen.ie.constrainer.Failure;
import com.exigen.ie.constrainer.Goal;
import com.exigen.ie.constrainer.GoalFastMinimize;
import com.exigen.ie.constrainer.GoalGenerate;
import com.exigen.ie.constrainer.GoalMinimize;
import com.exigen.ie.constrainer.IntExp;
import com.exigen.ie.constrainer.IntExpArray;
import com.exigen.ie.constrainer.IntVar;

public class TestKnapsackProblem {

	public static void main(String[] args) {

		Constrainer C = new Constrainer("test GoalMinimize");
		try {
			int nbResources = 7;
			int nbItems = 12;
			// int[] capacity= {18209, 7692, 1333, 924, 26638, 61188, 13360};
			int[] capacity = { 1820, 769, 133, 924, 2663, 6118, 1336 };
			int[] value = { 96, 76, 56, 11, 86, 10, 66, 86, 83, 12, 9, 81 };
			int[][] use = { { 19, 1, 10, 1, 1, 14, 152, 11, 1, 1, 1, 1 },
					{ 0, 4, 53, 0, 0, 80, 0, 4, 5, 0, 0, 0 },
					{ 4, 660, 3, 0, 30, 0, 3, 0, 4, 90, 0, 0 },
					{ 7, 0, 18, 6, 770, 330, 7, 0, 0, 6, 0, 0 },
					{ 0, 20, 0, 4, 52, 3, 0, 0, 0, 5, 4, 0 },
					{ 0, 0, 40, 70, 4, 63, 0, 0, 60, 0, 4, 0 },
					{ 0, 32, 0, 0, 0, 5, 0, 3, 0, 660, 0, 9 } };
			Constrainer C1 = new Constrainer("knapsack problem");
			// variables
			IntExpArray take = new IntExpArray(C1, nbItems, 0, 61188, "take");
			// costFunction
			IntExp costFunc = C1.scalarProduct(take, value);
			costFunc.name("cost");
			// capacity due constraints
			for (int r = 0; r < nbResources; r++) {
				C1.postConstraint(C1.scalarProduct(take, use[r])
						.le(capacity[r]));
			}

			GoalMinimize gm = new GoalMinimize(new GoalGenerate(take), costFunc.neg(), false);
			boolean flag = false;
			flag = C1.execute(gm, false);
			System.out.println("Solution: " + take);
			System.out.println(costFunc);
		} catch (Exception f) {
			System.err.println("test failed");
		}
	} // end of testKnapsackProblem

}