package it.ssc.step.sort;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.logging.Logger;

import it.ssc.dynamic_source.DynamicClassSortInterface;
import it.ssc.log.SscLevel;
import it.ssc.log.SscLogger;

public class SortSetRecord implements Iterator<DynamicClassSortInterface> {
	
	private static final Logger logger=SscLogger.getLogger();
	
	private ArrayList<DynamicClassSortInterface> list;
	private DynamicClassSortInterface[] array;
	private int index=0;
	private int dimension;
	
	/**
	 * Questa classe e' delegata ad effettuare l'ordinamento dei record rappresentati tramite l'interfacia 
	 * DynamicClassSortInterface e restituirli al chiamante tramite l'interfaccia Iterator  . 
	 * Possiamo lasciare anche il metodo flushCacheSort() 
	 * 
	 * @param dimension
	 */
	
	public SortSetRecord(int dimension) {
		this.dimension=dimension;
		logger.log(SscLevel.NOTE,"Dimensione del numero di record tenuti in memoria per singolo step di ordinamento:("+dimension+")");
		list=new ArrayList<DynamicClassSortInterface>(); 
	}
	
	
	public void add(DynamicClassSortInterface record) {
		list.add(record);
	}
	
	public void flushCacheSort() {
		array=new DynamicClassSortInterface[list.size()];
		array=list.toArray(array);
		list.clear();
		Arrays.sort(array);
	}

	public boolean hasNext() {
		if(this.index < array.length) return true;
		else return false;
	}
	
	public DynamicClassSortInterface next() {
		return array[this.index++];
	}
	
	public void remove() {
		throw new UnsupportedOperationException();
	}
}
