/**
 *  Queens.java 
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

import JaCoP.constraints.Alldiff;
import JaCoP.constraints.Element;
import JaCoP.constraints.Sum;
import JaCoP.constraints.XneqY;
import JaCoP.constraints.XplusCeqZ;
import JaCoP.core.FDV;
import JaCoP.core.Store;
import JaCoP.core.Variable;

/**
 * 
 * It models the queens problem in different ways as well as applies 
 * different search methods.
 * 
 * @author Radoslaw Szymanek
 *
 */
public class Queens extends Example {

	// Place n queens on a chessboard of size nxn
	// so none queen checks another queen
	// This program uses only primitive (basic) constraints

	// Example of queen placement in 4x4 chessboard
	// ---------
	// | | |Q| |
	// ---------
	// |Q| | | |
	// ---------
	// | | | |Q|
	// ---------
	// | |Q| | |
	// ---------

	/**
	 * It specifies the size of chessboard to be used in the model.
	 */
	public int numberQ = 19;	
	
	/**
	 * This model uses only primitive constraints.
	 */
	public void modelBasic() {

		// Creating constraint store
		store = new Store();
		vars = new ArrayList<Variable>();

		// I-th queen variable represents the placement
		// of a queen in i-th column
		// There are n columns so there are n variables
		FDV queens[] = new FDV[numberQ];

		// Each queen variable has a domain from 1 to numberQ
		// Value of queen variable represents the row
		for (int i = 0; i < numberQ; i++) {
			queens[i] = new FDV(store, "Q" + (i + 1), 1, numberQ);
			vars.add(queens[i]);
		}
		// Queens from different columns can not be placed
		// in the same row, therefore the values
		// must be different
		for (int i = 0; i < queens.length; i++)
			for (int j = i - 1; j >= 0; j--)
				store.impose(new XneqY(queens[i], queens[j]));

		// Notice that j index starts from i+1
		for (int i = 0; i < queens.length; i++)
			for (int j = i + 1; j < queens.length; j++) {

				// Temporary variable denotes the chessboard
				// field in j-th column which is checked by
				// i-th column queen
				// If temporarty variable has value outside
				// range 1..numberQ then i-th column queen
				// does not check any field in j-th column

				// Checking diagonals like this \
				// Note that C constant is positive
				FDV temporary = new FDV(store, -2 * numberQ, 2 * numberQ);
				store.impose(new XplusCeqZ(queens[j], j - i, temporary));
				store.impose(new XneqY(queens[i], temporary));

				// Checking diagonals like this /
				// Note that C constant is negative
				temporary = new FDV(store, -2 * numberQ, 2 * numberQ);
				store.impose(new XplusCeqZ(queens[j], -(j - i), temporary));
				store.impose(new XneqY(queens[i], temporary));

			}

	}
	
	
	/**
	 * This model uses dual model to solve Queens problems.
	 */
	public void modelChanneling() {
		
		// Creating constraint store
		store = new Store();
		vars = new ArrayList<Variable>();
		
		// Global model

		FDV queens[] = new FDV[numberQ];

		for (int i = 0; i < numberQ; i++) {
			queens[i] = new FDV(store, "Q" + (i + 1), 1, numberQ);
			vars.add(queens[i]);
		}
		
		store.impose(new Alldiff(queens));

		FDV[] diagonalUp = new FDV[queens.length];
		FDV[] diagonalDown = new FDV[queens.length];

		diagonalUp[0] = queens[0]; // diagonal like this /
		diagonalDown[0] = queens[0]; // diagonal like this \

		for (int i = 1; i < queens.length; i++) {

			diagonalUp[i] = new FDV(store, -2 * numberQ, 2 * numberQ);
			store.impose(new XplusCeqZ(queens[i], i, diagonalUp[i]));

			diagonalDown[i] = new FDV(store, -2 * numberQ, 2 * numberQ);
			store.impose(new XplusCeqZ(queens[i], -i, diagonalDown[i]));

		}

		store.impose(new Alldiff(diagonalUp));
		store.impose(new Alldiff(diagonalDown));

		// Channeling constraints

		FDV[] values = new FDV[numberQ];

		FDV queensRows[] = new FDV[numberQ];

		for (int i = 0; i < numberQ; i++) {
			queensRows[i] = new FDV(store, "Qrows" + (i + 1), 1, numberQ);
			vars.add(queensRows[i]);
		}
		
		for (int i = 0; i < numberQ; i++)
			values[i] = new FDV(store, "val-" + (i + 1), i + 1, i + 1);

		for (int i = 0; i < numberQ; i++)
			store.impose(new Element(queensRows[i], queens, values[i]));

	}
	

	
	/**
	 * It uses a model based on fields to model Queens problem (rather inefficient model).
	 */
	public void modelFields() {

		// Creating constraint store
		store = new Store();
		vars = new ArrayList<Variable>();
		
		FDV one = new FDV(store, "one", 1, 1);

		FDV fields[] = new FDV[numberQ * numberQ];

		for (int i = 0; i < numberQ; i++)
			for (int j = 0; j < numberQ; j++) {
				fields[i * numberQ + j] = new FDV(store, "F" + (i + 1) + ","
						+ (j + 1), 0, 1);
			vars.add(fields[i * numberQ + j]);
			}
		
		FDV row[] = new FDV[numberQ];
		FDV firstRowPosition = new FDV(store, "firstRowPosition", 0, numberQ);

		for (int i = 0; i < numberQ; i++) {
			for (int j = 0; j < numberQ; j++)
				row[j] = fields[i * numberQ + j];
			FDV rowSum = new FDV(store, "row" + (i + 1), 1, 1);
			store.impose(new Sum(row, rowSum));
			if (i == 0) {
				store.impose(new Element(firstRowPosition, row, one));
			}
		}

		FDV column[] = new FDV[numberQ];
		FDV firstColumnPosition = new FDV(store, "firstColumnPosition", 0,
				numberQ);

		for (int j = 0; j < numberQ; j++) {
			for (int i = 0; i < numberQ; i++)
				column[i] = fields[i * numberQ + j];
			FDV columnSum = new FDV(store, "column" + (j + 1), 1, 1);
			store.impose(new Sum(column, columnSum));
			if (j == 0) {
				store.impose(new Element(firstColumnPosition, column, one));
			}
		}

		// symmetry breaking
		// store.impose(new XltY(firstColumnPosition, firstRowPosition));

		FDV diagonalSums[] = new FDV[numberQ * 4 - 6];
		int indexSum = 0;

		FDV diagonal[] = null;

		for (int i = 0; i < numberQ - 1; i++) {
			diagonal = new FDV[numberQ - i];
			for (int j = 0; j < numberQ - i; j++)
				diagonal[j] = fields[(i + j) * numberQ + i + j];

			FDV diagonalSum = new FDV(store, "diagonal-west-south-F" + (i + 1)
					+ "," + 1, 0, 1);
			diagonalSums[indexSum++] = diagonalSum;
			store.impose(new Sum(diagonal, diagonalSum));
		}

		for (int i = numberQ - 1; i > 0; i--) {
			diagonal = new FDV[i + 1];
			for (int j = 0; j < i + 1; j++)
				diagonal[j] = fields[(i - j) * numberQ + j];

			FDV diagonalSum = new FDV(store, "diagonal-west-north-F" + (i + 1)
					+ "," + 1, 0, 1);
			diagonalSums[indexSum++] = diagonalSum;
			store.impose(new Sum(diagonal, diagonalSum));
		}

		for (int j = 1; j < numberQ - 1; j++) {
			diagonal = new FDV[numberQ - j];
			for (int i = 0; i < numberQ - j; i++) {
				diagonal[i] = fields[i * numberQ + j + i];
			}

			FDV diagonalSum = new FDV(store, "diagonal-west-south-F" + (1)
					+ "," + (j + 1), 0, 1);
			diagonalSums[indexSum++] = diagonalSum;
			store.impose(new Sum(diagonal, diagonalSum));
		}

		for (int j = 1; j < numberQ - 1; j++) {
			diagonal = new FDV[numberQ - j];
			for (int i = 0; i < numberQ - j; i++) {
				diagonal[i] = fields[(numberQ - i - 1) * numberQ + j + i];
			}

			FDV diagonalSum = new FDV(store, "diagonal-west-north-F"
					+ (numberQ) + "," + (j + 1), 0, 1);
			diagonalSums[indexSum++] = diagonalSum;
			store.impose(new Sum(diagonal, diagonalSum));
		}

		FDV numberOfDiagonals = new FDV(store, "takenDiagonals",
				2 * numberQ - 1, 2 * numberQ);

		store.impose(new Sum(diagonalSums, numberOfDiagonals));

		FDV queenNo = new FDV(store, "noQ", numberQ, numberQ);

		store.impose(new Sum(fields, queenNo));

	}
	
	@Override
	public void model() {

		// Creating constraint store
		store = new Store();
		vars = new ArrayList<Variable>();
		
		// I-th queen variable represents the placement
		// of a queen in i-th column
		// There are n columns so there are n variables
		FDV queens[] = new FDV[numberQ];

		for (int i = 0; i < numberQ; i++) {
			queens[i] = new FDV(store, "Q" + (i + 1), 1, numberQ);
			vars.add(queens[i]);
		}
		// symmetry breaking - not usefull in this problem
		// FDV one = new FDV(store, "one", 1, 1);
		// FDV firstRowPosition = new FDV(store, "firstRowPosition", 0,
		// numberQ);
		// store.impose(new Element(firstRowPosition, queens, one));
		// store.impose(new XltY(queens[0], firstRowPosition));

		store.impose(new Alldiff(queens));

		FDV[] diagonalUp = new FDV[queens.length];
		FDV[] diagonalDown = new FDV[queens.length];

		diagonalUp[0] = queens[0]; // diagonal like this /
		diagonalDown[0] = queens[0]; // diagonal like this \

		for (int i = 1; i < queens.length; i++) {

			// Position of every queen is shifted based on a distance
			// to column 1, If any queen can check another on diagonal
			// then the position after shifting will be equal to the
			// position of the checked queen. Therefore the diagonal list
			// is used in Alldifferent constraint
			diagonalUp[i] = new FDV(store, -2 * numberQ, 2 * numberQ);
			store.impose(new XplusCeqZ(queens[i], i, diagonalUp[i]));

			diagonalDown[i] = new FDV(store, -2 * numberQ, 2 * numberQ);
			store.impose(new XplusCeqZ(queens[i], -i, diagonalDown[i]));

		}

		// Imposes constraints so queens can not check each other using
		// diagonals.
		store.impose(new Alldiff(diagonalUp));
		store.impose(new Alldiff(diagonalDown));

	}
		
	/**
	 * It executes different models and search methods to solve Queens problem.
	 * @param args first argument specifies the size of the chessboard.
	 */
	public static void main(String args[]) {

		Queens example = new Queens();

		// It is possible to supply the program
		// with the chessboard size
		if (args.length != 0)
			example.numberQ = new Integer(args[0]);
		
		example.modelBasic();

		if (example.searchLDS(3))
			System.out.println("Solution(s) found");
		
		example = new Queens();

		// It is possible to supply the program
		// with the chessboard size
		if (args.length != 0)
			example.numberQ = new Integer(args[0]);
		
		example.modelChanneling();

		if (example.searchSmallestMiddle())
			System.out.println("Solution(s) found");		
		
		
		example = new Queens();

		// It is possible to supply the program
		// with the chessboard size
		if (args.length != 0)
			example.numberQ = new Integer(args[0]);
		
		example.modelFields();

		if (example.search())
			System.out.println("Solution(s) found");	
		
		example = new Queens();

		// It is possible to supply the program
		// with the chessboard size
		if (args.length != 0)
			example.numberQ = new Integer(args[0]);
		
		example.model();

		
		if (example.searchSmallestMiddle())
			System.out.println("Solution(s) found");		
	}	
		
}
