/**
 *  Diet.java 
 *  This file is part of JaCoP.
 *
 *  JaCoP is a Java Constraint Programming solver. 
 *	
 *	Copyright (C) 2008 Hakan Kjellerstrand and Radoslaw Szymanek
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
import JaCoP.constraints.SumWeight;
import JaCoP.constraints.XgteqC;
import JaCoP.core.Store;
import JaCoP.core.Variable;

/**
 *
 * Diet problem in JaCoP.
 * 
 * Problem from http://www.mcs.vuw.ac.nz/courses/OPRE251/2006T1/Labs/lab09.pdf
 * """
 *  My diet requires that all the food I eat come from one of the four .basic 
 *  food groups. (chocolate cake, ice cream, soft drink, and cheesecake). 
 *  Each (large) slice of chocolate cake costs 50c, 
 *  each scoop of chocolate ice cream costs 20c, 
 *  each bottle of cola costs 30c, 
 *  and each piece of pineapple cheesecake costs 80c. 
 *
 *  Each day, I must ingest at least 500 calories, 
 *  6 oz of chocolate, 
 *  10 oz of sugar, 
 *  and 8 oz of fat.
 *  The nutritional content per unit of each food is shown in the table below. 
 * 
 *  Formulate a linear programming model that can be used to satisfy my daily 
 *  nutritional requirement at minimum cost.

 *  Type of                        Calories   Chocolate    Sugar    Fat
 *  Food                                      (ounces)     (ounces) (ounces)
 *  Chocolate Cake (1 slice)       400           3            2      2
 *  Chocolate ice cream (1 scoop)  200           2            2      4
 *  Cola (1 bottle)                150           0            4      1
 *  Pineapple cheesecake (1 piece) 500           0            4      5
 *
 * """  
 *
 * Compare with my MiniZinc model:
 * http://www.hakank.org/minizinc/diet1.mzn
 *
 */

public class Diet extends Example {

    Variable[] x;

    int n; // number of ingredients
    int m; // number of food types

    String[] food = {"Chocolate Cake", "Chocolate ice cream", "Cola", "Pineapple cheesecake"};

    String[] ingredients = {"Calories", "Chocolate", "Sugar", "Fat"};

    /**
     *
     *  Imposes the model of the problem.
     *
     */
    @Override
	public void model() {

        store = new Store();

        n = 4; // number of ingredients
        m = 4; // number of food types

        int[] price   = {50, 20, 30, 80}; // in cents
        int[] limits  = {500, 6, 10, 8};  // minimum required for a diet

                    // Food: 0   1     2    3
        int[][] matrix = {{400, 200, 150, 500},  // calories
                          {  3,   2,   0,   0},  // chocolate
                          {  2,   2,   4,   4},  // sugar
                          {  2,   4,   1,   5}}; // fat


        // create x before using it in SumWeight
        x = new Variable[m];
        for(int i = 0; i < m; i++) {
            x[i] = new Variable(store, "x_" + i, 0, 10);
        }

        Variable[] sums = new Variable[n];
        for(int i = 0; i < n; i++) {
            sums[i] = new Variable(store, "sums_" + i, 0, 10000);
            store.impose(new SumWeight(x, matrix[i], sums[i]));
            store.impose(new XgteqC(sums[i], limits[i]));
        }


        // Cost to minimize: x * price
        cost = new Variable(store, "cost", 0, 100000);
        store.impose( new SumWeight(x, price, cost) );

        vars = new ArrayList<Variable>();
        for(Variable v : x) 
            vars.add(v);


    } 
    
    /**
     * It executes the program optimizing the diet.
     * @param args no argument is used.
     */
    public static void main(String args[]) {

      Diet diet = new Diet();
      diet.model();
      
      if (diet.searchOptimal()) {
          System.out.println("Cost: " + diet.cost.value());
          for(int i = 0; i < diet.m; i++) {
              System.out.println(diet.food[i] + ": " + diet.x[i].value());
          }
      }  else {
          System.out.println("No solution.");
      }

    } 

}
