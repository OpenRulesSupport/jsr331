package cloud.balancing;

import javax.constraints.Problem;
import javax.constraints.Var;

public class VarComputer {
	CloudComputer computer;
	Var[] processVars; // processVars[i] = 1 means this computer is used by the i-th process
	
	public VarComputer(Problem p, CloudComputer computer, CloudProcess[] processes,
			int[] requiredMemories, int[] requiredCpuPowers, int[] requiredNetworkBandwidths) {
		this.computer = computer;
		processVars = new Var[processes.length];
		for (int i = 0; i < processes.length; i++) {
			String name = "P" + processes[i].getId() + "C" + computer.getId();
			processVars[i] = p.variable(name,0,1);
		}
		p.post(requiredMemories, processVars,"<=",computer.getMemory());
		p.post(requiredCpuPowers, processVars,"<=",computer.getCpuPower());
		p.post(requiredNetworkBandwidths, processVars,"<=",computer.getNetworkBandwidth());
	}

	public Var[] getProcessVars() {
		return processVars;
	}
}
