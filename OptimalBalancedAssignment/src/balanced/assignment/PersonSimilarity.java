package balanced.assignment;

public class PersonSimilarity {
	
	Person	person1;
	Person	person2;
	int		sameness;
	
	public Person getPerson1() {
		return person1;
	}
	public void setPerson1(Person person1) {
		this.person1 = person1;
	}
	public Person getPerson2() {
		return person2;
	}
	public void setPerson2(Person person2) {
		this.person2 = person2;
	}
	public int getSameness() {
		return sameness;
	}
	public void setSameness(int sameness) {
		this.sameness = sameness;
	}
	@Override
	public String toString() {
		return "Similarity [person1=" + person1.number + ", person2=" + person2.number + ", sameness=" + sameness + "]";
	}
	
	

}
