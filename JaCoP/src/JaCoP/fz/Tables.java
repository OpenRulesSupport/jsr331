/**
 *  Tables.java 
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import JaCoP.core.Variable;
import JaCoP.core.Store;
import JaCoP.set.core.Set;
import JaCoP.set.core.SetDomain;

/**
 * 
 * This class contains information about all variables, including 
 * the variables which are used by search. 
 * 
 * @author Krzysztof Kuchcinski
 *
 */
public class Tables {

    Store store;
    Variable zero, one;

    // intTable keeps both int & bool (0=false, 1=true) parameters
    HashMap<String, Integer> intTable = new HashMap<String, Integer>();
	
    HashMap<String, int[]> intArrayTable = new HashMap<String, int[]>(); // boolean are also stored here as 0/1 values

    HashMap<String, Set> setTable = new HashMap<String, Set>();

    HashMap<String, Set[]> setArrayTable = new HashMap<String, Set[]>();

    HashMap<String, Variable> variableTable = new HashMap<String, Variable>();

    HashMap<String, Variable[]> variableArrayTable = new HashMap<String, Variable[]>();

    HashMap<String, Variable> setVariableTable = new HashMap<String, Variable>();

    HashMap<String, Variable[]> setVariableArrayTable = new HashMap<String, Variable[]>();

    ArrayList<Variable> outputVariables = new ArrayList<Variable>();

    ArrayList<OutputArrayAnnotation> outputArray = new ArrayList<OutputArrayAnnotation>();

    ArrayList<Variable> defaultSearchVariables = new ArrayList<Variable>();

    ArrayList<Variable[]> defaultSearchArrays = new ArrayList<Variable[]>();

    ArrayList<Variable> defaultSearchSetVariables = new ArrayList<Variable>();

    ArrayList<Variable[]> defaultSearchSetArrays = new ArrayList<Variable[]>();

    /**
     * It constructs the storage object to store different objects, like int, array of ints, sets, ... . 
     */
    public Tables() {}

    public Tables(Store s) {
	this.store = s;
	this.zero = new Variable(store, "zero", 0,0);
	this.one = new Variable(store, "one", 1,1);
    }
    /**
     * It adds an int parameter.
     * 
     * @param ident the identity of the added int parameter.
     * @param val the value of the parameter.
     */
    public void addInt(String ident, int val) {
	intTable.put(ident, val);
    }
    /**
     * It returns an int parameter of a given identity.
     * 
     * @param ident the identify of the parameter.
     * @return the int value of the specified parameter.
     */
    public int getInt(String ident) {
	Integer iVal = intTable.get(ident);
	if (iVal != null)
	    return iVal.intValue();
	else {
	    System.err.println("Symbol \""+ident+ "\" does not have assigned value when refered; execution aborted");
	    System.exit(0);
	    return -1;
	}
    }

    /**
     * It stores an int array.
     * 
     * @param ident the identity of the stored array.
     * @param array the array being stored.
     */
    public void addIntArray(String ident, int[] array) {
	// TODO, asserts to prevent multiple array being put with the same identity?
	// assert ( intArrayTable.get(ident) == null ) : "The int array with identity " + ident + " already exists ";
	intArrayTable.put(ident, array);
    }
	
    /**
     * It obtains the int array of the given unique identity.
     * 
     * @param ident the identity of the required array.
     * @return the int array with the specified identity.
     */
    public int[] getIntArray(String ident) {
	return intArrayTable.get(ident);
    }

    /**
     * It adds a set of the given identity. 
     * 
     * @param ident the identity of the set being added. 
     * @param val the set being added.
     */
    public void addSet(String ident, Set val) {
	setTable.put(ident, val);
    }
	
    /**
     * It returns the set of the given identity.
     * 
     * @param ident the identity of the searched set.
     * 
     * @return the set of the given identity.
     */
    public Set getSet(String ident) {
	return setTable.get(ident);
    }

    /**
     * It adds the set array to the storage. 
     * 
     * @param ident the identity of the added set array.
     * @param array the array being added.
     */
    public void addSetArray(String ident, Set[] array) {
	setArrayTable.put(ident, array);
    }
    /**
     * 
     * It returns the set array of the given id.
     * @param ident the unique id of the looked for set array.
     * @return the set array of the given identity. 
     */
    public Set[] getSetArray(String ident) {
	return setArrayTable.get(ident);
    }

    /**
     * It adds a variable with a given identity to the storage. 
     * 
     * @param ident the identity of the added variable. 
     * @param var the variable being added.
     */
    public void addVariable(String ident, Variable var) {
	variableTable.put(ident, var);
    }
	
	
    /**
     * It returns the variable of the given identity. 
     * 
     * @param ident the identity of the returned variable.
     * @return the variable of the given identity.
     */
    public Variable getVariable(String ident) {
	return variableTable.get(ident);
    }

    /**
     * It adds a variable array to the storage.
     * 
     * @param ident the identity of the added variable array.
     * @param array the array of variables being added.
     */
    public void addVariableArray(String ident, Variable[] array) {
	variableArrayTable.put(ident, array);
    }
	
	
    /**
     * It returns the variable array of the given identity.
     * 
     * @param ident the identity of the returned variable array.
     * @return the variable array of the given identity.
     */
    public Variable[] getVariableArray(String ident) {
	Variable[]  a = variableArrayTable.get(ident);
	if (a == null) {
	    int[] intA = intArrayTable.get(ident);
	    a = new Variable[intA.length];
	    for (int i =0; i<intA.length; i++) {
		if (intA[i] == 0) a[i] = zero;
		else if (intA[i] == 1) a[i] = one;
		else
		    a[i] = new Variable(store, intA[i], intA[i]);
	    }
	}
	return a;
    }

    /**
     * It adds the set variable of the given identity.
     * 
     * @param ident the identity of the added set variable.
     * @param var the set variable being added.
     */
    public void addSetVariable(String ident, Variable var) {
	setVariableTable.put(ident, var);
    }
	
	
    /**
     * It returns the set variable of the given identity.
     * 
     * @param ident the identity of the returned set variable.
     * @return the set variable of the given identity.
     */
    public Variable getSetVariable(String ident) {
	return setVariableTable.get(ident);
    }

    /**
     * It stores the array of the set variables with the specified identity.
     * 
     * @param ident the identity of the stored array of set variables.
     * @param array the array of set variables being added.
     */
    public void addSetVariableArray(String ident, Variable[] array) {
	setVariableArrayTable.put(ident, array);
    }
	
	
    /**
     * It returns the array of set variables of the given identity.
     * 
     * @param ident the identity of the returned array of set variables. 
     * @return the array of set variables with the given identity.
     */
    public Variable[] getSetVariableArray(String ident) {
	Variable[] a = setVariableArrayTable.get(ident);
	if (a == null) {
	    Set[] setA = setArrayTable.get(ident);
	    a = new Variable[setA.length];
	    for (int i =0; i<setA.length; i++) {
		a[i] = new Variable(store, "", new SetDomain(setA[i], setA[i]));
	    }
	}
	return a;
    }


    /**
     * It adds an output variable.
     * @param v the output variable being added.
     */
    public void addOutVar(Variable v) { outputVariables.add(v); }
	
	
    /**
     * It adds an output array annotation. 
     * 
     * @param v the output array annotation being added.
     */
    public void addOutArray(OutputArrayAnnotation v) { outputArray.add(v); }

    /**
     * It adds a search variable. 
     * 
     * @param v the search variable being added.
     */
    public void addSearchVar(Variable v) { defaultSearchVariables.add(v); }
	
	
    /**
     * It adds a search array.
     * @param v the search array being added.
     */
    public void addSearchArray(Variable[] v) { defaultSearchArrays.add(v); }

    /**
     * It adds a search set variable. 
     * 
     * @param v the set search variable being added.
     */
    public void addSearchSetVar(Variable v) { defaultSearchSetVariables.add(v); }
	
    /**
     * It adds an array of search set variables.
     * @param v
     */
    public void addSearchSetArray(Variable[] v) { defaultSearchSetArrays.add(v); }


    // StringBuilder to be used instead of normal string additions. 
	
    @SuppressWarnings("unchecked")
	public String toString() {
		
	HashMap[] dictionary = { intTable,
				 intArrayTable,
				 setTable,
				 setArrayTable,
				 variableTable,
				 variableArrayTable,
				 setVariableTable,
				 setVariableArrayTable
	};
		
	int indexIntArray = 1;
	int indexSetArray = 3;
	int indexVariableArray = 5;
	int indexSetVariableArray = 7;
		
	String s = "";
	for (int i=0; i<dictionary.length; i++) {

	
		
	    // int array
	    if (i == indexIntArray) {
		s+="Int arrays\n";
		s +="{";
		java.util.Set<String> keys = dictionary[i].keySet();
		for (String k : keys) {
		    int[] a = (int[])dictionary[i].get(k);
		    s += k+"=[";
		    for (int j=0; j<a.length; j++) {
			s += a[j];
			if (j < a.length-1)
			    s += ", ";
		    }
		    s += "], ";
		}
		s+="}\n";
	    }
	    // Set Array
	    else if (i == indexSetArray) {
		s+="Set arrays\n";
		s +="{";
		java.util.Set<String> keys = dictionary[i].keySet();
		for (String k : keys) {
		    s += k+"=";
		    Set[] a = (Set[])dictionary[i].get(k);
		    s += Arrays.asList(a);
		    s += ", ";
		}
		s+="}\n";
	    }
	    // Variable Array
	    else if (i == indexVariableArray) {
		s+="Var arrays\n";
		s +="{";
		java.util.Set<String> keys = dictionary[i].keySet();
		for (String k : keys) {
		    Variable[] a = (Variable[])dictionary[i].get(k);
		    s += k+"=";
		    s += Arrays.asList(a);
		    s += ", ";
		}
		s+="}\n";
	    }
	    // Set Variables Array
	    else if (i == indexSetVariableArray) {
		s+="Set var arrays\n";
		s +="{";
		java.util.Set<String> keys = dictionary[i].keySet();
		for (String k : keys) {
		    Variable[] a = (Variable[])dictionary[i].get(k);
		    s += k+"=";
		    s += Arrays.asList(a);
		    s += ", ";
		}
		s+="}\n";
	    }
	    // others
	    else
		s += dictionary[i] +"\n";
	}

	s += "Output variables = "+ outputVariables+"\n";
	s += "Output arrays = [";//+ outputArray+"\n";
	for (int i=0; i<outputArray.size(); i++) {
	    OutputArrayAnnotation a = outputArray.get(i);
	    s += a;
	    s += ", ";
	}
	s += "]\n";
	s += "Search variables = "+ defaultSearchVariables+"\n";
	s += "Search arrays = ["; //+ defaultSearchArrays+"\n";
	for (int i=0; i<defaultSearchArrays.size(); i++) {
	    Variable[] a = defaultSearchArrays.get(i);
	    s += Arrays.asList(a);
	    s += ", ";
	}
	s += "]\n";
	s += "Search set variables = "+ defaultSearchSetVariables+"\n";
	s += "Search set arrays = ["; //+ defaultSearchArrays+"\n";
	for (int i=0; i<defaultSearchSetArrays.size(); i++) {
	    Variable[] a = defaultSearchSetArrays.get(i);
	    s += Arrays.asList(a);
	    s += ", ";
	}
	s += "]\n";
	return s;
    }
}
