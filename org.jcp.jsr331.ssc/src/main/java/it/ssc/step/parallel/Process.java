package it.ssc.step.parallel;

/**
 * @deprecated
 * @author Stefano
 *
 */
 class Process implements Parallelizable {
	private Parallelizable processes[];
	
	public Process(Parallelizable... processi) {
		this.processes=processi;
	}
	
	public void run() throws Exception {
		for(Parallelizable process:processes) process.run();
	}
}
