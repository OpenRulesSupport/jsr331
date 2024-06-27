/**
 *  ProAndCon.java 
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
import JaCoP.constraints.Reified;
import JaCoP.constraints.Sum;
import JaCoP.constraints.SumWeight;
import JaCoP.constraints.XeqC;
import JaCoP.constraints.XeqY;
import JaCoP.constraints.XlteqC;
import JaCoP.constraints.XmulCeqZ;
import JaCoP.constraints.XplusCeqZ;
import JaCoP.constraints.XplusYeqZ;
import JaCoP.constraints.XplusYlteqZ;
import JaCoP.core.FDV;
import JaCoP.core.Store;
import JaCoP.core.Variable;

/**
 * 
 *
 * @author Romam Gawelek, Marcin Kazmierczak, Radoslaw Szymanek
 *
 * Logic Puzzle - Pro and Con
 * Author: Monica Tenniel
 * Publication: Dell Logic Puzzles
 * Issue: April, 1998
 * Page: 12
 * Stars: 2
 * At the last meeting of the local city council, each member 
 * (Mr. Akerman, Ms. Baird, Mr. Chatham, Ms. Duval, and Mr. Etting) 
 * had to vote on five motions, number 1 to 5 in the clues below. 
 * Can you discover how each one voted on each motion?
 * 
 * 
 * Note: a motion may have received zero or one yes vote, even though 
 * in real life it's unlikely that both the maker and seconder of 
 * the motion would change their minds before the motion came up for a vote. 
 * Each member voted either yes or no on each motion; no one abstained 
 * from voting on any motion.
 * 
 * Voting Chart
 * (view with non-proportional fonts)
 *
 *               1     2     3     4     5  
 * Mr. Akerman
 * Ms. Baird
 * Mr. Chatham
 * Ms. Duval
 * Mr. Etting

 * 1. Each motion got a different number of yes votes.
 * 2. In all, the five motions got three more yes votes than no votes.
 * 3. No two council members voted the same way on all five motions.
 * 4. The two women disagreed in their voting more often than they agreed.
 * 5. Mr. Chatham never made two yes votes on consecutive motions.
 * 6. Mr. Akerman and Ms. Baird both voted in favor of motion 4.
 * 7. Motion 1 received two more yes votes than motion 2 did.
 * 8. Motion 3 received twice as many yes votes as motion 4 did.
 *
 * Determine: fill in the chart (Yes/No) for each motion
 * 
 */

public class ProAndCon extends Example {

	@Override
	public void model() {

		store = new Store();
		vars = new ArrayList<Variable>();
		
		String surname[] = { "Mr. Akerman", "Ms. Baird", "Mr. Chatham",
				"Ms. Duval", "Mr. Etting" };

		int iAkerman = 0, iBaird = 1, iChatham = 2, iDuval = 3, iEtting = 4;
		int iMotion1 = 0, iMotion2 = 1, iMotion3 = 2, iMotion4 = 3, iMotion5 = 4;

		// Votes are encoded as two dimensional array - one index for person,
		// one index for group
		FDV vote[][] = new FDV[5][5];
		// Sums votes for each group
		FDV sum4Group[] = new FDV[5];

		for (int i = 0; i < 5; i++) {
			// Each motion (group of votes) has no of yes votes associated.
			sum4Group[i] = new FDV(store, "Sum4Group[" + (i + 1) + "]", 0, 5);
			vars.add(sum4Group[i]);
			
			// 0 - vote no , 1 - vote yes
			for (int j = 0; j < 5; j++) {
				vote[i][j] = new FDV(store, surname[i] + " Group [" + (j + 1)
						+ "]", 0, 1);
				vars.add(vote[i][j]);
			}

		}

		// Sum constraint for each group
		ArrayList<FDV> votesMotion1 = new ArrayList<FDV>();
		for (int i = 0; i < 5; i++)
			votesMotion1.add(vote[i][iMotion1]);
		store.impose(new Sum(votesMotion1, sum4Group[iMotion1]));

		ArrayList<FDV> votesMotion2 = new ArrayList<FDV>();
		for (int i = 0; i < 5; i++)
			votesMotion2.add(vote[i][iMotion2]);
		store.impose(new Sum(votesMotion2, sum4Group[iMotion2]));

		ArrayList<FDV> votesMotion3 = new ArrayList<FDV>();
		for (int i = 0; i < 5; i++)
			votesMotion3.add(vote[i][iMotion3]);
		store.impose(new Sum(votesMotion3, sum4Group[iMotion3]));

		ArrayList<FDV> votesMotion4 = new ArrayList<FDV>();
		for (int i = 0; i < 5; i++)
			votesMotion4.add(vote[i][iMotion4]);
		store.impose(new Sum(votesMotion4, sum4Group[iMotion4]));

		ArrayList<FDV> votesMotion5 = new ArrayList<FDV>();
		for (int i = 0; i < 5; i++)
			votesMotion5.add(vote[i][iMotion5]);
		store.impose(new Sum(votesMotion5, sum4Group[iMotion5]));

		// Clues enconding

		// 1. Each motion got a different number of yes votes.

		store.impose(new Alldifferent(sum4Group));

		// 2. In all, the five motions got three more yes votes than no votes.

		// We sum all yes votes and no votes.

		FDV noYesVotes = new FDV(store, "noYesVotes", 1, 25);
		FDV noNoVotes = new FDV(store, "noNoVotes", 1, 25);
		FDV noVotes = new FDV(store, "25", 25, 25);

		vars.add(noYesVotes); vars.add(noNoVotes); vars.add(noVotes);
		
		// We constraint number of yes votes.
		store.impose(new Sum(sum4Group, noYesVotes));
		// To connect no of yes votes with no of no votes.
		store.impose(new XplusYeqZ(noYesVotes, noNoVotes, noVotes));

		store.impose(new XplusCeqZ(noNoVotes, 3, noYesVotes));

		// 3. No two council members voted the same way on all five motions.

		// To represent the uniqueness of binary vector, we will use weighted
		// sum constraint.
		// We transform unique binary vectors into unique numbers on which we
		// can impose all different.

		int weights[] = { 1, 2, 4, 8, 16 };

		FDV weightedVotes[] = new FDV[5];
		for (int i = 0; i < 5; i++)
			weightedVotes[i] = new FDV(store, "weightedVotes4" + surname[i], 1,
					32);

		store.impose(new SumWeight(vote[iAkerman], weights,
				weightedVotes[iAkerman]));
		store.impose(new SumWeight(vote[iBaird], weights,
						weightedVotes[iBaird]));
		store.impose(new SumWeight(vote[iChatham], weights,
				weightedVotes[iChatham]));
		store.impose(new SumWeight(vote[iDuval], weights,
						weightedVotes[iDuval]));
		store.impose(new SumWeight(vote[iEtting], weights,
				weightedVotes[iEtting]));

		// All weightes votes must be different.
		store.impose(new Alldifferent(weightedVotes));

		// 4. The two women disagreed in their voting more often than they
		// agreed.
		// Women are people no 2 and 4 (Baird and Duval).

		// We will use Reified constraints to encode this clue.

		FDV reified[] = new FDV[5];
		for (int i = 0; i < 5; i++) {
			reified[i] = new FDV(store, "agreeOnVote" + i, 0, 1);
			store.impose(new Reified(
					new XeqY(vote[iBaird][i], vote[iDuval][i]), reified[i]));
		}

		// There are 5 votes for each person, this means that no of yes votes is
		// at most 2.
		FDV sumOfReified = new FDV(store, "noAgreeBairdAndDuval", 1, 5);
		store.impose(new Sum(reified, sumOfReified));
		store.impose(new XlteqC(sumOfReified, 2));

		// 5. Mr. Chatham never made two yes votes on consecutive motions.

		// implied constraint, the sum must be smaller than 4.
		FDV sumChatham = new FDV(store, "sumChatham", 0, 3);
		store.impose(new Sum(vote[iChatham], sumChatham));

		// We take each pair and make sure they are not two yes votes
		FDV two = new FDV(store, "2", 2, 2);
		for (int i = 0; i < 4; i++)
			store.impose(new XplusYlteqZ(vote[iChatham][i],
					vote[iChatham][i + 1], two));

		// 6. Mr. Akerman and Ms. Baird both voted in favor of motion 4.
		store.impose(new XeqC(vote[iAkerman][3], 1));
		store.impose(new XeqC(vote[iBaird][3], 1));

		// 7. Motion 1 received two more yes votes than motion 2 did.
		store.impose(new XplusCeqZ(sum4Group[iMotion2], 2,
						sum4Group[iMotion1]));

		// 8. Motion 3 received twice as many yes votes as motion 4 did.
		store.impose(new XmulCeqZ(sum4Group[iMotion4], 2, sum4Group[iMotion3]));

	}
		
	/**
	 * It executes the program which solves this logic puzzle.
	 * @param args
	 */
	public static void main(String args[]) {

		ProAndCon example = new ProAndCon();
		
		example.model();

		if (example.search())
			System.out.println("Solution(s) found");
		
	}		
	
	
}
