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
 * CmpIntervalsByGlb.java
 * @version 2.3
 *
 */
 
package jsetl.comparator;

import jsetl.Interval;

import java.util.Comparator;

/**
 * This class implements a Comparator on objects of class 
 * <code>Interval</code>, based on intervals greatest lower bound.
 * 
 * @see Interval
 * 
 * @author Roberto Amadini.
 */
public class CmpIntervalsByGlb implements Comparator<Interval> {

    @Override
    public int 
    compare(Interval i0, Interval i1) {
        if (i0.equals(i1))
            return 0;
        if (i0.isEmpty())
            if (i1.isEmpty())
                return 0;
            else
                return -1;
        else if (i1.isEmpty())
            return 1;
        int glb0 = i0.getGlb();
        int glb1 = i1.getGlb();
        if (glb0 < glb1)
            return -1;
        else if (glb0 == glb1) 
            if (i0.getLub() < i1.getLub())
                return -1;
            else
                return 1;
        else
            return 1;
    }
}
