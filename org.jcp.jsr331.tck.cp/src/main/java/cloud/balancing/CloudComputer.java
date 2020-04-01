package cloud.balancing;
public class CloudComputer {

	private String id;
	private int cpuPower;
	private int memory;
	private int networkBandwidth;
	private int cost;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getCpuPower() {
		return cpuPower;
	}

	public void setCpuPower(int cpuPower) {
		this.cpuPower = cpuPower;
	}

	public int getMemory() {
		return memory;
	}

	public void setMemory(int memory) {
		this.memory = memory;
	}

	public int getNetworkBandwidth() {
		return networkBandwidth;
	}

	public void setNetworkBandwidth(int networkBandwidth) {
		this.networkBandwidth = networkBandwidth;
	}

	public int getCost() {
		return cost;
	}

	public void setCost(int cost) {
		this.cost = cost;
	}

	@Override
	public String toString() {
		return "CloudComputer [id=" + id + ", cpuPower=" + cpuPower
				+ ", memory=" + memory + ", networkBandwidth="
				+ networkBandwidth + ", cost=" + cost + "]";
	}

}
