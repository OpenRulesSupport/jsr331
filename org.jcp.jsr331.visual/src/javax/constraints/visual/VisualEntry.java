package javax.constraints.visual;

import javax.constraints.Constraint;
import javax.constraints.Var;

public class VisualEntry {
	int nr;
	Constraint constraint;
	Var var;
	Var[] vector;
	Var[][] matrix;
	VisualEntryType type;
	String name;

	public VisualEntry(Constraint c, int nr) {
		this.nr = nr;
		this.constraint = c;
		this.type = VisualEntryType.CONSTRAINT;
		name = c.getName();
	}

	public VisualEntry(Var var, int nr) {
		this.nr = nr;
		this.var = var;
		this.type = VisualEntryType.VARIABLE;
		name = var.getName();
	}

	public VisualEntry(Var[] vector, int nr) {
		this.nr = nr;
		this.vector = vector;
		this.type = VisualEntryType.VECTOR;
		name = "Var[]";
	}

	public VisualEntry(Var[][] matrix, int nr) {
		this.nr = nr;
		this.matrix = matrix;
		this.type = VisualEntryType.MATRIX;
		name = "Var[][]";
	}

	public int getNr() {
		return nr;
	}

	public VisualEntryType getType() {
		return type;
	}

	public Constraint getConstraint() {
		return constraint;
	}

	public Var getVariable() {
		return var;
	}

	public Var[] getVector() {
		return vector;
	}

	public Var[][] getMatrix() {
		return matrix;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
