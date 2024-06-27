/**
 *  OutputArrayAnnotation.java 
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
import JaCoP.core.Variable;
import JaCoP.set.core.Set;


/**
 * 
 * It stores information about the annotation for an output array. 
 * 
 * @author Krzysztof Kuchcinski
 *
 */
public class OutputArrayAnnotation {

    String id;
	
    // TODO, ArrayList of Sets? Why? Is a set needed? Maybe just IntervalDomain suffices? 
    ArrayList<Set> indexes;
    Variable[] array;

    /**
     * It constructs and output array annotation. 
     * 
     * @param name the name of the output array annotation. 
     * 
     * @param indexBounds the indexes bounds.
     */
    public OutputArrayAnnotation(String name, ArrayList<Set> indexBounds) {
	id = name;
	indexes = indexBounds;
    }

    String getName() {
	return id;
    }

    void setArray(Variable[] a) {
	array = a;
    }

    Variable[] getArray() {
	return array;
    }

    int getNumberIndexes() {
	return indexes.size();
    }

    Set getIndexes(int i) {
	return indexes.get(i);
    }

    public String toString() {

	StringBuilder s = new StringBuilder(id + " = array"+indexes.size() + "d(");

	for (int i=0; i<indexes.size(); i++) 
	    s.append(indexes.get(i).min()).append("..").append(indexes.get(i).max()).append(", ");

	s.append("[");
	for (int i=0; i<array.length; i++) {
	    Variable v = array[i];

	    if (v instanceof JaCoP.core.BooleanVariable) {
		if (v.singleton())
		    switch (v.value()) {
		    case 0: s.append("false");
			break;
		    case 1: s.append("true");
			break;
		    default: s.append(v.dom().toString());
		    }
	    }
	    else {
		s.append(v.dom().toString());
	    }
	    if (i<array.length-1) 
		s.append(", ");
	}
	s.append("]);");
	return s.toString();
    }
}
