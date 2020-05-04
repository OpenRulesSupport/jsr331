package it.ssc.pl.milp;

import java.util.ArrayList;

public final class ListConstraints {
	private ArrayList<Constraint> listConstraint;
	
	public ListConstraints() {
		listConstraint=new ArrayList<Constraint>();
	}
	
	public void add(Constraint constraint) {
		listConstraint.add(constraint);
	}

	public ArrayList<Constraint> getListConstraint() {
		return listConstraint;
	}
}
