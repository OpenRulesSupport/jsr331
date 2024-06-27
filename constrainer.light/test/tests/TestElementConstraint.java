package tests;

import com.exigen.ie.constrainer.Constrainer;
import com.exigen.ie.constrainer.IntExp;
import com.exigen.ie.constrainer.IntExpArray;
import com.exigen.ie.constrainer.IntVar;

public class TestElementConstraint {

	public static void main(String[] args) {
		System.out.println("Start test");

		Constrainer C = new Constrainer("XYZ");
		IntVar a = C.addIntVar(10, 10,"A");
		IntVar b = C.addIntVar(4, 10, "B");
		IntVar c = C.addIntVar(7, 10, "C");

		IntExpArray array = new IntExpArray(C, 3);
		array.set(a,0);
		array.set(b,1);
		array.set(c,2);
		
		IntVar ind = C.addIntVar(0, 2, "ind");
		try {
			IntExp el = array.elementAt(ind);
		} catch (Exception e) {
			System.out.println("Error:" + e);
		}
		
		System.out.println("End test");
	}	
}
