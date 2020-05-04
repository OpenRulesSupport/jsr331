package it.ssc.step.parallel;

import java.util.concurrent.CyclicBarrier;
import java.util.logging.Level;
import java.util.logging.Logger;

import it.ssc.log.SscLogger;

 public class Task implements Runnable {

	private static final Logger logger=SscLogger.getLogger();
	private CyclicBarrier barrier;
	private Parallelizable step;

	public Task(CyclicBarrier barrier, Parallelizable step) {
		this.barrier = barrier;
		this.step = step;
	}

	public void run()  {
		try {
			step.run(); 
			barrier.await();
		} 
		catch (Exception e) {
			logger.log(Level.SEVERE,"Execution run() method of ParallelProcesses",e);
			throw new RuntimeException(e);
		}
	}
}
