package hello;

import java.util.Iterator;
import java.util.List;

public class Customer 
{
	String   name
	String   maritalStatus
	String   gender
	int      age
	Customer spouse
	List<Address> addresses

	boolean addressesContainStates(String[] states) {
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
