package it.ssc.step.sort;

import it.ssc.dynamic_source.DynamicClassSortInterface;

public class SaveArrayOrderedToMemory implements MyIterator {
	private DynamicClassSortInterface[] array;
	private int index=0;
	
	public boolean hasNext() {
		if(this.index < array.length) return true;
		else return false;
	}
	
	public DynamicClassSortInterface next() {
		return array[this.index++];
	}
	
	
	public void close() {
		array=null;
	}
	
	public void saveToMemory(DynamicClassSortInterface[] array) {
		this.array=array;
	}
}
