package balanced.assignment;

public class Person {
	
	public static String[] categories = {"department", "location", "gender", "title"};
	String	name;
	String	department;
	String	location;
	String	gender;
	String	title;
	int 	number;
	int		group;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
		group = -1;
	}
	
	public int getNumber() {
		return number;
	}
	public void setNumber(int number) {
		this.number = number;
	}
	public boolean belongsToCategory(int categoryNumber, String categoryInstance) {
		switch (categoryNumber) {
		case 0:
			if (categoryInstance.equals(department))
				return true;
			return false;
		case 1:
			if (categoryInstance.equals(location))
				return true;
			return false;
		case 2:
			if (categoryInstance.equals(gender))
				return true;
			return false;
		case 3:
			if (categoryInstance.equals(title))
				return true;
			return false;
		default:
			return false;
		}
	}
	
	
	public String getDepartment() {
		return department;
	}
	public void setDepartment(String department) {
		this.department = department;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	
	public int getGroup() {
		return group;
	}
	public void setGroup(int group) {
		this.group = group;
	}
	@Override
	public String toString() {
		return "Person [name=" + name + ", department=" + department + ", location=" + location + ", gender=" + gender
				+ ", title=" + title 
				+ ", group=" + group
				+ ", number=" + number
				+ "]";
	}
	
	


}
