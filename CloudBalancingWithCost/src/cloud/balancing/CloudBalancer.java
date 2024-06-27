package cloud.balancing;

/*
 * The problem description can be found at http://java.dzone.com/articles/resource-optimization-quick
 */

import java.util.Arrays;

import javax.constraints.Objective;
import javax.constraints.OptimizationStrategy;
import javax.constraints.Problem;
import javax.constraints.ProblemFactory;
import javax.constraints.SearchStrategy;
import javax.constraints.Solution;
import javax.constraints.Solver;
import javax.constraints.Var;
import javax.constraints.VarMatrix;
import javax.constraints.VarSelectorType;
import javax.constraints.scheduler.Activity;
import javax.constraints.scheduler.Resource;
import javax.constraints.scheduler.Schedule;
import javax.constraints.scheduler.ScheduleFactory;

public class CloudBalancer {

	static public void balance(CloudComputer[] computers, CloudProcess[] processes) {

		long executionStart = System.currentTimeMillis();
		
		Schedule s = ScheduleFactory.newSchedule("CloudBalancing",0,1);
		
		Arrays.sort(processes,new ProcessComparator());
//		for (int i = 0; i < processes.length; i++) {
//			s.log(""+processes[i]);
//		}
		// Create activities for all processes
		Activity[] activities = new Activity[processes.length];
		for (int i = 0; i < processes.length; i++) {
			activities[i] = s.activity(processes[i].getId(),1);
		}
		// Create resources for all computer memories and cpuPowers
		Resource[] memories = new Resource[computers.length];
		Resource[] cpuPowers = new Resource[computers.length];
		Resource[] requiredNetworkBandwidths = new Resource[processes.length];
		for (int i = 0; i < computers.length; i++) {
			 memories[i] = s.resource(computers[i].getId()+"_memory",computers[i].getMemory());
			 cpuPowers[i] = s.resource(computers[i].getId()+"_cpu",computers[i].getCpuPower());
			 requiredNetworkBandwidths[i] = s.resource(computers[i].getId()+"_bandwidth",computers[i].getCpuPower());
		}
		// Post constraints between required and available memories and cpuPowers
		Var[][] processUsesComputerVars = new Var[processes.length][computers.length];
		for (int i = 0; i < processes.length; i++) {
			for (int j = 0; j < computers.length; j++) {
				String name = "Process " + processes[i].getId() + " uses computer " + computers[j].getId();
				processUsesComputerVars[i][j] = s.variable(name,0,1);
				Var requiredMemoryVar = processUsesComputerVars[i][j].multiply(processes[i].getRequiredMemory());
				activities[i].requires(memories[j],requiredMemoryVar).post();
				Var requiredCpuPowerVar = processUsesComputerVars[i][j].multiply(processes[i].getRequiredCpuPower());
				activities[i].requires(cpuPowers[j],requiredCpuPowerVar).post();
				Var requiredNetworkBandwidthVar = processUsesComputerVars[i][j].multiply(processes[i].getRequiredNetworkBandwidth());
				activities[i].requires(requiredNetworkBandwidths[j],requiredNetworkBandwidthVar).post();
			}
		}

		// Post constraint "a process uses one and only one computer"
		Var[] allVars = new Var[processes.length * computers.length+1];
		Var[] allCostVars = new Var[processes.length * computers.length];
		int n = 0;
		for (int i = 0; i < processes.length; i++) {
			Var[] assignedComputers = new Var[computers.length];
			for (int j = 0; j < computers.length; j++) {
				assignedComputers[j] = processUsesComputerVars[i][j];
				allCostVars[n] = processUsesComputerVars[i][j].multiply(computers[j].getCost());
				allVars[n++] = processUsesComputerVars[i][j];
			}
			s.post(assignedComputers, "=", 1);
		}
		Var totalCostVar = s.sum(allCostVars);
		totalCostVar.setName("Total Cost");
		s.add(totalCostVar);
		allVars[n] = totalCostVar;

		Solver solver = s.getSolver();
		solver.setTimeLimit(60000); // mills for one solution search
		solver.setTimeLimitGlobal(900000); // mills for optimal solution search
		SearchStrategy strategy = solver.getSearchStrategy();
		strategy.setVars(allVars);
		//strategy.setVarSelectorType(VarSelectorType.RANDOM);
		solver.traceSolutions(true);
		Solution solution = null;
		if (processes.length < 50) {
			solver.setMaxNumberOfSolutions(10);
			//solver.setOptimizationTolerance(300);
			//solver.traceExecution(true);
			solution = solver.findOptimalSolution(Objective.MINIMIZE,totalCostVar,
					OptimizationStrategy.BASIC); 
					//OptimizationStrategy.DICHOTOMIZE);
					//OptimizationStrategy.NATIVE);
		}
		else
			solution = solver.findSolution();
		if (solution != null) {
			for (int i = 0; i < allVars.length; i++) {
				int value = solution.getValue(allVars[i].getName());
				if (value == 1)
					s.log(allVars[i].getName());
			}
			s.log("Total Cost: "+ solution.getValue("Total Cost"));
		} else {
			s.log("No solutions");
		}
		solver.logStats();
		long executionTime = System.currentTimeMillis() - executionStart;
		System.out.println("Total Execution time: " + executionTime + " msec");

	}

	public static void main(String[] args) {
//		String test = "./data/cb-0002comp-0006proc.xml";
//		String test = "./data/cb-0003comp-0009proc.xml";
//		String test = "./data/cb-0004comp-0012proc.xml";
		String test = "./data/cb-0010comp-0020proc.xml";
//		String test = "./data/cb-0015comp-0030proc.xml";
//		String test = "./data/cb-0030comp-0060proc.xml";
//		String test = "./data/cb-0050comp-0100proc.xml";
//		String test = "./data/cb-0075comp-0200proc.xml"; // 68 secs
//		String test = "./data/cb-0100comp-0300proc.xml";
//		String test = "./data/cb-0800comp-2400proc.xml";
		CloudComputer[] computers = XmlReader.readComputers(test);
		CloudProcess[] processes = XmlReader.readProcesses(test);
		System.out.println("computers: " + computers.length);
		System.out.println("processes: " + processes.length);
		CloudBalancer.balance(computers,processes);
	}

}
