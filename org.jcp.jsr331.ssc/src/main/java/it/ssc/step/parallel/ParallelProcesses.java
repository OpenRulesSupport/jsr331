package it.ssc.step.parallel;

import it.ssc.log.SscLevel;
import it.ssc.log.SscLogger;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.logging.Level;
import java.util.logging.Logger;


public class ParallelProcesses {
	
	private static final Logger logger=SscLogger.getLogger();
	private String desc_name = "";
	private Thread tgroup[];
	private CyclicBarrier cb;

	public ParallelProcesses(Parallelizable... steps) throws InterruptedException {
		int num_step = steps.length;
		cb = new CyclicBarrier(num_step, new Runnable() {

			public void run() {
				// This task will be executed once all thread reaches barrier
				logger.log(SscLevel.INFO,"Il gruppo di processi " + desc_name
						+ " e' stato eseguito.");
			}
		});
		
		

		tgroup = new Thread[num_step];
		for (int a = 0; a < tgroup.length; a++) {
			tgroup[a] = new Thread(new Task(cb, steps[a]));
		}
	}

	public void setDescName(String name) {
		this.desc_name = "\""+name+"\"";
	}

	public void esecute() throws InterruptedException, BrokenBarrierException {
		for (int a = 0; a < tgroup.length; a++) {
			tgroup[a].start();
		}
		// mette in attesa main finche' non termina tred[0] 
		tgroup[0].join();
	}

	
}
