/**
 *  Store.java 
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

import JaCoP.constraints.*;
import JaCoP.core.*;
import JaCoP.search.*;
import JaCoP.util.MDD;

import java.io.*;
import java.util.*;

/**
*
* CrossWord in JaCoP. It uses a simplified problem where words can be repeated
* in one crossword.
*
* @author : Radoslaw Szymanek
* 
* This program uses problem instances and dictionary obtained from Hadrien Cambazard.
*/
public class CrossWord extends Example {

    int r;          // number of rows
    int c;          // number of column
    char[][] matrix; // the clues matrix
    ArrayList<Integer> wordSizes;
    // * - black wall
    // letter - letter which must be in crossword
    // _ - unknown letter, any letter is accepted.
    
    Variable[][] x;      // the solution
    Variable blank;
    
    String defaultDictionary = "./ExamplesJaCoP/words";

    HashMap<String, Integer> mapping = new HashMap<String, Integer>();
    HashMap<Integer, String> mappingReverse = new HashMap<Integer, String>();

    
  	HashMap<Integer, MDD> mdds = new HashMap<Integer, MDD>();
  	
    /**
     *
     *  model()
     *
     */
    @Override
	public void model() {

        store = new FDstore();

        mapping.put("q", 1); 
        mapping.put("w", 2);
        mapping.put("e", 3);
        mapping.put("r", 4);
        mapping.put("t", 5);
        mapping.put("z", 6);
        mapping.put("u", 7);
        mapping.put("i", 8);
        mapping.put("o", 9);
        mapping.put("p", 10);
        mapping.put("a", 11);
        mapping.put("s", 12);
        mapping.put("d", 13);
        mapping.put("f", 14);
        mapping.put("g", 15);
        mapping.put("h", 16);
        mapping.put("j", 17);
        mapping.put("k", 18);
        mapping.put("l", 19);
        mapping.put("y", 20);
        mapping.put("x", 21);
        mapping.put("c", 22);
        mapping.put("v", 23);
        mapping.put("b", 24);
        mapping.put("n", 25);
        mapping.put("m", 26);

        
        mappingReverse.put(1,"q"); 
        mappingReverse.put(2,"w");
        mappingReverse.put(3,"e");
        mappingReverse.put(4,"r");
        mappingReverse.put(5,"t");
        mappingReverse.put(6,"z");
        mappingReverse.put(7,"u");
        mappingReverse.put(8,"i");
        mappingReverse.put(9,"o");
        mappingReverse.put(10,"p");
        mappingReverse.put(11,"a");
        mappingReverse.put(12,"s");
        mappingReverse.put(13,"d");
        mappingReverse.put(14,"f");
        mappingReverse.put(15,"g");
        mappingReverse.put(16,"h");
        mappingReverse.put(17,"j");
        mappingReverse.put(18,"k");
        mappingReverse.put(19,"l");
        mappingReverse.put(20,"y");
        mappingReverse.put(21,"x");
        mappingReverse.put(22,"c");
        mappingReverse.put(23,"v");
        mappingReverse.put(24,"b");
        mappingReverse.put(25,"n");
        mappingReverse.put(26,"m");

        blank = new Variable(store, "blank", 1, 26);
        
        if (matrix == null) {

            System.out.println("Using the default problem.");

            char[][] matrixTemp = {{'_', 's', '_', '_', '_'},
                                   {'_', 'a', '_', '_', '_'},
                                   {'_', '_', '_', '_', '_'},
                                   {'a', '_', '_', '_', '_'},
                                   {'_', '_', '_', '_', '_'}};
            	
            r = 5;
            c = 5;
            matrix = matrixTemp;
            wordSizes = new ArrayList<Integer>();

            wordSizes.add(5);

            x = new Variable[matrix.length][];
            for (int i = 0; i < matrix.length; i++)
            	x[i] = new Variable[matrix[i].length];
        }
        
        readDictionaryFromFile(defaultDictionary, wordSizes);
        
        
        //
        // initiate structures and variables
        //
        
        for(int i = 0; i < r; i++) {
            for(int j = 0; j < c; j++) {
                x[i][j] = new FDV(store, "x_" + i + "_" + j, 1, 26);
                if (matrix[i][j] != '_') {
                    store.impose(new XeqC(x[i][j], mapping.get(String.valueOf(matrix[i][j]))));
                }
            }
        }
        
        for (int i = 0; i < r; i++) {
        	
        	ArrayList<Variable> word = new ArrayList<Variable>();

            for(int j = 0; j < c; j++) {

            	if (matrix[i][j] == '*') {
            		if (wordSizes.contains(word.size())) {
            			MDD mdd4word = mdds.get(word.size()).reuse(word.toArray(new Variable[0]));
            			store.impose(new ExtensionalSupportMDD(mdd4word));
            		}
            		// System.out.println(word);
            		word.clear();
            	}
            	else
            		word.add(x[i][j]);
            	
            }

        	if (word.size() > 0) {
        		if (wordSizes.contains(word.size())) {
            		MDD mdd4word = mdds.get(word.size()).reuse(word.toArray(new Variable[0]));
        			store.impose(new ExtensionalSupportMDD(mdd4word));
            		// System.out.println(word);
        		}
        		// System.out.println(word);
        		word.clear();		
        	}
        	
        }

        for(int j = 0; j < c; j++) {

        	ArrayList<Variable> word = new ArrayList<Variable>();

            for(int i = 0; i < r; i++) {

            	if (matrix[i][j] == '*') {
            		if (wordSizes.contains(word.size())) {
            			MDD mdd4word = mdds.get(word.size()).reuse(word.toArray(new Variable[0]));
            			store.impose(new ExtensionalSupportMDD(mdd4word));
            			// System.out.println(word);
            		}
            		word.clear();
            	}
            	else
            		word.add(x[i][j]);

            }

        	if (word.size() > 0) {
        		if (wordSizes.contains(word.size())) {
        			MDD mdd4word = mdds.get(word.size()).reuse(word.toArray(new Variable[0]));
        			store.impose(new ExtensionalSupportMDD(mdd4word));
        			// System.out.println(word);
        		}
        		word.clear();		
        	}

        }

        vars = new ArrayList<Variable>();

        for(int i = 0; i < r; i++) 
            for(int j = 0; j < c; j++)
            	vars.add(x[i][j]);
                    
    }


    /**
     * It prints a variable matrix.
     */
    public void printSolution() {

    	System.out.println();
        for(int i = 0; i < r; i++) {
            for(int j = 0; j < c; j++) {
                System.out.print(mappingReverse.get(x[i][j].value() ) + " ");
            }
            System.out.println();
        }

    } 
    
    /**
     * It reads a dictionary. For every word length specified 
     * it reads a dictionary and creates an MDD representation 
     * of it for use by an extensional constraint.
     * 
     * @param file filename containing dictionary
     * @param wordSizes  
     */
    public void readDictionaryFromFile(String file, ArrayList<Integer> wordSizes) {

    	for (int wordSize : wordSizes) {

			int wordCount = 0;

    		Variable[] list = new Variable[wordSize];
    		for (int i = 0; i < wordSize; i++)
    			list[i] = blank;

			int[] tupleForGivenWord = new int[wordSize];
    		MDD resultForWordSize = new MDD(list);
    		
    		try {

    			BufferedReader inr = new BufferedReader(new FileReader(file));
    			String str;
    			int lineCount = 0;

    			
    			while ((str = inr.readLine()) != null && str.length() > 0) {
                
    				str = str.trim();
                
    				// ignore comments
    				// starting with either # or %
    				if(str.startsWith("#") || str.startsWith("%")) {
    					continue;
    				}

    				if (str.length() != wordSize)
    					continue;
    				
    				for (int i = 0; i < wordSize; i++) {
    					tupleForGivenWord[i] = mapping.get(str.substring(i, i+1));
    				}

    				wordCount++;
    				resultForWordSize.addTuple(tupleForGivenWord);
    				
    				lineCount++;

    			} // end while

    			inr.close();

    		}
    		catch (IOException e) {
    			System.out.println(e);
    		}
        
    		System.out.println("There are " + wordCount + " words of size " + wordSize);
    		mdds.put(wordSize, resultForWordSize);
    	}
    
    }
    	
    
	/**
	 * It searches for all solutions. It does not record them and prints 
	 * every tenth of them.
	 * 
	 * @return true if any solution was found, false otherwise.
	 */
	public boolean searchAllAtOnceNoRecord() {
		
		long T1, T2;
		T1 = System.currentTimeMillis();		
		
		SelectChoicePoint select = new SimpleSelect(vars.toArray(new Variable[1]),
				new SmallestDomain(), new IndomainMin());

		Search search = new DepthFirstSearch();
		search.setSolutionListener(new PrintListener());
		
		search.getSolutionListener().searchAll(true);
		search.getSolutionListener().recordSolutions(false);
		search.setAssignSolution(true);
		
		boolean result = search.labeling(store, select);

		T2 = System.currentTimeMillis();

		if (result) {
			System.out.println("Number of solutions " + search.getSolutionListener().solutionsNo());
			search.printAllSolutions();
		} 
		else
			System.out.println("Failed to find any solution");

		System.out.println("\n\t*** Execution time = " + (T2 - T1) + " ms");

		return result;
	}	    
    
	
	
    /**
     *  It executes the program to create a model and solve
     *  crossword problem. 
     *  
     *  @todo Add additional parameter which allows to specify the crossword.
     *  
     *  @param args no arguments used.
     *
     */
    public static void main(String args[]) {

        String filename = "";
        if (args.length == 1) {
            filename = args[0];
            System.out.println("Using file " + filename);
        }

        CrossWord m = new CrossWord();
        
        m.model();
        
		long T1, T2;
		T1 = System.currentTimeMillis();
		
        m.searchAllAtOnceNoRecord();

        T2 = System.currentTimeMillis();

		System.out.println("\n\t*** Execution time = " + (T2 - T1) + " ms");
        
    } // end main

	/**
	 * It is a simple print listener to print every tenth solution encountered.
	 */
	public class PrintListener extends SimpleSolutionListener {

		@Override
		public boolean executeAfterSolution(Search search, SelectChoicePoint select) {

			boolean returnCode = super.executeAfterSolution(search, select);
			
			if (noSolutions % 10 == 0) {
				System.out.println("Solution # " + noSolutions);
				printSolution();
			}
			
			return returnCode;
		}

		
	}
	
} // end class

