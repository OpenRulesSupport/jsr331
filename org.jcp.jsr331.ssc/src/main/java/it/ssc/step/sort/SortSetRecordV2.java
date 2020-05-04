package it.ssc.step.sort;

import it.ssc.dynamic_source.DynamicClassSortInterface;
import it.ssc.log.SscLevel;
import it.ssc.log.SscLogger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Logger;

public class SortSetRecordV2  {
	
	private static final Logger logger=SscLogger.getLogger();
	
	private MyIterator very_sorted_source;
	private ArrayList<DynamicClassSortInterface> list;
	private SaveArrayOrderedToFile save_to_file;
	private SaveArrayOrderedToMemory save_to_memory; 
	private int num_record_adding=0; 
	private int max_dimension_array;
	
	/**
	 * Questa classe e' delegata ad effettuare l'ordinamento dei record rappresentati tramite l'interfacia 
	 * DynamicClassSortInterface e restituirli al chiamante tramite l'interfaccia Iterator . 
	 * Possiamo lasciare anche il metodo flushCacheSort() che svuota, ordinandola , i dati da sortare presenti 
	 * nella cache. 
	 * 
	 * Implementazione : 
	 * 
	 * @param dimension
	 */
	
	public SortSetRecordV2(int dimension,String path_sort,String path_compiler) {
		this.max_dimension_array=dimension;
		save_to_file=new SaveArrayOrderedToFile(path_sort);
		save_to_memory=new SaveArrayOrderedToMemory();
		logger.log(SscLevel.INFO,"Numero massimo di record tenuti in memoria per singolo step di ordinamento:("+dimension+")");
		list=new ArrayList<DynamicClassSortInterface>(); 
	}
	
	
	public void add(DynamicClassSortInterface record) throws Exception {
		num_record_adding++;
		if(num_record_adding > this.max_dimension_array)  { 
			//ordina in un array ,  svuota la lista  e salva in un  file apposito diverso da altri 
			//presente nella directori work/sorting
			save_to_file.saveToFile(sortArrayList(list),(Class<DynamicClassSortInterface>)record.getClass()); 
			num_record_adding=1;
		}
		list.add(record);
	}
	
	private DynamicClassSortInterface[] sortArrayList(ArrayList<DynamicClassSortInterface> list) { 
		DynamicClassSortInterface[] array=new DynamicClassSortInterface[list.size()];
		array=list.toArray(array);
		list.clear();
		Arrays.sort(array);
		return array;
	}
	
	public void flushCacheSort() throws Exception, ClassNotFoundException {
		DynamicClassSortInterface[] array=sortArrayList(list);
		if(save_to_file.isSavedToFile()) {
			logger.log(SscLevel.INFO,"Ordinamento del dataset utilizzando swap su area di work ");
			save_to_file.saveToFile(array,(Class<DynamicClassSortInterface>)array[0].getClass()); 
			save_to_file.flush();
			very_sorted_source=this.save_to_file;
		}
		else {
			logger.log(SscLevel.INFO,"Ordinamento del dataset utilizzando esclusivamente la memoria heap");
			save_to_memory.saveToMemory(array);
			very_sorted_source=this.save_to_memory;
		}
	}

	public MyIterator  iterator() {
		return very_sorted_source;
	}
	
	public void close() throws IOException {
		if(save_to_file!=null) save_to_file.close();
		else if(save_to_memory!=null) save_to_memory.close();
	}
}
