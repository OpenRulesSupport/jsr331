/**
 *  BuildingBlocks.java 
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
import JaCoP.constraints.Cumulative;
import JaCoP.core.FDV;
import JaCoP.core.FDstore;
import JaCoP.core.Variable;

/**
 * @author Krzysztof "Vrbl" Wrobel and Radoslaw Szymanek
 *
 * Each of four alphabet blocks has a single letter of the alphabet on
 * each of its six sides.  In all, the four blocks contain every letter
 * but Q and Z.  By arranging the blocks in various ways, you can spell
 * all of the words listed below.  Can you figure out how the letters are
 * arranged on the four blocks?
 *
 * BAKE ONYX ECHO OVAL 
 *
 * GIRD SMUG JUMP TORN 
 *
 * LUCK VINY LUSH WRAP 
 */

public class BuildingBlocks extends Example {

	@Override
	public void model() {

		vars = new ArrayList<Variable>();
		store = new FDstore();

		System.out.println("Building Blocks");

		FDV A = new FDV(store, "A", 1, 4);
		FDV B = new FDV(store, "B", 1, 4);
		FDV C = new FDV(store, "C", 1, 4);
		FDV D = new FDV(store, "D", 1, 4);
		FDV E = new FDV(store, "E", 1, 4);
		FDV F = new FDV(store, "F", 1, 4);
		FDV G = new FDV(store, "G", 1, 4);
		FDV H = new FDV(store, "H", 1, 4);
		FDV I = new FDV(store, "I", 1, 4);
		FDV J = new FDV(store, "J", 1, 4);
		FDV K = new FDV(store, "K", 1, 4);
		FDV L = new FDV(store, "L", 1, 4);
		FDV M = new FDV(store, "M", 1, 4);
		FDV N = new FDV(store, "N", 1, 4);
		FDV O = new FDV(store, "O", 1, 4);
		FDV P = new FDV(store, "P", 1, 4);
		FDV R = new FDV(store, "R", 1, 4);
		FDV S = new FDV(store, "S", 1, 4);
		FDV T = new FDV(store, "T", 1, 4);
		FDV U = new FDV(store, "U", 1, 4);
		FDV W = new FDV(store, "W", 1, 4);
		FDV V = new FDV(store, "V", 1, 4);
		FDV X = new FDV(store, "X", 1, 4);
		FDV Y = new FDV(store, "Y", 1, 4);

		// array of letters.
		FDV letters[] = { A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, R, S,
						  T, U, W, V, X, Y };

		for (FDV v : letters)
			vars.add(v);

		// First word, each letter on a different block.
		FDV bake[] = { B, A, K, E };
		store.impose(new Alldifferent(bake));

		FDV onyx[] = { O, N, Y, X };
		store.impose(new Alldifferent(onyx));

		FDV echo[] = { E, C, H, O };
		store.impose(new Alldifferent(echo));

		FDV oval[] = { O, V, A, L };
		store.impose(new Alldifferent(oval));

		FDV grid[] = { G, R, I, D };
		store.impose(new Alldifferent(grid));

		FDV smug[] = { S, M, U, G };
		store.impose(new Alldifferent(smug));

		FDV jump[] = { J, U, M, P };
		store.impose(new Alldifferent(jump));

		FDV torn[] = { T, O, R, N };
		store.impose(new Alldifferent(torn));

		FDV luck[] = { L, U, C, K };
		store.impose(new Alldifferent(luck));

		FDV viny[] = { V, I, N, Y };
		store.impose(new Alldifferent(viny));

		FDV lush[] = { L, U, S, H };
		store.impose(new Alldifferent(lush));

		FDV wrap[] = { W, R, A, P };
		store.impose(new Alldifferent(wrap));

		// auxilary variables
		FDV one = new FDV(store, "one", 1, 1);
		FDV six = new FDV(store, "six", 6, 6);

		FDV ones[] = new FDV[24];
		for (int i = 0; i < 24; i++)
			ones[i] = one;

		// Each block can not contain more than six letters.
		store.impose(new Cumulative(letters, ones, ones, six));

		// Letters decode the start time (block number).
		// Duration, each letter is only on one block (duration 1).
		// Resource, each letter takes only one space (usage 1).
		// Limit, all blocks can accommodate 6 letters.

	}
		
	/**
	 * It executes the program to solve this logic puzzle.
	 * @param args
	 */
	public static void main(String args[]) {

		BuildingBlocks example = new BuildingBlocks();
		
		example.model();

		if (example.searchSmallestDomain(false))
			System.out.println("Solution(s) found");
	}	
	
}
