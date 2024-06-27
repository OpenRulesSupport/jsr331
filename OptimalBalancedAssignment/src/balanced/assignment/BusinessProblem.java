package balanced.assignment;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import com.openrules.ruleengine.Decision;

public class BusinessProblem {

	Person[] people; 
	ArrayList<PersonSimilarity> similarities;
	
	int 				numberOfGroups;
	int 				groupMin;
	int 				groupMax;
	Group[]				groups;
	ArrayList<Set<String>>		categorySets;
	
	public BusinessProblem() {
		groups = null;
		people = null;
		similarities = new ArrayList<PersonSimilarity>();
		categorySets = new ArrayList<Set<String>>(Person.categories.length);
		for (int i = 0; i < Person.categories.length; i++) {
			Set<String> set = new HashSet<String>();
			categorySets.add(set);
		}
	}
	
	public void initialize(Person[] people) {
		setPeople(people);
		for (int i = 0; i < people.length; i++) {
			people[i].setNumber(i);
		}
//		setCategoryInstanceSets();
		getGroups();
		System.out.println(this);
		validate();
	}
	
	public boolean validate() {
		if (people.length < numberOfGroups*groupMin)
			throw new RuntimeException("Number of people < numberOfGroups*groupMin");
		if (people.length > numberOfGroups*groupMax)
			throw new RuntimeException("Number of people > numberOfGroups*groupMax");
		return true;
	}
	
	public int getNumberOfGroups() {
		return numberOfGroups;
	}
	public void setNumberOfGroups(int numberOfGroups) {
		this.numberOfGroups = numberOfGroups;
	}
	public int getGroupMin() {
		return groupMin;
	}
	public void setGroupMin(int groupMin) {
		this.groupMin = groupMin;
	}
	public int getGroupMax() {
		return groupMax;
	}
	public void setGroupMax(int groupMax) {
		this.groupMax = groupMax;
	}
	
	public Person[] getPeople() {
		return people;
	}
	public void setPeople(Person[] people) {
		this.people = people;
	}
	
	public ArrayList<PersonSimilarity> getSimilarities() {
		return similarities;
	}

	public void setSimilarities(ArrayList<PersonSimilarity> similarities) {
		this.similarities = similarities;
	}
	
	public ArrayList<PersonSimilarity> defineSimilarities(String filename, String decisionName) {
		// define sameness for all pairs of people
		Decision decision = new Decision(decisionName,filename);
		decision.put("FEEL", "On");
		decision.put("trace", "off");
		int n = 0;
		similarities = new ArrayList<PersonSimilarity>();
		for (int p1 = 0; p1 < people.length-1; p1++) {
			for (int p2 = p1+1; p2 < people.length; p2++) {
				PersonSimilarity similarity = new PersonSimilarity();
				similarity.person1 = people[p1];
				similarity.person2 = people[p2];
				decision.put("PersonSimilarity", similarity);
				decision.execute();
				if (similarity.sameness > 0) {
					similarities.add(similarity);
					n++;
					if (n%1000 == 0)
						System.out.println("Defined " + n + " similarities...");
				}
			}
		}
		System.out.println("Defined " + similarities.size() + " similarities.");
		return similarities;
	}

	public Group[] getGroups() {
		if (groups == null) {
			groups = new Group[numberOfGroups];
			for (int i = 0; i < groups.length; i++) {
				groups[i] = new Group(i,people);
			}
		}
		return groups;
	}

	public void setGroups(Group[] groups) {
		this.groups = groups;
	}
	
	public void setCategoryInstanceSets() {
		for (Person p : people) {
			for (int c = 0; c < Person.categories.length; c++) {
				Set<String> set = categorySets.get(c);
				switch (c) {
				case 0:
					set.add(p.getDepartment());
					break;
				case 1:
					set.add(p.getLocation());
					break;
				case 2:
					set.add(p.getGender());
					break;
				case 3:
					set.add(p.getTitle());
					break;
				default:
					throw new RuntimeException("Impossible category number: " + c);
				}
			}
		}
		showCategoryInstanceSets();
	}
	
	public void showCategoryInstanceSets() {
		for (int c = 0; c < Person.categories.length; c++) {
			System.out.println("Category: "+ Person.categories[c]);
			for(String categoryInstance : categorySets.get(c)) {
				System.out.println("\tCategoryInstance: "+categoryInstance 
						+ " " + getNumberOfPeopleInCategoryInstance(c,categoryInstance)+" people");
			}
		}
	}
	
	public int getNumberOfPeopleInCategoryInstance(int category, String categoryInstance) {
		Set<String> set = categorySets.get(category);
		int result = 0;
		for (Person person : people) {
			if (person.belongsToCategory(category, categoryInstance))
				result++;
		}
		return result;
	}
	
	public Set<String> getCategoryInstances(int categoryNumber) {
		return categorySets.get(categoryNumber);
	}
	
	public void showSolution() {
		System.out.println("\nASSIGNED GROUPS:");
		int totalPenalty = 0;
		for(Group group : groups) {
			System.out.println(group);
			totalPenalty += group.getPenalty();
//			System.out.println("GROUP "+ group.getId() + " size=" + group.getSize());
//			int n = 1;
//			for (Person person : people) {
//				if (person.getGroup() == group.getId()) {
//					System.out.println("\t" + n + " "+ person);
//					n++;
//				}
//			}
		}
		System.out.println("Total Penalty: " + totalPenalty);
	}
	
//	public int maxSizeOfAnyCategorySet() {
//		int max = 0;
//		for (int c = 0; c < Person.categories.length; c++) {
//			int size = getCategoryInstances(c).size();
//			if (size > max) 
//				max = size;
//		}
//		return max;
//	}
//	public int minSizeOfAnyCategorySet() {
//		int min = people.length;
//		for (int c = 0; c < Person.categories.length; c++) {
//			int size = getCategoryInstances(c).size();
//			if (size < min) 
//				min = size;
//		}
//		return min;
//	}
	

	@Override
	public String toString() {
		return "BusinessProblem [numberOfPeople=" + people.length + 
				" numberOfGroups=" + numberOfGroups + ", groupMin=" + groupMin + ", groupMax=" + groupMax
				+ "]";
	}


}
