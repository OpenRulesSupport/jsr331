package it.ssc.dynamic_source;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Serializable;

import it.ssc.pdv.PDVAll;

public interface DynamicClassSortInterface extends Comparable<DynamicClassSortInterface> , Cloneable , Serializable {
	public DynamicClassSortInterface _loadRecord(PDVAll pdv) throws Exception;
	public void	_uploadRecord(DynamicClassSortInterface record_sort,PDVAll pdv) throws Exception;
	public void writeExternal(DataOutputStream out) throws IOException;
	public void readExternal(DataInputStream out) throws IOException, ClassNotFoundException;
}
