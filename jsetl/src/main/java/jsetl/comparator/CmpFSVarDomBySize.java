/**
 * jsetl � A Java library that combines the object-oriented
 * programming paradigm with valuable concepts of CLP languages
 *
 * Copyright (C) 2000-2012 jsetl Team.
 *
 * jsetl is distributed under the terms of the GNU Lesser General
 * Public License.
 */

/**
 * CmpFSVarDomBySize.java
 * @version 2.3
 *
 */

package jsetl.comparator;

import jsetl.SetInterval;
import jsetl.SetLVar;

import java.util.Comparator;

/**
 * Comparator used to compare <code>SetLVar</code>s by comparing the sizes of their domains.
 * @author Pandini
 */

public class CmpFSVarDomBySize implements Comparator<SetLVar> {
	/**
	 * Compares two <code>SetLVar</code> using the comparison between the sizes of their domains
	 * @param arg0 first argument of comparison
	 * @param arg1 second argument of comparison
	 * @return -1 if <code>arg0</code> is less than <code>argument1</code>,
	 * 			0 if they are equal, 1 if <code>arg0</code> is greater than <code>argument1</code>
	 */
	@Override
	public int 
	compare(SetLVar arg0, SetLVar arg1) {
		SetInterval dom0 = arg0.getDomain();
		SetInterval dom1 = arg1.getDomain();
		int s0 = dom0.getLub().size() - dom0.getGlb().size();
		int s1 = dom1.getLub().size() - dom1.getGlb().size();
		if (s0 < s1)	
			return -1;
		else if (s0 > s1)
			return 1;
		else
			return 0;
	}

}
