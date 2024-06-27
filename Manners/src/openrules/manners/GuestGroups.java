package openrules.manners;

import java.util.Vector;


public class GuestGroups {

	Guest[] allGuests;
	Vector<GuestGroup> groups;
	int index;

	public GuestGroups(Guest[] allGuests) {
		this.allGuests = allGuests;
		index = 0;
		groups = new Vector<GuestGroup>();
		groups.add(new GuestGroup(allGuests,0));
		for (int guest = 1; guest <allGuests.length; guest++) {
			boolean within = false;
			for (int j = 0; j < groups.size(); j++) {
				GuestGroup group = groups.elementAt(j);
				if (group.shouldGuestBelongToTheGroup(guest)) {
					group.add(guest);
					within = true;
					break;
				}
			}
			if (!within)
				groups.add(new GuestGroup(allGuests,guest));
		}
	}

	public GuestGroup next() {
		if (index < groups.size()) {
			GuestGroup group = groups.elementAt(index);
			index++;
			return group;
		}
		else {
			index = 0;
			return null;
		}
	}

}
