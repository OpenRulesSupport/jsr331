package hello;

/**
 * @author Jacob
 *
 * A simple Java class Customer to be greeted from OpenRules
 */
public class Customer 
{
	protected String   name;
	protected String   maritalStatus;
	protected String   gender;
	protected int      age;
	protected Customer spouse;

	public String getGender() {
		return gender;
	}
	public String getMaritalStatus() {
		return maritalStatus;
	}
	public String getName() {
		return name;
	}
	public Customer getSpouse() {
		return spouse;
	}
	public void setGender(String string) {
		gender = string;
	}
	public void setMaritalStatus(String string) {
		maritalStatus = string;
	}
	public void setName(String string) {
		name = string;
	}
	public void setSpouse(Customer customer) {
		spouse = customer;
	}

	public int getAge() {
		return age;
	}
	public void setAge(int age) {
		this.age = age;
	}
}
