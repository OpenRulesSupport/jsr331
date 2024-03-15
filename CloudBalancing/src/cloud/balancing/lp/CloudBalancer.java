package cloud.balancing.lp;

/*
 * The problem description can be found at http://java.dzone.com/articles/resource-optimization-quick
 */

import java.util.Arrays;

import javax.constraints.Objective;
import javax.constraints.Problem;
import javax.constraints.ProblemFactory;
import javax.constraints.Solution;
import javax.constraints.Solver;
import javax.constraints.Var;

import cloud.balancing.CloudComputer;
import cloud.balancing.CloudProcess;
import cloud.balancing.ProcessComparator;
import cloud.balancing.VarComputer;
import cloud.balancing.XmlReader;

public class CloudBalancer {

	static public void balance(CloudComputer[] computers, CloudProcess[] processes) {

		long start = System.currentTimeMillis();

		Problem p = ProblemFactory.newProblem("CloudBalancing");
		
		Arrays.sort(processes,new ProcessComparator());

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
		System.out.println("Created "+computers.length*processes.length
				+" decision variables with domain [0;1]");
		
		p.log("Post constraints 'a process uses one and only one computer'");
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
		totalCostVar.setName("TotalCost");
		p.add(totalCostVar);
		p.log("Problem is defined in " + (System.currentTimeMillis() - start) + " msec");
		
		p.log("Find a solution");
		Solver solver = p.getSolver();
		//solver.getSearchStrategy().setVars(allVars);
		//Solution solution = solver.findSolution();
		solver.traceSolutions(true);
		solver.setTimeLimit(60000);
		Solution solution = solver.findOptimalSolution(Objective.MINIMIZE,totalCostVar);
		if (solution != null) {
			//solution.log();
			for (int i = 0; i < allVars.length; i++) {
				String name = allVars[i].getName();
				int value = solution.getValue(name);
				if (value == 1)
				   p.log(name);
			}
			p.log("Total Cost: "+solution.getValue("TotalCost"));
		} else {
			p.log("No solutions");
		}

		System.out.println("Total Execution time: " + (System.currentTimeMillis() - start) + " msec");

	}

	public static void main(String[] args) {
//		String test = "./data/cb-0002comp-0006proc.xml"; // optimal: 20,520
//		String test = "./data/cb-0003comp-0009proc.xml"; // optimal: 18,440
//		String test = "./data/cb-0004comp-0012proc.xml"; // optimal: 22,250
		String test = "./data/cb-0010comp-0020proc.xml"; // optimal: 21,970; pure CP stops at 23,970
//		String test = "./data/cb-0020comp-0040proc.xml"; // optimal: 34,379 
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
