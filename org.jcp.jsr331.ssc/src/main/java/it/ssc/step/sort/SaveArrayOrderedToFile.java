package it.ssc.step.sort;

import java.io.IOException;
import java.util.ArrayList;

import it.ssc.dynamic_source.DynamicClassSortInterface;

/**
 * Si salvano gli array di DynamicClassSortInterface in file. Avro n file. 
 * Una volta finita la scrittura, avro il puntamento a ciascuno degli n file. 
 * 
 * @author Stefano
 *
 */

class SaveArrayOrderedToFile implements MyIterator {
	private boolean saved_to_file=false;
	private FileRecordSorted[] all_stack;
	private ArrayList<FileRecordSorted> list_all_stack;
	FileRecordSorted stack1=null;
	FileRecordSorted stack2=null;
	private int index_file;
	private String path_sort;
	
	
	SaveArrayOrderedToFile(String path_sort)  {
		this.path_sort=path_sort;
		list_all_stack=new ArrayList<FileRecordSorted>();
	}
	
	public void flush() throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException {
		all_stack=new FileRecordSorted[list_all_stack.size()]; 
		all_stack=list_all_stack.toArray(all_stack);
		for (FileRecordSorted file:all_stack) {
			file.open(); 
		}
	}
	
	void saveToFile(DynamicClassSortInterface[] array,Class<DynamicClassSortInterface> class_record) throws Exception { 
		list_all_stack.add(new FileRecordSorted(array,this.path_sort,class_record));
		saved_to_file=true;
	}

	public boolean isSavedToFile() {
		return saved_to_file;
	}
	
	public boolean hasNext()  {
		for(this.index_file=0;index_file< all_stack.length;index_file++) { 
			if((this.stack1=all_stack[index_file]).getFirst()!=null)  return true;  
		}
		return false; 
	}
	
	public DynamicClassSortInterface next() throws IOException, ClassNotFoundException { 
		
		for( this.index_file++;index_file< all_stack.length;index_file++) { 
			if((this.stack2=all_stack[index_file]).getFirst()!=null)  { 
				if(this.stack1.getFirst().compareTo(this.stack2.getFirst())  > 0)  { 
					this.stack1=this.stack2; 
				}
			}
		}
		return this.stack1.getFirstLoadNext();
	}
	
	public void close() throws IOException {
		if(all_stack==null) return;
		for(FileRecordSorted file: all_stack) {
			//chiude e cancella; 
			file.close();
		}
	}
}
