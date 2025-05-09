/**
 *  HistoricHomes.java 
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
import JaCoP.constraints.Element;
import JaCoP.constraints.XeqC;
import JaCoP.constraints.XltY;
import JaCoP.constraints.XneqC;
import JaCoP.constraints.XneqY;
import JaCoP.constraints.XplusCeqZ;
import JaCoP.core.FDV;
import JaCoP.core.Store;
import JaCoP.core.Variable;

/**
 * 
 * @author Radoslaw Szymanek
 *
 * Each year the Glendale Women's Club sponsors a Historic Homes Tour in which 
 * five old houses are (with the owners' permission, of course) opened to the 
 * public. This year the five homes are on different streets (Azalea Drive, 
 * Crepe Myrtle Court, Jasmine Boulevard, Magnolia Street, and Oleander Road), 
 * and each was built in a different year (1860, 1870, 1890, 1900, and 1920). 
 * Can you give the order in which the tour visited the five homes (identifying 
 * them by street) and match each with its year?
 * 
 * 1. The home on Jasmine is 20 years older than the one on Azalea.
 * 2. The third home on the tour was built in 1860.
 * 3. The tour visited the home on Magnolia sometime before the one built in 1890.
 * 4. The tour visited the home on Oleander (which wasn't the last of the five to 
 * be built) sometime before it visited the one on Jasmine, which in turn was 
 * seen sometime before the one built in 1900.
 *
 * Determine: Order -- Street -- Year
 */


public class HistoricHomes extends Example {

	@Override
	public void model() {

		store = new Store();
		vars = new ArrayList<Variable>();
		
		System.out.println("Program to solve Historic Homes logic puzzle");

		String streetName[] = { "street Azalea Drive",
				"street Crepe Myrtle Court", "street Jasmine Boulevard",
				"street Magnolia Street", "street Oleander Road", };

		int iAzalea_Drive = 0, /* iCrepe_Myrtle_Court = 1, */ iJasmine_Boulevard = 2, 
			iMagnolia_Street = 3, iOleander_Road = 4;

		String orderName[] = { "1st", "2nd", "3rd", "4th", "5th" };
		
		int i1st = 0, i2nd = 1, i3rd = 2, i4th = 3, i5th = 4;

		FDV order[] = new FDV[5];
		FDV street[] = new FDV[5];

		for (int i = 0; i < 5; i++) {

			order[i] = new FDV(store, orderName[i]);
			order[i].addDom(1860, 1860);
			order[i].addDom(1870, 1870);
			order[i].addDom(1890, 1890);
			order[i].addDom(1900, 1900);
			order[i].addDom(1920, 1920);

			street[i] = new FDV(store, streetName[i]);
			street[i].addDom(1860, 1860);
			street[i].addDom(1870, 1870);
			street[i].addDom(1890, 1890);
			street[i].addDom(1900, 1900);
			street[i].addDom(1920, 1920);
			vars.add(order[i]); vars.add(street[i]);
		}

		store.impose(new Alldifferent(street));
		store.impose(new Alldifferent(order));

		// 1. The home on Jasmine is 20 years older than the one on Azalea.

		store.impose(new XplusCeqZ(street[iAzalea_Drive], -20,
				street[iJasmine_Boulevard]));

		// 2. The third home on the tour was built in 1860.

		store.impose(new XeqC(order[i3rd], 1860));

		// 3. The tour visited the home on Magnolia sometime before the one
		// built in 1890.

		// Position of MagnoliaStreet within order array determines its order.
		FDV index1 = new FDV(store, "index1", 1, 5);
		store.impose(new Element(index1, order, street[iMagnolia_Street]));
		// Position of value 1890 within order array determines its order.
		FDV index2 = new FDV(store, "index2", 1, 5);
		FDV value1890 = new FDV(store, "1890", 1890, 1890);
		store.impose(new Element(index2, order, value1890));

		store.impose(new XltY(index1, index2));

		// implied constraints
		store.impose(new XneqC(street[iMagnolia_Street], 1890));
		store.impose(new XneqC(order[i1st], 1890));
		store.impose(new XneqY(order[i5th], street[iMagnolia_Street]));

		// 4. The tour visited the home on Oleander (which wasn't the last of
		// the five to
		// be built) sometime before it visited the one on Jasmine, which in
		// turn was
		// seen sometime before the one built in 1900.

		store.impose(new XneqC(street[iOleander_Road], 1920));

		// Index 3 specifies the order for iOleander_Road
		FDV index3 = new FDV(store, "index3", 1, 5);
		store.impose(new Element(index3, order, street[iOleander_Road]));
		// Index 4 specifies the order for Jasmine buiding.
		FDV index4 = new FDV(store, "index4", 1, 5);
		store.impose(new Element(index4, order, street[iJasmine_Boulevard]));
		// index 2 specifies the order for building built at 1890.
		FDV index5 = new FDV(store, "index2", 1, 5);
		FDV value1900 = new FDV(store, "1900", 1900, 1900);
		store.impose(new Element(index5, order, value1900));

		store.impose(new XltY(index3, index4));
		store.impose(new XltY(index4, index5));

		// implied constraints.
		store.impose(new XneqY(street[iOleander_Road], order[i5th]));
		store.impose(new XneqY(street[iOleander_Road], order[i4th]));
		store.impose(new XneqC(street[iOleander_Road], 1900));

		store.impose(new XneqC(order[i1st], 1900));
		store.impose(new XneqC(order[i2nd], 1900));

		store.impose(new XneqY(order[i1st], street[iJasmine_Boulevard]));
		store.impose(new XneqY(order[i5th], street[iJasmine_Boulevard]));

		vars.add(index1);
		vars.add(index2);
		vars.add(index3);
		vars.add(index4);		
		
	}
	
	
	/**
	 * It executes the program to solve this simple logic puzzle.
	 * @param args
	 */
	public static void main(String args[]) {

		HistoricHomes example = new HistoricHomes();
		
		example.model();

		if (example.search())
			System.out.println("Solution(s) found");
		
	}		

}
