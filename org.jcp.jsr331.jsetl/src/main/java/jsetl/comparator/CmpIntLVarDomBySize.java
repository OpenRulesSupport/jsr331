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
 * CmpIntLVarDomBySize.java
 * @version 2.3
 *
 */

package jsetl.comparator;

import jsetl.IntLVar;

import java.util.Comparator;

/**
 * This class implements a comparator for <code>IntLVar</code> based on the sizes of their domains.
 * @author Pandan //TODO who????
 *
 **/

public class CmpIntLVarDomBySize implements Comparator<IntLVar> {

    @Override
    public int 
    compare(IntLVar arg0, IntLVar arg1) {
        int s0 = arg0.getDomain().size();
        int s1 = arg1.getDomain().size();
        if (s0 < s1)    
            return -1;
        else if (s0 > s1)
            return 1;
        else
            return 0;
    }

}
