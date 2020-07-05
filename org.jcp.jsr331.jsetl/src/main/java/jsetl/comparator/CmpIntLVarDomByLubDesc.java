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
 * CmpIntLVarDomByLubDesc.java
 * @version 2.3
 *
 */

package jsetl.comparator;

import jsetl.IntLVar;

import java.util.Comparator;

/**
 * This class implements a comparator for <code>IntLVar</code> based on the least upper bound of their domains, in descending order.
 * @author Pandan //TODO who????
 *
 **/

public class CmpIntLVarDomByLubDesc implements Comparator<IntLVar> {

    @Override
    public int 
    compare(IntLVar arg0, IntLVar arg1) {
        CmpIntLVarDomByLub comp = new CmpIntLVarDomByLub();
        return -comp.compare(arg0, arg1);
    }

}
