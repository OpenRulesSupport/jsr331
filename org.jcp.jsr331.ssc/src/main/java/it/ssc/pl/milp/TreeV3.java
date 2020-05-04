package it.ssc.pl.milp;


import java.util.ArrayList;
import java.util.PriorityQueue;


import it.ssc.pl.milp.ObjectiveFunction.TARGET_FO;
import it.ssc.pl.milp.util.MILPThreadsNumber;

 final class TreeV3 {
	private PriorityQueue<MilpManager> queue ;
	private TARGET_FO target;

	public TreeV3(TARGET_FO target) {
		this.target=target;
		if (target==TARGET_FO.MAX) queue = new PriorityQueue<MilpManager>((e,o) ->  e.compareTo(o));
		else queue = new PriorityQueue<MilpManager>((e,o) ->  -e.compareTo(o));
	}
	
	public MilpManager getMilpBestUP() { 
		return queue.poll();
	}
	
	public ArrayList<MilpManager> getMilpBestUP(MILPThreadsNumber threadNumber) { 
		int num_lp=0;
		ArrayList<MilpManager> list_best=new ArrayList<MilpManager> ();
		while(!queue.isEmpty() && num_lp!=threadNumber.getNumLp()) { 
			list_best.add(queue.poll());
			num_lp++;
		}
		return list_best;
	}
	
	public boolean isEmpty() { 
		return queue.isEmpty();
	}
	
	public void addNode(MilpManager milp) {
		queue.add(milp);
	}
	
	
	public void deleteNodeWhitUPnotValide(double lb) {
		if (target==TARGET_FO.MAX) queue.removeIf(e -> e.getOptimumValue() <=lb);
		else queue.removeIf(e -> e.getOptimumValue() >=lb);
	}
	
	/*
	
	public void deleteNodeWhitUPnotValide(double lb,double tollerance) {
		if (target==TARGET_FO.MAX) queue.removeIf(e -> e.getOptimumValue() <=(lb+tollerance));
		else queue.removeIf(e -> e.getOptimumValue() >=(lb-tollerance));
	}
	 * 
	 */
	
}

