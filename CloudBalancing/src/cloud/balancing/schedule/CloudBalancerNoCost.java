package cloud.balancing.schedule;

/*
 * The problem description can be found at http://java.dzone.com/articles/resource-optimization-quick
 */

import javax.constraints.Solution;
import javax.constraints.Solver;
import javax.constraints.Var;
import javax.constraints.scheduler.Activity;
import javax.constraints.scheduler.Resource;
import javax.constraints.scheduler.Schedule;
import javax.constraints.scheduler.ScheduleFactory;

import cloud.balancing.CloudComputer;
import cloud.balancing.CloudProcess;

public class CloudBalancerNoCost {

	static public void balance(CloudComputer[] computers, CloudProcess[] processes) {

		long executionStart = System.currentTimeMillis();
		
		Schedule s = ScheduleFactory.newSchedule("CloudBalancing",0,1);
		// Create activities for all processes
		Activity[] activities = new Activity[processes.length];
		for (int i = 0; i < processes.length; i++) {
			activities[i] = s.activity(processes[i].getId(),1);
		}
		// Create resources for all computer memories and cpuPowers
		Resource[] memories = new Resource[computers.length];
		Resource[] cpuPowers = new Resource[computers.length];
		for (int i = 0; i < computers.length; i++) {
			 memories[i] = s.resource(computers[i].getId(),computers[i].getMemory());
			 cpuPowers[i] = s.resource(computers[i].getId(),computers[i].getCpuPower());
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
			}
		}

		// Post constraint "a process uses one and only one computer"
		Var[] allVars = new Var[processes.length * computers.length];
		int n = 0;
		for (int i = 0; i < processes.length; i++) {
			Var[] assignedComputers = new Var[computers.length];
			for (int j = 0; j < computers.length; j++) {
				assignedComputers[j] = processUsesComputerVars[i][j];
				allVars[n++] = processUsesComputerVars[i][j];
			}
			s.post(assignedComputers, "=", 1);
		}

		Solver solver = s.getSolver();
		solver.getSearchStrategy().setVars(allVars);
		Solution solution = solver.findSolution();
		if (solution != null) {
			for (int i = 0; i < allVars.length; i++) {
				if (allVars[i].getValue() == 1)
					s.log(allVars[i].getName());
			}
		} else {
			s.log("No solutions");
		}

		long executionTime = System.currentTimeMillis() - executionStart;
		System.out.println("Total Execution time: " + executionTime + " msec");

	}

	public static void main(String[] args) {

		CloudComputer c1 = new CloudComputer();
		c1.setId("1");
		c1.setCpuPower(7);
		c1.setMemory(6);
		CloudComputer c2 = new CloudComputer();
		c2.setId("2");
		c2.setCpuPower(6);
		c2.setMemory(6);

		CloudProcess p1 = new CloudProcess();
		p1.setId("A");
		p1.setRequiredCpuPower(5);
		p1.setRequiredMemory(5);
		CloudProcess p2 = new CloudProcess();
		p2.setId("B");
		p2.setRequiredCpuPower(4);
		p2.setRequiredMemory(3);
		CloudProcess p3 = new CloudProcess();
		p3.setId("C");
		p3.setRequiredCpuPower(2);
		p3.setRequiredMemory(3);
		CloudProcess p4 = new CloudProcess();
		p4.setId("D");
		p4.setRequiredCpuPower(2);
		p4.setRequiredMemory(1);

		CloudComputer[] computers = { c1, c2 };
		CloudProcess[] processes = { p1, p2, p3, p4 };
		
		CloudBalancerNoCost.balance(computers,processes);

	}

}
