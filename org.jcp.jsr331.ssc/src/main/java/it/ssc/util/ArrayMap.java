package it.ssc.util;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class ArrayMap<E,V> {
	private ArrayList<V> list;
	private LinkedHashMap<E,V> map;
	
	public ArrayMap() {
		list=new ArrayList<V>();
		map=new LinkedHashMap<E,V>();
	}
	
	public void put(E key, V value) {
		list.add(value);
		map.put(key, value);
	}
	
	public  V get(int index) {
		return list.get(index);
	}

	public V get(E key) {
		return map.get(key);
	}
	
	public void remove(E key) { 
		V value=map.remove(key);
		list.remove(value);
	}
	
	public Iterable<E> getIterableKeys() {
		return map.keySet();
	}
	
	public Iterable<V> getIterableValues() {
		return list;
	}
	
	public int getSize() {
		return list.size();
	}
}
