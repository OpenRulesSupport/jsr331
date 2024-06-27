/**
 *  Tunapalooza.java 
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
import JaCoP.constraints.And;
import JaCoP.constraints.Or;
import JaCoP.constraints.Reified;
import JaCoP.constraints.Sum;
import JaCoP.constraints.XeqC;
import JaCoP.constraints.XeqY;
import JaCoP.constraints.XneqC;
import JaCoP.constraints.XneqY;
import JaCoP.core.FDV;
import JaCoP.core.Store;
import JaCoP.core.Variable;

/**
 * @author Lesniak Kamil, Harezlak Roman, Radoslaw Szymanek
 * @version 2.4
 * 
 * Tim and Keri have a full day ahead for themselves as they plan to see
 * and hear everything at Tunapalooza '98, the annual save-the-tuna
 * benefit concert in their hometown. To cover the most ground, they
 * will have to split up.  They have arranged to meet during four rock
 * band acts (Ellyfish, Korrupt, Retread Ed and the Flat Tires, and
 * Yellow Reef) at planned rendezvous points (carnival games,
 * information booth, mosh pit, or T-shirt vendor).  Can you help match
 * each band name with the type of music they play (country, grunge,
 * reggae, or speed metal) and Tim and Kerri's prearranged meeting spot
 * while they play?
 * 
 * 1. Korrupt isn't a country or grunge music band. 
 * 
 * 2. Tim and Kerri won't meet at the carnival games during Ellyfish's performance.
 *
 * 3. The pair won't meet at the T-shirt vendor during the reggae band's show.
 *
 * 4. Exactly two of the following three statements are true:
 * a) Ellyfish plays grunge music.
 * b) Tim and Kerri won't meet at the information booth during a performance by Retread Ed and the Flat Tires.
 * c) The two friends won't meet at the T-shirt vendor while Yellow Reef is playing.
 *
 * 5. The country and speed metal acts are, in some order, Retread Ed and the Flat Tires 
 * and the act during which Tim and Kerri will meet at the mosh pit.
 *
 * 6. The reggae band is neither Korrupt nor the act during which Tim and 
 * Kerri will meet at the information booth.
 *
 * Determine: Band name -- Music type -- Meeting place
 *
 * Given solution : 
 *
 * 1 Ellyfish, grunge,  vendor
 * 2 Korrupt,  metal,   mosh
 * 3 Retread,  country, information 
 * 4 Yellow ,  reggae,  carnival
 * 
 */

public class Tunapalooza extends Example {

	@Override
	public void model() {

		// Creating constraint store
		store = new Store();
		vars = new ArrayList<Variable>();
		
		// names
		int Ellyfish = 1, Korrupt = 2, Retread = 3, Yellow = 4;

		// types
		FDV country = new FDV(store, "country", 1, 4);
		FDV grunge = new FDV(store, "grunge", 1, 4);
		FDV reggae = new FDV(store, "reggae", 1, 4);
		FDV metal = new FDV(store, "metal", 1, 4);
		// places
		FDV carnival = new FDV(store, "carnival", 1, 4);
		FDV information = new FDV(store, "information", 1, 4);
		FDV mosh = new FDV(store, "mosh", 1, 4);
		FDV vendor = new FDV(store, "vendor", 1, 4);

		// arrays of variables
		FDV types[] = { country, grunge, reggae, metal };
		FDV places[] = { carnival, information, mosh, vendor };

		for (Variable v : types)
			vars.add(v);
		for (Variable v : places)
			vars.add(v);
		
		// All types and places have to be associated with different band.
		store.impose(new Alldifferent(types));
		store.impose(new Alldifferent(places));

		// 1. Korrupt isn't a country or grunge music band.

		store.impose(new And(new XneqC(country, Korrupt), new XneqC(grunge,
				Korrupt)));

		// 2. Tim and Kerri won't meet at the carnival games during Ellyfish's
		// performance.

		store.impose(new XneqC(carnival, Ellyfish));

		// 3. The pair won't meet at the T-shirt vendor during the reggae band's
		// show.

		store.impose(new XneqY(vendor, reggae));

		// 4. Exactly two of the following three statements are true:
		// a) Ellyfish plays grunge music.
		// b) Tim and Kerri won't meet at the information booth during a
		// performance by Retread Ed and the Flat Tires.
		// c) The two friends won't meet at the T-shirt vendor while Yellow Reef
		// is playing.

		FDV statement1 = new FDV(store, "s1", 0, 1);
		FDV statement2 = new FDV(store, "s2", 0, 1);
		FDV statement3 = new FDV(store, "s3", 0, 1);

		store.impose(new Reified(new XeqC(grunge, Ellyfish), statement1));
		store.impose(new Reified(new XneqC(information, Retread), statement2));
		store.impose(new Reified(new XneqC(vendor, Yellow), statement3));

		FDV two = new FDV(store, "2", 2, 2);
		FDV sum[] = { statement1, statement2, statement3 };
		store.impose(new Sum(sum, two));

		for (Variable v : sum)
			vars.add(v);
		
		// 5. The country and speed metal acts are, in some order, Retread Ed
		// and the Flat Tires
		// and the act during which Tim and Kerri will meet at the mosh pit.

		store.impose(new Or(new XeqY(country, mosh), new XeqY(metal, mosh)));
		store.impose(new Or(new XeqC(country, Retread),
				new XeqC(metal, Retread)));
		store.impose(new XneqC(mosh, Retread));

		// 6. The reggae band is neither Korrupt nor the act during which Tim
		// and
		// Kerri will meet at the information booth.

		store.impose(new XneqC(reggae, Korrupt));
		store.impose(new XneqY(reggae, information));

	}
		
	/**
	 * It executes the program to solve this simple logic puzzle.
	 * @param args no arguments are used.
	 */
	public static void main(String args[]) {

		Tunapalooza example = new Tunapalooza();
		
		example.model();

		if (example.searchMostConstrainedStatic())
			System.out.println("Solution(s) found");
		
	}		
	
}
