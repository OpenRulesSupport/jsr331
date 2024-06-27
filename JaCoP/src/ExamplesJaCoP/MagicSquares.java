/**
 *  MagicSquares.java 
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
import JaCoP.constraints.Alldistinct;
import JaCoP.constraints.Assignment;
import JaCoP.constraints.Constraint;
import JaCoP.constraints.Sum;
import JaCoP.constraints.XltY;
import JaCoP.core.BoundDomain;
import JaCoP.core.FDV;
import JaCoP.core.Store;
import JaCoP.core.Variable;

/**
 * @author Radoslaw Szymanek
 *
 * MagicSquare problem consists of filling the square of size n with
 * numbers from 1 to n^2 in such a way that all rows, all columns, and
 * main diagonals are equal to the same number K. K can be computed to 
 * be equal to (n * (n^2 + 1)) / 2.
 *  
 */

public class MagicSquares extends Example {

	/**
	 * It specifies the number 
	 */
	public int number = 4;

	/**
	 * It specifies the list of constraints which can be used for guiding shaving.
	 */
	public ArrayList<Constraint> guidingShaving;


	@Override
	public void model() {

		// Creating constraint store
		store = new Store();
		vars = new ArrayList<Variable>();

		FDV squares[] = new FDV[number * number];

		FDV k = new FDV(store, "K", (number * (number * number + 1)) / 2,
				(number * (number * number + 1)) / 2);

		for (int i = 0; i < number; i++)
			for (int j = 0; j < number; j++)
				squares[i * number + j] = new FDV(store, "S" + (i + 1) + ","
						+ (j + 1), 1, number * number);

		for (int i = 0; i < number; i++)
			vars.add(squares[(i) * number + i]);
		for (int i = number; i > 0; i--)
			vars.add(squares[(i - 1) * number + (number - i)]);
		for (Variable v : squares)
			vars.add(v);

		// Imposing inequalities constraints between squares
		store.impose(new Alldiff(squares));

		FDV row[] = new FDV[number];

		for (int i = 0; i < number; i++) {
			for (int j = 0; j < number; j++)
				row[j] = squares[i * number + j];
			store.impose(new Sum(row, k));
		}

		FDV column[] = new FDV[number];

		for (int j = 0; j < number; j++) {
			for (int i = 0; i < number; i++)
				column[i] = squares[i * number + j];
			store.impose(new Sum(column, k));
		}

		FDV diagonal[] = new FDV[number];

		for (int i = 0; i < number; i++)
			diagonal[i] = squares[(i) * number + i];

		store.impose(new Sum(diagonal, k));

		for (int i = number; i > 0; i--)
			diagonal[i - 1] = squares[(i - 1) * number + (number - i)];
		store.impose(new Sum(diagonal, k));

		// symmetry breaking
		store.impose(new XltY(squares[0], squares[number - 1]));
		store.impose(new XltY(squares[0], squares[number * number - 1]));
		store.impose(new XltY(squares[0], squares[number * number - number]));

	}

	/**
	 * It specifies the model which uses only variables with BoundDomain.
	 */
	public void modelBound() {

		// Creating constraint store
		store = new Store();
		vars = new ArrayList<Variable>();
		
		Variable squares[] = new Variable[number * number];

		Variable k = new FDV(store, "K", (number * (number * number + 1)) / 2,
				(number * (number * number + 1)) / 2);

		for (int i = 0; i < number; i++)
			for (int j = 0; j < number; j++)
				squares[i * number + j] = new Variable(store, "S" + (i + 1) + ","
						+ (j + 1), new BoundDomain(1, number * number));
				
		// Imposing inequalities constraints between squares
		store.impose(new Alldiff(squares));

		Variable row[] = new Variable[number];

		for (int i = 0; i < number; i++) {
			for (int j = 0; j < number; j++)
				row[j] = squares[i * number + j];
			store.impose(new Sum(row, k));
		}

		Variable column[] = new Variable[number];

		for (int j = 0; j < number; j++) {
			for (int i = 0; i < number; i++)
				column[i] = squares[i * number + j];
			store.impose(new Sum(column, k));
		}

		Variable diagonal[] = new Variable[number];

		for (int i = 0; i < number; i++)
			diagonal[i] = squares[(i) * number + i];

		store.impose(new Sum(diagonal, k));

		for (int i = number; i > 0; i--)
			diagonal[i - 1] = squares[(i - 1) * number + (number - i)];
		store.impose(new Sum(diagonal, k));

		// symmetry breaking
		//store.impose(new XltY(squares[0], squares[number - 1]));
		//store.impose(new XltY(squares[0], squares[number * number - 1]));
		//store.impose(new XltY(squares[0], squares[number * number - number]));

		for (int i = 0; i < number; i++)
			vars.add(squares[(i) * number + i]);
		for (int i = number; i > 0; i--)
			vars.add(squares[(i - 1) * number + (number - i)]);
		for (Variable v : squares)
			vars.add(v);

	}	
	
	
	/**
	 * It creates the model with specification of what constraint can 
	 * help in guiding shaving.
	 */
	public void model4Shaving() {

		guidingShaving = new ArrayList<Constraint>();

		// Creating constraint store
		store = new Store();
		vars = new ArrayList<Variable>();

		FDV squares[] = new FDV[number * number];

		FDV k = new FDV(store, "K", (number * (number * number + 1)) / 2,
				(number * (number * number + 1)) / 2);

		for (int i = 0; i < number; i++)
			for (int j = 0; j < number; j++)
				squares[i * number + j] = new FDV(store, "S" + (i + 1) + ","
						+ (j + 1), 1, number * number);

		for (int i = 0; i < number; i++)
			vars.add(squares[(i) * number + i]);
		for (int i = number; i > 0; i--)
			vars.add(squares[(i - 1) * number + (number - i)]);
		for (Variable v : squares)
			vars.add(v);

		// Imposing inequalities constraints between squares
		store.impose(new Alldiff(squares));

		FDV row[] = new FDV[number];

		for (int i = 0; i < number; i++) {
			for (int j = 0; j < number; j++)
				row[j] = squares[i * number + j];
			Constraint cx = new Sum(row, k);
			store.impose(cx);
			guidingShaving.add(cx);
		}

		FDV column[] = new FDV[number];

		for (int j = 0; j < number; j++) {
			for (int i = 0; i < number; i++)
				column[i] = squares[i * number + j];

			Constraint cx = new Sum(column, k);
			store.impose(cx);
			guidingShaving.add(cx);
		}

		FDV diagonal[] = new FDV[number];

		for (int i = 0; i < number; i++)
			diagonal[i] = squares[(i) * number + i];

		Constraint cx = new Sum(diagonal, k);
		store.impose(cx);
		guidingShaving.add(cx);

		for (int i = number; i > 0; i--)
			diagonal[i - 1] = squares[(i - 1) * number + (number - i)];
		store.impose(new Sum(diagonal, k));

		// symmetry breaking
		store.impose(new XltY(squares[0], squares[number - 1]));
		store.impose(new XltY(squares[0], squares[number * number - 1]));
		store.impose(new XltY(squares[0], squares[number * number - number]));

		// store.print();

		ArrayList<FDV> fdvV = new ArrayList<FDV>();

		for (int i = 0; i < number; i++)
			fdvV.add(squares[(i) * number + i]);
		for (int i = number; i > 0; i--)
			fdvV.add(squares[(i - 1) * number + (number - i)]);

		for (FDV v : squares)
			fdvV.add(v);

	}

	/**
	 * IT creates a dual model.
	 */
	public void modelDual() {

		// Creating constraint store
		store = new Store();
		vars = new ArrayList<Variable>();

		FDV squares[] = new FDV[number * number];

		FDV k = new FDV(store, "K", (number * (number * number + 1)) / 2,
				(number * (number * number + 1)) / 2);

		for (int i = 0; i < number; i++)
			for (int j = 0; j < number; j++)
				squares[i * number + j] = new FDV(store, "S" + (i + 1) + ","
						+ (j + 1), 1, number * number);

		for (int i = 0; i < number; i++)
			vars.add(squares[(i) * number + i]);
		for (int i = number; i > 0; i--)
			vars.add(squares[(i - 1) * number + (number - i)]);
		for (Variable v : squares)
			vars.add(v);

		FDV row[] = new FDV[number];

		for (int i = 0; i < number; i++) {
			for (int j = 0; j < number; j++)
				row[j] = squares[i * number + j];
			store.impose(new Sum(row, k));
		}

		FDV column[] = new FDV[number];

		for (int j = 0; j < number; j++) {
			for (int i = 0; i < number; i++)
				column[i] = squares[i * number + j];
			store.impose(new Sum(column, k));
		}

		FDV diagonal[] = new FDV[number];

		for (int i = 0; i < number; i++)
			diagonal[i] = squares[(i) * number + i];

		store.impose(new Sum(diagonal, k));

		for (int i = number; i > 0; i--)
			diagonal[i - 1] = squares[(i - 1) * number + (number - i)];
		store.impose(new Sum(diagonal, k));

		// // symmetry breaking
		// store.impose(new XltY(squares[0], squares[number-1]));
		// store.impose(new XltY(squares[0], squares[number*number - 1]));
		// store.impose(new XltY(squares[0], squares[number*number - number]));

		FDV[] d = new FDV[number * number];

		for (int i = 0; i < number * number; i++) {
			d[i] = new FDV(store, "d" + i, 1, number * number);
			vars.add(d[i]);
		}

		store.impose(new Assignment(squares, d, 1));

		// Imposing inequalities constraints between squares
		Constraint cx = new Alldistinct(squares);
		store.impose(cx);

	}

	/**
	 * It executes the program which solves the MagicSquare problem using many different
	 * model and searches.
	 * 
	 * @param args the first argument allows to specify the size of magic square.
	 */
	public static void main(String args[]) {

		MagicSquares example = new MagicSquares();

		if (args.length != 0)
			example.number = new Integer(args[0]);
		
		example.model();

		if (example.searchMiddle())
			System.out.println("Solution(s) found");

		MagicSquares exampleDual = new MagicSquares();

		if (args.length != 0)
			exampleDual.number = new Integer(args[0]);
		
		exampleDual.modelDual();

		if (exampleDual.creditSearch(64, 5000, 10))
			System.out.println("Solution(s) found");

		MagicSquares exampleShave = new MagicSquares();

		if (args.length != 0)
			exampleShave.number = new Integer(args[0]);
		
		exampleShave.model4Shaving();

		if (exampleShave.shavingSearch(exampleShave.guidingShaving, true))
			System.out.println("Solution(s) found");
		
		MagicSquares exampleBound = new MagicSquares();
		exampleBound.number = 5;
		exampleBound.modelBound();

		if (exampleBound.search())
			System.out.println("Solution(s) found");		
		
	}


}
