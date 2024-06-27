package openrules.manners;

import com.openrules.ruleengine.OpenRulesEngine;

/*
 * “Miss Manners” is a notorious benchmark for rule engines.
 * The problem is to find an acceptable seating arrangement
 * for allGuests at a dinner party.  It should match people with
 * the same hobbies, and to seat everyone next to a member of
 * the opposite sex.
 *
 */
public class MannersSolverGreedy {

	Guest[] guests;
	static int GENDER_SCORE = 10;
	static int HOBBY_SCORE = 1;

	public MannersSolverGreedy(Guest[] guests) {
		this.guests = guests;
	}

	Guest findGuestToSeatNextTo(Guest prevGuest) {
		if (prevGuest==null)
			return guests[0];
		int maxScore = -1;
		Guest maxGuest = null;
		for (int g = 0; g < guests.length; g++) {
			if (guests[g].seat >= 0)
				continue;
			int score = 0;
			if (!guests[g].sameGenderAs(prevGuest))
				score += GENDER_SCORE;
			int commonHobbies = prevGuest.commonHobbiesWith(guests[g]);
			score += HOBBY_SCORE * commonHobbies;
			if (score > maxScore) {
				maxScore = score;
				maxGuest = guests[g];
				//System.out.print(" Score=" + maxScore);
			}
		}
		if (maxScore < (GENDER_SCORE+HOBBY_SCORE))
			System.out.println("\t\tViolation: max score=" + maxScore);
		return maxGuest;
	}

	public static void main(String[] args)
	{
		OpenRulesEngine engine = new OpenRulesEngine("file:rules/Manners.xls");
		String size = "16";
		Guest[] guests = (Guest[]) engine.run("getGuests"+size + "bad");
		long start = System.currentTimeMillis();
		MannersSolverGreedy solver = new MannersSolverGreedy(guests);

		Guest prevGuest = null;
		for (int seat = 0; seat < guests.length; seat++) {
			Guest guest = solver.findGuestToSeatNextTo(prevGuest);
			guest.seat = seat;
			System.out.println("Seat " + (seat+1) + ": " + guest);
			prevGuest = guest;
		}
		System.out.println("Total elapsed time: " + (System.currentTimeMillis() - start) + " ms");
	}

}
