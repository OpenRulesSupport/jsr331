/**
 *  SleepingArrangements.java 
 *  This file is part of JaCoP.
 *
 *  JaCoP is a Java Constraint Programming solver. 
 *	
 *	Copyright (C) 2000-2008 Krzysztof Kuchcinski and Radoslaw Szymanek
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *  
 *  Notwithstanding any other provision of this License, the copyright
 *  owners of this work supplement the terms of this License with terms
 *  prohibiting misrepresentation of the origin of this work and requiring
 *  that modified versions of this work be marked in reasonable ways as
 *  different from the original version. This supplement of the license
 *  terms is in accordance with Section 7 of GNU Affero General Public
 *  License version 3.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package ExamplesJaCoP;

import java.util.ArrayList;

import JaCoP.constraints.Alldifferent;
import JaCoP.constraints.Distance;
import JaCoP.constraints.Element;
import JaCoP.constraints.ExtensionalSupportVA;
import JaCoP.constraints.XgtY;
import JaCoP.constraints.XltY;
import JaCoP.constraints.XneqY;
import JaCoP.constraints.XplusCeqZ;
import JaCoP.constraints.XplusYeqZ;
import JaCoP.core.FDV;
import JaCoP.core.Store;
import JaCoP.core.Variable;

/**
 * 
 * @author Radoslaw Szymanek
 *
 * The Dillies have five teenaged children, two boys named Ollie and
 * Rollie, and three girls named Mellie, Nellie, and Pollie. Each is a
 * different number of years old, from 13 to 17. There are three bedrooms
 * for the children in the Dillie house, so two share the yellow room,
 * two share the white room, and one alone has the smaller green
 * room. Can you match each one's name and age, and tell who sleeps
 * where?
 * 
 * 1. No one shares a room with a sibling of the opposite sex.
 *
 * 2. Pollie is exactly one year older than Mellie.
 * 
 * 3. The two teenagers who share the yellow room are two years apart in age.
 *
 * 4. The two who share the white room are three years apart in age.
 * 
 * 5. Rollie is somewhat older than Ollie, but somewhat younger than the
 * sibling who has the green room.
 * 
 * Determine: Child -- Age -- Room
 * 
 * Given solution : 
 * 
 * Mellie, 16, green room
 * Nellie, 14, white room
 * Ollie, 13, yellow room
 * Pollie, 17, white room
 * Rollie, 15, yellow room
 * 
 *
 */

public class SleepingArrangements extends Example {

	@Override
	public void model() {

		store = new Store();
		vars = new ArrayList<Variable>();

		String nameID[] = { "Ollie", "Rollie", "Mellie", "Nellie", "Pollie" };
		int iOllie = 0, iRollie = 1, iMellie = 2, iNellie = 3, iPollie = 4;

		String roomID[] = { "Yellow1", "Yellow2", "White1", "White2", "Green" };
		int iYellow1 = 0, iYellow2 = 1, iWhite1 = 2, iWhite2 = 3, iGreen = 4;

		FDV name[] = new FDV[5];
		FDV room[] = new FDV[5];

		for (int i = 0; i < 5; i++) {
			// The most complex clue to express will be clue no. 1 since the
			// domains of name and room variables denote the age of the person.
			// However, we gain by making other clues easier to express.
			name[i] = new FDV(store, nameID[i], 13, 17);
			room[i] = new FDV(store, roomID[i], 13, 17);
			vars.add(name[i]); vars.add(room[i]);
		}

		// Each person has to have a different age.
		store.impose(new Alldifferent(name));
		store.impose(new Alldifferent(room));

		// 1. No one shares a room with a sibling of the opposite sex.

		// Room no and position in room array
		int[][] roomNoGivenPosition = { { 1, 1 }, { 1, 2 }, { 2, 3 }, { 2, 4 },
				{ 3, 5 } };

		FDV olliePos = new FDV(store, "ollieRoomPosition", 1, 5);
		// ollie room position is obtained through room array
		store.impose(new Element(olliePos, room, name[iOllie]));
		FDV ollieRoomNo = new FDV(store, "ollieRoomNo", 1, 3);
		FDV[] ollie = { ollieRoomNo, olliePos };
		// ollie room number is obtained based on ollie position
		store.impose(new ExtensionalSupportVA(ollie, roomNoGivenPosition));

		FDV rolliePos = new FDV(store, "rollieRoomPosition", 1, 5);
		store.impose(new Element(rolliePos, room, name[iRollie]));
		FDV rollieRoomNo = new FDV(store, "rollieRoomNo", 1, 3);
		FDV[] rollie = { rollieRoomNo, rolliePos };
		store.impose(new ExtensionalSupportVA(rollie, roomNoGivenPosition));

		FDV melliePos = new FDV(store, "mellieRoomPosition", 1, 5);
		store.impose(new Element(melliePos, room, name[iMellie]));
		FDV mellieRoomNo = new FDV(store, "mellieRoomNo", 1, 3);
		FDV[] mellie = { mellieRoomNo, melliePos };
		store.impose(new ExtensionalSupportVA(mellie, roomNoGivenPosition));

		FDV nelliePos = new FDV(store, "nellieRoomPosition", 1, 5);
		store.impose(new Element(nelliePos, room, name[iNellie]));
		FDV nellieRoomNo = new FDV(store, "nellieRoomNo", 1, 3);
		FDV[] nellie = { nellieRoomNo, nelliePos };
		store.impose(new ExtensionalSupportVA(nellie, roomNoGivenPosition));

		FDV polliePos = new FDV(store, "pollieRoomPosition", 1, 5);
		store.impose(new Element(polliePos, room, name[iPollie]));
		FDV pollieRoomNo = new FDV(store, "pollieRoomNo", 1, 3);
		FDV[] pollie = { pollieRoomNo, polliePos };
		store.impose(new ExtensionalSupportVA(pollie, roomNoGivenPosition));

		store.impose(new XneqY(ollieRoomNo, mellieRoomNo));
		store.impose(new XneqY(ollieRoomNo, nellieRoomNo));
		store.impose(new XneqY(ollieRoomNo, pollieRoomNo));
		store.impose(new XneqY(rollieRoomNo, mellieRoomNo));
		store.impose(new XneqY(rollieRoomNo, nellieRoomNo));
		store.impose(new XneqY(rollieRoomNo, pollieRoomNo));

		// 2. Pollie is exactly one year older than Mellie.

		store.impose(new XplusCeqZ(name[iMellie], 1, name[iPollie]));

		// 3. The two teenagers who share the yellow room are two years apart in
		// age.

		FDV two = new FDV(store, "2", 2, 2);
		store.impose(new Distance(room[iYellow1], room[iYellow2], two));

		// 4. The two who share the white room are three years apart in age.

		FDV auxilary = new FDV(store, "3apart");
		auxilary.addDom(-3, -3);
		auxilary.addDom(3, 3);

		vars.add(auxilary);
		
		store.impose(new XplusYeqZ(room[iWhite1], auxilary, room[iWhite2]));

		// 5. Rollie is somewhat older than Ollie, but somewhat younger than the
		// sibling who has the green room.

		store.impose(new XgtY(name[iRollie], name[iOllie]));
		store.impose(new XltY(name[iRollie], room[iGreen]));

	}

	/**
	 * It executes a program to solve this simple logic puzzle.
	 * @param args no arguments are used.
	 */
	public static void main(String args[]) {

		SleepingArrangements example = new SleepingArrangements();
		
		example.model();

		if (example.search())
			System.out.println("Solution(s) found");
		
	}		
	
	

}