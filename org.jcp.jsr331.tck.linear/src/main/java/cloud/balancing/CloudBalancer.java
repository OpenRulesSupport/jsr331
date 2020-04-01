package cloud.balancing;

/*
 * The problem description can be found at http://java.dzone.com/articles/resource-optimization-quick
 */

import java.util.Calendar;

import javax.constraints.Objective;
import javax.constraints.OptimizationStrategy;
import javax.constraints.Problem;
import javax.constraints.ProblemFactory;
import javax.constraints.Solution;
import javax.constraints.Solver;
import javax.constraints.Var;

public class CloudBalancer {

	static public void balance(CloudComputer[] computers, CloudProcess[] processes) {

		long start = System.currentTimeMillis();
		
		Problem p = ProblemFactory.newProblem("CloudBalancing");

		p.log("Create "+computers.length + " constrained computers");
		int[] requiredMemories = new int[processes.length];
		int[] requiredCpuPowers = new int[processes.length];
		int[] requiredNetworkBandwidths = new int[processes.length];
		for (int i = 0; i < processes.length; i++) {
			requiredMemories[i] = processes[i].getRequiredMemory();
			requiredCpuPowers[i] = processes[i].getRequiredCpuPower();
			requiredNetworkBandwidths[i] = processes[i].getRequiredNetworkBandwidth();
		}
		VarComputer[] varComputers = new VarComputer[computers.length];
		for (int i = 0; i < varComputers.length; i++) {
			varComputers[i] = new VarComputer(p, computers[i], processes, 
								requiredMemories, requiredCpuPowers, requiredNetworkBandwidths );
		}
		System.out.println("Created "+computers.length*processes.length+" decision variables");
		
		p.log("Post constraints 'Each process uses one and only one computer'");
		Var[] allVars = new Var[processes.length * computers.length];
		int[] costs = new int[processes.length * computers.length];
		int n = 0;
		for (int i = 0; i < processes.length; i++) {
			Var[] assignedComputerVars = new Var[computers.length];
			for (int j = 0; j < computers.length; j++) {
				assignedComputerVars[j] = varComputers[j].getProcessVars()[i];
				allVars[n] = assignedComputerVars[j];
				costs[n] = computers[j].getCost();
				n++;
			}
			p.post(assignedComputerVars, "=", 1);
		}
		
		Var totalCostVar = p.scalProd(costs,allVars);
		totalCostVar.setName("Cost");
		p.add(totalCostVar);
		
		p.log("Problem is defined in " + (System.currentTimeMillis() - start) + " msec");
		
		p.log("Find a solution: " + Calendar.getInstance().getTime());
		Solver solver = p.getSolver();
//		solver.getSearchStrategy().setVars(allVars);
//		Solution solution = solver.findSolution();
		solver.traceSolutions(true);
		solver.setTimeLimit(100000); // milliseconds for one solution search
		Solution solution = solver.findOptimalSolution(Objective.MINIMIZE,totalCostVar,
				            OptimizationStrategy.BASIC);
							//OptimizationStrategy.DICHOTOMIZE);
							//OptimizationStrategy.NATIVE);
		if (solution != null)
			printSolution(solution);
		else
			p.log("No solutions");

		System.out.println("\nTotal Execution time: " + (System.currentTimeMillis() - start) + " msec");

	}
	
//	static public void printSolution(Solution solution) {
//		solution.log();
//	}
	
	static public void printSolution(Solution solution) {
		//solution.log();
		int s = 0;
		System.out.println("Solution:");
		Var[] vars = solution.getProblem().getVars();
		for (int i = 0; i < vars.length; i++) {
			Var var = vars[i];
			int value = solution.getValue(var.getName());
			if (value != 0) {
				System.out.print(var.getName() + "[" + value + "] ");
				s++;
				if (s%10 == 0)
					System.out.println();
			}
		}
	}

	public static void main(String[] args) {
//		String test = "./data/cb-0002comp-0006proc.xml"; // optimal: 20,520
//		String test = "./data/cb-0003comp-0009proc.xml"; // optimal: 18,440
//		String test = "./data/cb-0004comp-0012proc.xml"; // optimal: 22,250
//		String test = "./data/cb-0010comp-0020proc.xml"; // optimal: 21,970
		String test = "./data/cb-0020comp-0040proc.xml"; // optimal: 34,379 
//		String test = "./data/cb-0020comp-0070proc.xml"; // infeasible problem
//		String test = "./data/cb-0050comp-0100proc.xml"; // optimal: 85,730
//		String test = "./data/cb-0075comp-0200proc.xml"; 
//		String test = "./data/cb-0100comp-0300proc.xml";
//		String test = "./data/cb-0800comp-2400proc.xml";
		CloudComputer[] computers = XmlReader.readComputers(test);
		CloudProcess[] processes = XmlReader.readProcesses(test);
		System.out.println("computers: " + computers.length);
		System.out.println("processes: " + processes.length);
		CloudBalancer.balance(computers,processes);
	}
}

/*
Solution #1:
	 P0-C0[0] P1-C0[1] P2-C0[1] P3-C0[1] P4-C0[1] P5-C0[0] P0-C1[1] P1-C1[0] P2-C1[0]
	 P3-C1[0] P4-C1[0] P5-C1[1] TotalCost[20520]
 */
