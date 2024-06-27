package balanced.assignment;

import java.util.Arrays;

public class Group {
	
	int id;	
	Person[] people;
	int size;
	int penalty;
	
	public Group(int id, Person[] people) {
		this.id = id;
		this.people = people;
		size = 0;
		penalty = 0;
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public Person[] getPeople() {
		return people;
	}
	
	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public int getPenalty() {
		return penalty;
	}

	public void setPenalty(int penalty) {
		this.penalty = penalty;
	}

	@Override
	public String toString() {
		StringBuffer buf = new StringBuffer();
		buf.append("Group [id=" + id + ", size=" + size + " penalty=" + penalty + "]");
		for (int i = 0; i < people.length; i++) {
			if (people[i].getGroup() == id)
				buf.append("\n\t"+people[i]);
		}
		return buf.toString();
	}

}
