/**
 *  Kakro.java 
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
import JaCoP.constraints.Sum;
import JaCoP.core.FDV;
import JaCoP.core.FDstore;
import JaCoP.core.Variable;

/**
 * 
 * @author Radoslaw Szymanek
 * 
 * This is a program which uses Constraint Programming to find the solution to a
 * simple Kakro puzzle. For a moment the problem representation does not allow 
 * to model the problems with fields which are both origins of the row and column word.
 */

public class Kakro extends Example {

	@Override
	public void model() {

		// >1 - wall with row sum
		// <0 - wall with column sum
		// 1 - field
		// 0 - clean wall.
		int[][] description = { { 0, -4, -7,  0 }, 
								{ 3,  1,  1, -3 },
								{ 6,  1,  1,  1 }, 
								{ 0,  5,  1,  1 } };

		int noRows = 4;
		int noColumns = 4;
		
		store = new FDstore();
		vars = new ArrayList<Variable>();
		
		FDV[][] elements = new FDV[noRows][noColumns];

		// Creating variables.
		for (int i = 0; i < noRows; i++)
			for (int j = 0; j < noColumns; j++)
				if (description[i][j] == 1) {
					elements[i][j] = new FDV(store, "f" + i + "-" + j, 1, 9);
					vars.add(elements[i][j]);
				}

		// Creating constraints for rows.
		for (int i = 0; i < noRows; i++)
			for (int j = 0; j < noColumns; j++)
				if (description[i][j] > 1) {
					FDV sum = new FDV(store, "sumAt" + i + "-" + j,
							description[i][j], description[i][j]);

					ArrayList<FDV> row = new ArrayList<FDV>();

					for (int m = j + 1; m < noColumns && description[i][m] == 1; m++)
						row.add(elements[i][m]);

					store.impose(new Sum(row, sum));
					store.impose(new Alldiff(row));
				}

		// Creating constraints for columns.
		for (int i = 0; i < noRows; i++)
			for (int j = 0; j < noColumns; j++)
				if (description[i][j] < 0) {
					FDV sum = new FDV(store, "sumCol" + i + "-" + j,
							-description[i][j], -description[i][j]);

					ArrayList<FDV> column = new ArrayList<FDV>();

					for (int m = i + 1; m < noRows && description[m][j] == 1; m++)
						column.add(elements[m][j]);

					store.impose(new Sum(column, sum));
					store.impose(new Alldiff(column));
				}

	}
		

	/**
	 * It executes the program to solve simple Kakro puzzle.
	 * @param args
	 */
	public static void main(String args[]) {

		Kakro example = new Kakro();
		
		example.model();

		if (example.search())
			System.out.println("Solution(s) found");
		
	}	
	
	
}
