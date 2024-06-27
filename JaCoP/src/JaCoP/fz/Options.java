/**
 *  Options.java 
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

package JaCoP.fz;

import java.io.FileInputStream;

/**
 * 
 * It parses the options provided to flatzinc parser/executable. It contains
 * information about all options used for a given flatzinc file. 
 * 
 * @author Krzysztof Kuchcinski
 *
 */
public class Options {

	String[] argument;
	
	FileInputStream file;
	
    boolean all = false, verbose = false;
	
	boolean statistics = false;
	
	int time_out = 0;
	
	int number_solutions = -1;

	/**
	 * It constructs an Options object and parses all the parameters/options provided 
	 * to flatzinc to jacop parser. 
	 * 
	 * @param args arguments to flatzinc to jacop parser.
	 */
	public Options(String[] args) {
		
		argument = args;
		
		if (args.length == 0) {
			System.out.println("fz2jacop: no model file specified");
			System.out.println("fz2jacop: use --help for more information.");
			System.exit(0);
		}
		else if (args.length == 1) {
			String arg = args[0];
			if (arg.equals("-h") || arg.equals("--help")) {
				System.out.println(
						"Usage: java Fz2jacop [<options>] <file>.fzn\n"+
						"Options:\n"+
						"    -h, --help\n"+
						"        Print this message.\n"+
						"    -a, --all-solutions\n"+
						"    -v, --verbose\n"+
						"    -t <value>, --time-out <value>\n"+
						"        <value> - time in second.\n"+
						"    -s, --statistics\n"+
						"    -n <value>, --num-solutions <value>\n"+
						"        <value> - limit on solution number.\n"
				);
				System.exit(0);
			}
			else { // input file
				try {
					file = new java.io.FileInputStream(args[0]);
				} catch (java.io.FileNotFoundException e) {
					System.out.println("Flatzinc2JaCoP Parser Version 0.1:  File " + args[0] + " not found.");
					System.exit(0);
				}
			}
		}
		else { // args.length > 1
			int i=0; 
			while (i<args.length-1) {
				// decode options
				if (args[i].equals("-a") || args[i].equals("--all-solutions")) {
					all = true;
					i++;
				}
				else if (args[i].equals("-t") || args[i].equals("--time-out")) {
					time_out = Integer.parseInt(args[++i]);
					i++;
				}
				else if (args[i].equals("-s") || args[i].equals("--statistics")) {
					statistics = true;
					i++;
				}
				else if (args[i].equals("-n") || args[i].equals("--num-solutions")) {
					number_solutions = Integer.parseInt(args[++i]);
					if (number_solutions > 1) 
					    all = true;
					i++;
				}
				else if (args[i].equals("-v") || args[i].equals("--verbose")) {
					    verbose = true;
					i++;
				}
				else {
					System.out.println("fz2jacop: not recognized option "+ args[i]);
					i++;
				}
			}	    
			try {
				file = new java.io.FileInputStream(args[args.length-1]);
			} catch (java.io.FileNotFoundException e) {
				System.out.println("Flatzinc2JaCoP Parser Version 0.1:  File " + args[0] + " not found.");
				System.exit(0);
			}
		}
	}

	/**
	 * It returns the file input stream for the file containing flatzinc description. 
	 * @return file containing flatzinc description.
	 */
	public FileInputStream getFile() {
		return file;
	}

	/**
	 * It returns true if the search for all solution has been requested.
	 * @return true if the search for all solution should take place, false otherwise. 
	 */
	public boolean getAll() {
		return all;
	}

	/**
	 * It returns true if the verbose mode has been requested.
	 * @return true if the verbose mode is active, false otherwise. 
	 */
	public boolean getVerbose() {
		return verbose;
	}

	/**
	 * It returns true if the search statistics are to be displayed. 
	 * 
	 * @return true if the search statistics are to be displayed, false otherwise.
	 */
	public boolean getStatistics() {
		return statistics;
	}

	/**
	 * It returns time out set for the search. 
	 * 
	 * @return the value of the timeOut (in seconds), 0 if no time-out was set.
	 */
	public int getTimeOut() {
		return time_out;
	}

	/**
	 * It returns the number of solutions the solver should search for. 
	 * 
	 * @return the number of solutions the search should search for.
	 */
	public int getNumberSolutions() {
		return number_solutions;
	}
	
}









