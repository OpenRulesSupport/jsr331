package cloud.balancing.cp;

/*
 * The problem description can be found at http://java.dzone.com/articles/resource-optimization-quick
 */

import java.util.Arrays;
import java.util.Calendar;

import javax.constraints.Constraint;
import javax.constraints.Objective;
import javax.constraints.OptimizationStrategy;
import javax.constraints.Problem;
import javax.constraints.ProblemFactory;
import javax.constraints.Solution;
import javax.constraints.Solver;
import javax.constraints.Var;

import cloud.balancing.CloudComputer;
import cloud.balancing.CloudProcess;
import cloud.balancing.ProcessComparator;
import cloud.balancing.XmlReader;

public class CloudBalancer {

	static public void balance(CloudComputer[] computers, CloudProcess[] processes) {

		long start = System.currentTimeMillis();

		Problem p = ProblemFactory.newProblem("CloudBalancing");
		
		Arrays.sort(processes,new ProcessComparator());

		//p.log("Create "+computers.length + " constrained computers");
		int maxMemory = 0;
		int maxCpuPower = 0;
		int maxNetworkBandwidth = 0;
		int maxCost= 0;
		for (int i = 0; i < computers.length; i++) {
			if (maxMemory < computers[i].getMemory())
				maxMemory = computers[i].getMemory();
			if (maxCpuPower < computers[i].getCpuPower())
				maxCpuPower = computers[i].getCpuPower();
			if (maxNetworkBandwidth < computers[i].getNetworkBandwidth())
				maxNetworkBandwidth = computers[i].getNetworkBandwidth();
			if (maxCost < computers[i].getCost())
				maxCost = computers[i].getCost();
		}

		// Create decision variables processUsesComputerVars with domain[0;computers.length-1] (one per process)
		// Computer i is used by Process j if processUsesComputerVars[i] equals to j
		// Since a variable processUsesComputerVars[j] can be bound with only one value, 
		// it means that each process uses exactly one computer
		Var[] processUsesComputerVars = p.variableArray("P",0,computers.length-1,processes.length);
		System.out.println("Created "+processes.length+" decision variables with domain [0;"
				    + (computers.length-1) + "]");
		
		// Post "process uses a computer" memory, CPU, and network constraints 
		CloudComputer computer = null;
		try {
			for (int i = 0; i < computers.length; i++) {
				computer = computers[i];
				Var[] memoryVars = p.variableArray("mem"+i,0,maxMemory, processes.length);
				Var[] cpuPowerVars = p.variableArray("cpu"+i,0,maxCpuPower, processes.length);
				Var[] networkBandwidthVars = p.variableArray("net"+i,0,maxNetworkBandwidth, processes.length);
				for (int j = 0; j < processes.length; j++) {
					CloudProcess process = processes[j];
					//exclude obvious inconsistencies
					if (computer.getMemory() < process.getRequiredMemory() ||
						computer.getCpuPower() < process.getRequiredCpuPower() ||
						computer.getNetworkBandwidth() < process.getRequiredNetworkBandwidth()) {
						p.post(processUsesComputerVars[j],"!=",i);
						p.post(memoryVars[j],"=",0);
						p.post(cpuPowerVars[j],"=",0);
						p.post(networkBandwidthVars[j],"=",0);
						continue;
					}
					// process j uses computer i	
					Constraint c1 = p.linear(processUsesComputerVars[j],"=",i);
					Constraint c2 = p.linear(memoryVars[j],"=",process.getRequiredMemory());
					Constraint c3 = p.linear(cpuPowerVars[j],"=",process.getRequiredCpuPower());
					Constraint c4 = p.linear(networkBandwidthVars[j],"=",process.getRequiredNetworkBandwidth());
					p.post(c1.implies(c2.and(c3).and(c4)));
					// process j does not use computer i
					Constraint c5 = p.linear(processUsesComputerVars[j],"!=",i);
					Constraint c6 = p.linear(memoryVars[j],"=",0);
					Constraint c7 = p.linear(cpuPowerVars[j],"=",0);
					Constraint c8 = p.linear(networkBandwidthVars[j],"=",0);
					p.post(c5.implies(c6.and(c7).and(c8)));
				}
				p.post(memoryVars,"<=",computer.getMemory());
				p.post(cpuPowerVars,"<=",computer.getCpuPower());
				p.post(networkBandwidthVars,"<=",computer.getNetworkBandwidth());
			}
			
		} catch (Exception e) {
			p.log("Error posting constraints for computer "+ computer);
			System.exit(-1);
		}
		
		// calculate cost variables for each computer
		// cardVars[i] shows how many variables (processes) in the array processUsesComputerVars
		// have value i (use computer i)
		Var[] cardVars = p.variableArray("card", 0, processes.length-1, computers.length);
		Var[] costVars = new Var[computers.length];
		for (int i = 0; i < computers.length; i++) {
			p.postCardinality(processUsesComputerVars, i, "=", cardVars[i]);
			costVars[i] = cardVars[i].multiply(computers[i].getCost());
			p.add("cost"+i,costVars[i]);
		}
		
		Var totalCostVar = p.sum(costVars);
		totalCostVar.setName("TotalCost");
		p.add(totalCostVar);
		p.log("Problem is defined in " + (System.currentTimeMillis() - start) + " msec");
//		p.log(p.getVars());
		p.log("Find a solution: " + Calendar.getInstance().getTime());
		Solver solver = p.getSolver();
		solver.getSearchStrategy().setVars(processUsesComputerVars);
		//Solution solution = solver.findSolution();
		solver.traceSolutions(true);
		solver.setTimeLimit(60000);
		Solution solution = solver.findOptimalSolution(Objective.MINIMIZE,totalCostVar
				    );
				 	//,OptimizationStrategy.BASIC);
					//,OptimizationStrategy.NATIVE);
					//,OptimizationStrategy.DICHOTOMIZE);
		if (solution != null) {
//			solution.log();
			for (int i = 0; i < processUsesComputerVars.length; i++) {
				String name = processUsesComputerVars[i].getName();
				int value = solution.getValue(name);
				p.log(name + " uses computer " + value);
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
		String test = "./data/cb-0010comp-0020proc.xml"; // optimal: 21,970
//		String test = "./data/cb-0020comp-0040proc.xml"; // optimal: 34,379; pure CP stops at 67,280 
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
