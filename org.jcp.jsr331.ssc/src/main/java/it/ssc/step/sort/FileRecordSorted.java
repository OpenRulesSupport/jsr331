package it.ssc.step.sort;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Random;

import it.ssc.dynamic_source.DynamicClassSortInterface;

class FileRecordSorted {
	
	private DynamicClassSortInterface first1;
	private DynamicClassSortInterface first2;
	private DynamicClassSortInterface appo_first;
	private boolean is_first1=true;
	private File file_sorted;
	private DataInputStream  object_input_stream;
	private int num_record=0;
	
	
	//Gli viene passato il path_sort con separatore finale compreso 
	FileRecordSorted(DynamicClassSortInterface[] array, String path_sort,Class<DynamicClassSortInterface> class_record) throws Exception { 
		 file_sorted=createFileSorted(path_sort);
		 saveArray(array);
		 first1= (DynamicClassSortInterface)class_record.newInstance();
		 first2= (DynamicClassSortInterface)class_record.newInstance();
	}
	
	
	public DynamicClassSortInterface getFirst() {
		if(this.is_first1) return first1;
		else return first2;
	}
	
	//restituisce il primo della lista , rimettendo come first 
	// l'elemento successivo 
	
	public DynamicClassSortInterface getFirstLoadNext() throws IOException, ClassNotFoundException {

		if (this.is_first1) appo_first = first1;
		else appo_first = first2;
		
		if (num_record == 1) {
			first1 = null;
			first2 = null;
		} 
		else {
			if (this.is_first1) {
				first2.readExternal(object_input_stream);
				this.is_first1 = false;
			} 
			else {
				first1.readExternal(object_input_stream);
				this.is_first1 = true;
			}
			num_record--;
		}
		return appo_first;
	}
	
	// apre i file e mette il primo elemento in first
	public void open() throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException {
		FileInputStream fout=new FileInputStream(file_sorted);
		BufferedInputStream buff=new BufferedInputStream(fout);
		object_input_stream=new DataInputStream(buff);
		first1.readExternal(object_input_stream); 
	}
	
	//chiude e cancella; 
	public void close() throws IOException  {
		object_input_stream.close();
		file_sorted.delete();
	}
	
	private void saveArray(DynamicClassSortInterface[] array) throws IOException {
		FileOutputStream fout=new FileOutputStream(file_sorted);
		BufferedOutputStream buff=new BufferedOutputStream(fout);
		DataOutputStream  oouts=new DataOutputStream(buff);
		for(DynamicClassSortInterface obj:array) { 
			obj.writeExternal(oouts);
			//num_record++;
		}
		num_record=array.length;
		array=null;
		oouts.close();
	}
	
	
	private static synchronized File createFileSorted(String path_sort) throws IOException {
		String path_and_name;
		File file;
		do {
			Random ra = new Random(new Date().getTime());
			path_and_name = path_sort + "s" + Math.abs(ra.nextInt()) + ".ser"; 
			file = new File(path_and_name);
		} 
		while (file.exists());
		file.createNewFile();
		return file;
	}
}
