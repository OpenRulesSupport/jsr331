package it.ssc.pl.milp;

import java.util.HashMap;

 class Tree {
	private HashMap<Integer,MilpManager> tree ;
	public Tree() {
		tree=new HashMap<Integer,MilpManager> ();
	}
	
	public MilpManager getMilpBestUPMax() { 
		MilpManager milp_best=null; 
		for(MilpManager milp :tree.values()) {
			if(milp_best==null) milp_best=milp;
			else if(milp_best.getOptimumValue() < milp.getOptimumValue()) milp_best=milp;
		}
		deleteNode(milp_best);
		return milp_best;
	}
	
	
	public MilpManager getMilpBestUPMin() { 
		MilpManager milp_best=null; 
		for(MilpManager milp :tree.values()) {
			if(milp_best==null) milp_best=milp;
			else if(milp_best.getOptimumValue() > milp.getOptimumValue()) milp_best=milp;
		}
		deleteNode(milp_best);
		return milp_best;
	}
	
	public MilpManager getMilpBestUPNotDelete() { 
		MilpManager milp_best=null; 
		for(MilpManager milp :tree.values()) {
			if(milp_best==null) milp_best=milp;
			else if(milp_best.getOptimumValue() < milp.getOptimumValue()) milp_best=milp;
		}
		//deleteNode(milp_best);
		return milp_best;
	}
	
	
	public MilpManager getMilpBestIdNotDelete() { 
		MilpManager milp_best=null; 
		for(MilpManager milp :tree.values()) {
			if(milp_best==null) milp_best=milp;
			else if(milp_best.getId() < milp.getId()) milp_best=milp;
		}
		//deleteNode(milp_best);
		return milp_best;
	}
		
	public boolean isEmpty() { 
		return tree.isEmpty();
	}
	
	public void addNode(MilpManager milp) {
		tree.put(milp.getId(), milp);
	}
	
	public  void deleteNode(MilpManager milp) { 
		tree.remove(milp.getId());
	}
	
	public void deleteNodeWhitUPnotValideMax(double lb) {
		/*
		HashMap<Integer,ManagerMILP> tree_clone=(HashMap<Integer,ManagerMILP>)tree.clone();
		for(ManagerMILP milp :tree_clone.values()) {
			if(milp.getOptimumValue() <= lb)  { 
				tree.remove(milp.getId());
				//System.out.println("up SCADUTO - RIMOSSO PROBLEMA ID:"+milp.getId() +"z: "+milp.getUP() +" con LB:"+lb);
			}
		}
		*/
		
		tree.entrySet().removeIf(e -> e.getValue().getOptimumValue() <=lb);
	}
	
	public void deleteNodeWhitUPnotValideMin(double lb) {
		/*
		HashMap<Integer,ManagerMILP> tree_clone=(HashMap<Integer,ManagerMILP>)tree.clone();
		for(ManagerMILP milp :tree_clone.values()) {
			if(milp.getOptimumValue() <= lb)  { 
				tree.remove(milp.getId());
				//System.out.println("up SCADUTO - RIMOSSO PROBLEMA ID:"+milp.getId() +"z: "+milp.getUP() +" con LB:"+lb);
			}
		}
		*/
		
		tree.entrySet().removeIf(e -> e.getValue().getOptimumValue() >=lb);
	}
	
	
	
}
