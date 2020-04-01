package cloud.balancing;


public class CloudProcess {
	private String id;
	private int requiredCpuPower;
	private int requiredMemory;
	private int requiredNetworkBandwidth;

	private CloudComputer computer;
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getRequiredCpuPower() {
		return requiredCpuPower;
	}

	public void setRequiredCpuPower(int requiredCpuPower) {
		this.requiredCpuPower = requiredCpuPower;
	}

	public int getRequiredMemory() {
		return requiredMemory;
	}

	public void setRequiredMemory(int requiredMemory) {
		this.requiredMemory = requiredMemory;
	}

	public int getRequiredNetworkBandwidth() {
		return requiredNetworkBandwidth;
	}

	public void setRequiredNetworkBandwidth(int requiredNetworkBandwidth) {
		this.requiredNetworkBandwidth = requiredNetworkBandwidth;
	}

	public CloudComputer getComputer() {
		return computer;
	}

	public void setComputer(CloudComputer computer) {
		this.computer = computer;
	}

	@Override
	public String toString() {
		return "CloudProcess [id=" + id + ", requiredCpuPower="
				+ requiredCpuPower + ", requiredMemory=" + requiredMemory
				+ ", requiredNetworkBandwidth=" + requiredNetworkBandwidth
				+ ", computer=" + computer + "]";
	}

}
