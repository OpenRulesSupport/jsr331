package it.ssc.step.sort;

import java.io.IOException;

import it.ssc.dynamic_source.DynamicClassSortInterface;

public interface MyIterator  {
	
	public boolean hasNext()  ;
	public DynamicClassSortInterface next() throws IOException, ClassNotFoundException; 
	
}
