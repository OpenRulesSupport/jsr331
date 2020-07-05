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
 * CmpIntLVarDomByLub.java
 * @version 2.3
 *
 */

package jsetl.comparator;

import jsetl.IntLVar;
import jsetl.Interval;

import java.util.Comparator;

/**
 * This class implements a comparator for <code>IntLVar</code> based on the least upper bound of their domains.
 * @author Pandan //TODO who????
 *
 **/

public class CmpIntLVarDomByLub implements Comparator<IntLVar> {

    @Override
    public int 
    compare(IntLVar arg0, IntLVar arg1) {
        Interval i0 = arg0.getDomain().convexClosure();
        Interval i1 = arg1.getDomain().convexClosure();
        CmpIntervalsByLub comp = new CmpIntervalsByLub();
        return comp.compare(i0, i1);
    }

}
