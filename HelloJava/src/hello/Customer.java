package hello;

import java.util.Iterator;
import java.util.List;

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
	protected List<Address> addresses;

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
	
	public List<Address> getAddresses() {
		return addresses;
	}
	public void setAddresses(List<Address> addresses) {
		this.addresses = addresses;
	}
	
	public boolean addressesContainStates(String[] states) {
		Iterator<Address> iter = addresses.iterator();
		while(iter.hasNext()) {
			Address address = iter.next();
			String state = address.getState();
			if (state != null)
			for (int i = 0; i < states.length; i++) {
				if (state.equals(states[i]))
					return true;
			}
		}
		return false;
	}
	
}
