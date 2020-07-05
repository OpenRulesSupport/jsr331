package jsetl.lib;

import jsetl.IntLVar;

import java.util.Arrays;

public class IntLVars {

    /**
     * Returns an array of {@code n IntLVar}s.
     * @param n the length of the constructed array.
     * @return the constructed array.
     */
    public static IntLVar[] array(int n){
        if(n < 0)
            throw new IllegalArgumentException("n must be zero or positive");
        IntLVar[] array = new IntLVar[n];
        Arrays.setAll(array, index -> new IntLVar());

        return array;
    }
}
