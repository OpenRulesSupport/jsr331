package org.jcp.jsr331.samples;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.StringTokenizer;

import javax.constraints.*;

/**
 * Car sequencing problems arise on assembly lines in factories in the
 * automotive industry.<br>
 * <br>
 * There, an assembly line makes it possible to build many different types of
 * cars, where the types correspond to a basic model with added options. In that
 * context, one type of vehicle can be seen as a particular configuration of
 * options. Even without loss of generality, we can assume that it is possible
 * to put multiple options on the same vehicle while it is on the line. In that
 * way, virtually any configuration (taken as an isolated case) could be
 * produced on the assembly line. In contrast, for practical reasons (such as
 * the amount of time needed to do so), a given option really cannot be
 * installed on every vehicle on the line. This constraint is defined by what we
 * call the ``capacity'' of an option. The capacity of an option is usually
 * represented as a ratio partition/q where for any sequence of q cars on the line, at
 * most partition of them will have that option.<br>
 * <br>
 * The problem in car sequencing then consists of determining in which order
 * cars corresponding to each configuration should be assembled, while keeping
 * in mind that we must build a certain number of cars per configuration.<br>
 */

public class CarSequence {
	static Problem problem = ProblemFactory.newProblem("CarSequence");

	static int nbCars;
	static int nbOpt;
	static int nbClasses;

	static int[] demands;
	static int[][] options;
	static int[][] idleConfs;
	static int[][] optfreq;

	static int[][] matrix;

	/**
	 * reads out given problem from the given file
	 * @param path
	 * @param problem
	 */
	public static void readFile(String path, String problem){
		File file = new File(path);
		try {
			BufferedReader input = new BufferedReader(new FileReader(file));
			// look for the problem
			String line;
			while ((line = input.readLine()) != null) {
				if (line.contains(problem)) {
					input.readLine();
					StringTokenizer strT = new StringTokenizer(input.readLine());
					nbCars = Integer.parseInt(strT.nextToken());
					nbOpt = Integer.parseInt(strT.nextToken());
					nbClasses = Integer.parseInt(strT.nextToken());
					demands = new int[nbClasses];
					options = new int[nbOpt][];
					idleConfs = new int[nbOpt][];

					matrix = new int[nbClasses][nbOpt];

					StringTokenizer strT1 = new StringTokenizer(input
							.readLine());
					StringTokenizer strT2 = new StringTokenizer(input
							.readLine());
					optfreq = new int[strT1.countTokens()][2];
					for (int i = 0; i < optfreq.length; i++) {
						optfreq[i][0] = Integer.parseInt(strT1.nextToken());
						optfreq[i][1] = Integer.parseInt(strT2.nextToken());
					}

					int counter1 = 0;
					while (counter1 < nbClasses) {
						strT = new StringTokenizer(input.readLine());
						strT.nextToken();
						demands[counter1] = Integer.parseInt(strT.nextToken());
						int counter2 = 0;
						while (counter2 < nbOpt) {
							matrix[counter1][counter2] = Integer.parseInt(strT.nextToken());
							counter2++;
						}
						counter1++;
					}
					break;
				}
			}
			input.close();
		} catch (IOException e) {
			System.out.println("File error" + e);
		}

		for (int i = 0; i < matrix[0].length; i++) {
			int nbNulls = 0;
			int nbOnes = 0;
			for (int j = 0; j < matrix.length; j++) {
				if (matrix[j][i] == 1)
					nbOnes++;
				else
					nbNulls++;
			}
			options[i] = new int[nbOnes];
			idleConfs[i] = new int[nbNulls];
			int countOnes = 0;
			int countNulls = 0;
			for (int j = 0; j < matrix.length; j++) {
				if (matrix[j][i] == 1) {
					options[i][countOnes] = j;
					countOnes++;
				} else {
					idleConfs[i][countNulls] = j;
					countNulls++;
				}
			}
		}

	}

	static class Extractor {
		Var[] _vars = null;

		int _size;

		Extractor(Var[] array) {
			_vars = array;
			_size = array.length;
		}

		Var[] extract(int initialNumber, int amount) {
			if ((initialNumber + amount) > _size)
				amount = _size - initialNumber;
			Var[] tmp = new Var[amount];
			for (int i = initialNumber; i < initialNumber + amount; i++) {
				tmp[i - initialNumber] = _vars[i];
			}
			return tmp;
		}
	}

	static public void main(String[] args) {
		readFile("data/CarSequenceData.txt", "Problem 4/72");

//		========= Problem Representation ==================
		try {
			//problem.setDomainType(DomainType.DOMAIN_MIN_MAX);
			Var[] cars = problem.variableArray("cars",0, nbClasses - 1, nbCars);
			problem.log("nbCars: " + nbCars);

			Var[] expArray = new Var[nbClasses];

			int max = 0;
			for (int i = 0; i < cars.length; i++) {
				if (cars[i].getMax() > max)
					max = cars[i].getMax();
			}
			for (int optNum = 0; optNum < options.length; optNum++) {
				System.out.println("Options "+optNum+":");
				for (int i = 0; i < options[optNum].length; i++) {
					System.out.print(" " +options[optNum][i]);
				}
				System.out.println();
				int nbConf = options[optNum].length;
				for (int seqStart = 0; seqStart < (cars.length - optfreq[optNum][1]); seqStart++) {
					Var[] carSequence = new Extractor(cars).extract(
							seqStart, optfreq[optNum][1]);
					Var[] atMost = problem.variableArray("atmost", 0, max, options[optNum].length);
					problem.postGlobalCardinality(carSequence, options[optNum], atMost);
					// configurations that include given option may be chosen
					// optfreq[optNum][0] times AT MOST
					for (int i = 0; i < nbConf; i++) {
						problem.post(atMost[i],"<=",optfreq[optNum][0]);
					}

					Var[] atLeast = problem.variableArray("atleast", 0, max, idleConfs[optNum].length);
					problem.postGlobalCardinality(carSequence, idleConfs[optNum], atLeast);
					// all others configurations may be chosen
					// optfreq[optNum][1] - optfreq[optNum][0] times AT LEAST
					Var sum = problem.sum(atLeast);
					problem.post(sum, ">=", optfreq[optNum][1] - optfreq[optNum][0]);
				}
			}

			int[] values = new int[expArray.length];
			for (int i = 0; i < expArray.length; i++) {
				expArray[i] = problem.variable("var", 0, demands[i]);
				values[i] = i;
			}
			problem.postGlobalCardinality(cars,values,expArray);

//			========= Problem Resolution ==================
			problem.log("Solving...");
			Solver solver = problem.getSolver();
			SearchStrategy strategy = solver.getSearchStrategy();
			strategy.setVars(cars);
			strategy.setVarSelectorType(VarSelectorType.MIN_DOMAIN);
			Solution solution = solver.findSolution();
			if (solution == null)
				System.out.print("There are no solutions");
			else {
				solution.log();
			}
			solver.logStats();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}