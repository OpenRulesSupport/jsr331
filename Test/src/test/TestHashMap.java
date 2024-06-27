package test;

import java.util.HashMap;

public class TestHashMap {
	
	public static void main(String[] args) {
		HashMap map =  new HashMap();
		int value = 5;
		Integer i1 = new Integer(value);
		map.put(i1, "One");
		Integer i2 = new Integer(value);
		System.out.println(map.get(i2));
	}
	

}
