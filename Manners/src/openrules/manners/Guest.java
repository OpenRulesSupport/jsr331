package openrules.manners;

public class Guest {

	String name;
	String gender;
	int[] hobbies;
	int seat;

	public Guest() {
		seat = -1;
	}

	// input data
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}
	public int[] getHobbies() {
		return hobbies;
	}
	public void setHobbies(int[] hobbies) {
		this.hobbies = hobbies;
	}

	public int getSeat() {
		return seat;
	}
	public void setSeat(int seat) {
		this.seat = seat;
	}

	@Override
	public String toString() {
		StringBuffer buf = new StringBuffer();
		//buf.append("Guest " + name + " seats at the chair " + seat);
		buf.append(" Guest " + name + "\tGender="+gender + " Hobbies:");
		for (int i = 0; i < hobbies.length; i++) {
			buf.append(" " + hobbies[i]);
		}
		return buf.toString();
	}

	public boolean sameGenderAs(Guest g) {
		return this.getGender().equals(g.getGender());
	}

	public boolean sameHobbiesAs(Guest g) {
		int[] h1 = this.getHobbies();
		int[] h2 = g.getHobbies();
		if (h1.length != h2.length)
			return false;
		for (int i = 0; i < h1.length; i++) {
			boolean found = false;
			for (int j = 0; j < h2.length; j++) {
				if (h2[j] == h1[i]) {
					found = true;
					break;
				}
			}
			if (!found)
				return false;
		}
		return true;
	}

	public int commonHobbiesWith(Guest g) {
		int[] h1 = this.getHobbies();
		int[] h2 = g.getHobbies();
		int common = 0;
		for (int i = 0; i < h1.length; i++) {
			for (int j = 0; j < h2.length; j++) {
				if (h2[j] == h1[i]) {
					common++;
					break;
				}
			}
		}
		return common;
	}
}
