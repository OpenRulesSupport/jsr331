package javax.constraints.extra;

import java.util.ArrayList;

import javax.constraints.Var;

public class VarList extends ArrayList<Var> { 
	
	public Var[] toArray() {
		Var[] array = new Var[size()];
		for (int i = 0; i < array.length; i++) {
			array[i] = get(i);
		}
		return array;
	}
	public void add(Var[] vars) {
		for (int i = 0; i < vars.length; i++) {
			Var var = vars[i];
			add(var);
		}
	} 

}
