/**
 *  Constants.java 
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

package JaCoP.core;

/**
 * Standard unified interface for all classes. Defines all constants used in
 * JaCoP.
 * 
 * @author Krzysztof Kuchcinski and Radoslaw Szymanek
 * @version 2.3
 */

public interface Constants {

	/**
	 * It specifies the constant for GROUND event. It has to be smaller 
	 * than the constant for events BOUND and ANY.
	 */
	final static int GROUND = 0;

	/**
	 * It specifies the constant for BOUND event. It has to be smaller 
	 * than the constant for event ANY.
	 */
	final static int BOUND = 1;

	/**
	 * It specifies the constant for ANY event.
	 */
	final static int ANY = 2;

	/**
	 * It specifies the constant for NONE event, if event is NONE then
	 * the constraint is not attached to a variable. Useful for constraints
	 * which are always satisfied or not satisfied after the first 
	 * consistency function execution.
	 */
	final static int NONE = -1;

	/**
	 * It specifies the minimum element in the domain.
	 */
	static final int MinInt = -10000000;
	
	/**
	 * It specifies the maximum element in the domain.
	 */
	static final int MaxInt = 10000000;
	
	/**
	 * It specifies the id (constraint type) of different constraints in order to facilitate 
	 * fast type discovery.
	 */
	@SuppressWarnings("all")
	static final short add = 0, addC = 1, eq = 2, lt = 3, gt = 4, ltEq = 5,
			gtEq = 6, nEq = 7, eqC = 8, ltC = 9, gtC = 10, ltEqC = 11,
			gtEqC = 12, nEqC = 13, mulC = 14, mul = 15, reified = 16,
			alldifferent = 17, minConstr = 18, maxConstr = 19, 
			cumul = 20, diff = 21, ifthen = 22, ifthenelse = 23, or = 24,
			and = 25, not = 26, in = 27, eqConstr = 28, 
			gtC2 = 29, add3 = 30, add3C = 31, diff2 = 32,
			disjoint = 33, circuit = 34, sumConstr = 35,
			sumWeightConstr = 36, distance = 37, alldistinct = 38,
			elementVar = 39, elementInteger = 40, alldiff = 41, 
			assignmentConstr = 42, exp = 43, extSupport = 44, extConflict = 45,
	                abseq = 46, addeqC = 47, muleqC = 48, 
			countConstr = 50, noGoodConstr = 51, xor = 52, among = 53, 
			amongVar = 54, gcc = 55, regular = 56, extensionalSupportSTR = 57, 
			extensionalSupportMDD = 58, addCltEq = 59, val = 60, knapsack = 61, 
			geost = 62, andbool = 63, orbool = 64, eqbool = 65, ifthenbool = 66, 
			xorbool = 67, elementSet = 68, cardSet = 69, cardLexSet = 70, 
			disjointSets = 71, inSet = 72, lexSet = 73, matchSet = 74, specialSum = 75, 
			sumWeightSet = 76, XdiffYeqZ = 77, XeqYSet = 78, XintersectYeqZSet = 79,
	    XinYSet = 80, XunionYeqZSet = 81, div = 82, mod = 83;

	/**
	 * It specifies the short names for constraints. They are used to create
	 * automatic names for constraints based on their unique number and their 
	 * name.
	 */
	@SuppressWarnings("all")
	static final String id_add = "add",	 id_addC = "addC",
			id_addeqC = "addeqC", id_muleqC = "muleqC", id_abseq = "eq", 
			id_eq = "eq", id_lt = "lt", id_gt = "gt", id_ltEq = "lteq", 
			id_gtEq = "gteq", id_nEq = "neq", id_eqC = "eqC", id_ltC = "ltC",
			id_gtC = "gtC", id_ltEqC = "ltEqC", id_gtEqC = "gtEqC", 
			id_nEqC = "nEqC", id_mulC = "mulC", id_mul = "mul", 
			id_mulC_fc = "mulC_fc", id_addYle = "addYle", id_addCle = "addCle",
			id_reified = "reified", id_alldifferent = "alldifferent", 
			id_min = "min", id_max = "max", id_elementVar = "elemVar",
			id_elementINT = "elemINT", id_cumulative = "cumul",
			id_diff = "diff",
			id_ifthen = "ifthen", id_ifthenelse = "ifthenelse",
			id_circuit = "circuit", id_or = "or", id_and = "and", 
			id_not = "not", id_in = "in", id_eqConstr = "eqConstr",
			id_gtC2 = "gtC2", id_add3 = "add3",
			id_add3C = "add3C", id_sum = "sum", id_sumWeight = "sumWeight",
			id_distance = "distance", 
			id_alldistinct = "alldistinct", 
			id_alldiff = "alldiff", id_diff2 = "diff2",
			id_disjoint = "disjoint", id_assignment = "assignment", 
			id_exp = "exp", id_extSupportVA = "extensionalSupport",
	                id_extCon = "extensionalConflict", 
			id_noGood = "noGood", id_count = "count", id_xor = "xor",
			id_among = "among", id_amongVar = "amongVar", id_GCC = "GCC", 
			id_regular = "regular", 
			id_extensionalSupportSTR="extensionalSupportSTR",
			id_extensionalSupportMDD="extensionalSupportMDD", id_val="Values", 
			id_knapsack = "knapsack", id_geost = "geost", id_andbool = "addbool", 
			id_orbool = "orbool", id_eqbool = "eqbool", id_ifthenbool = "ifthenbool", 
			id_xorbool = "xorbool", id_elementSet = "elementSet", id_cardSet= "cardSet", 
			id_cardLexSet = "cardLexSet", id_disjointSets = "disjointSets", id_inSet = "inSet", 
			id_lexSet = "lexSet", id_matchSet = "matchSet", id_specialSum = "specialSumSet", 
			id_sumWeightSet = "sumWeightSet", id_XdiffYeqZ = "XdiffYeqZSet", id_XeqYSet = "XeqYSet", 
	    id_XintersectYeqZ = "XintersectYeqZSet", id_XinYSet = "XinYSet", id_XunionYeqZSet = "XunionYeqZSet", id_div = "XdivYeqZ", id_mod = "XmodYeqZ";
			
}
