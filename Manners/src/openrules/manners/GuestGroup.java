package openrules.manners;

public class GuestGroup {
	Guest[] allGuests;
	int[] guests;
	int numberOfGuests;

	public GuestGroup(Guest[] allGuests, int guestIndex) {
//		System.out.println("Create GuestGroup " + guestIndex);
		this.allGuests = allGuests;
		guests = new int[allGuests.length];
		numberOfGuests = 0;
		add(guestIndex);
	}

	public void add(int g) {
		guests[numberOfGuests++] = g;
	}

	public int getNumberOfGuests() {
		return numberOfGuests;
	}

	boolean shouldGuestBelongToTheGroup(int g) {
		Guest guest = allGuests[g];
		for (int i = 0; i < numberOfGuests; i++) {
			Guest gi = allGuests[guests[i]];
			if (guest != gi &&
				guest.sameGenderAs(gi) && guest.sameHobbiesAs(gi))
				return true;
		}
		return false;
	}

	public int[] getGuests() {
		return guests;
	}

	@Override
	public String toString() {
		StringBuffer buf = new StringBuffer();
		buf.append("GuestGroup: ");
		for (int i = 0; i < numberOfGuests; i++) {
			buf.append(" "+allGuests[guests[i]].getName());
		}
		return buf.toString();
	}
}
