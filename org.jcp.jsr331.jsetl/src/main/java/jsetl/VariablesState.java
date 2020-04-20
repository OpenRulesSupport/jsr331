package jsetl;

import jsetl.annotation.NotNull;

import java.util.*;

/**
 * The {@code VariablesState} class keeps track of the uninitialized
 * variables (and their domains) that are used during choice point creation.
 */

class VariablesState {

    ///////////////////////////////////////////////////////////////
    //////////////// DATA MEMBERS /////////////////////////////////
    ///////////////////////////////////////////////////////////////

    /**
     * List containing all uninitialized logical objects.
     */
    final ArrayList<LObject> notInitializedLObjects;

    /**
     * References to not initialized {@code IntLVar} domains.
     */
    final ArrayList<MultiInterval> intLVarDomains;
    
    /**
    * References to not initialized {@code SetLVar} domains.
    */
    final ArrayList<SetInterval> setLVarDomains;


    ///////////////////////////////////////////////////////////////
    //////////////// CONSTRUCTORS /////////////////////////////////
    ///////////////////////////////////////////////////////////////

    /**
     * Constructs a variable state which contains the given list of not initialized logical objects, logical variables
     * domains and finite sets variable domains. The precondition of this constructor is that the size of the three
     * lists it takes as parameters is the same and that {@code IntLVar}s and {@code SetLVar}s occur
     * at the same indexes as their domains in the respective lists.
     *
     * @param notInitializedLObjects list containing all uninitialized logical objects. It must not contain {@code null}
     *                               values.
     * @param intLVarDomains list containing {@code null} as placeholders and the actual domains in the positions
     *                       corresponding to {@code IntLVar}s in {@code notInitializedLObjects}.
     * @param setLVarDomains list containing {@code null} as placeholders and the actual domains in the positions
     *                       corresponding to {@code SetLVar}s in {@code notInitializedLObjects}.
     */
    protected VariablesState(@NotNull final ArrayList<LObject> notInitializedLObjects,
                             @NotNull final ArrayList<MultiInterval> intLVarDomains,
                             @NotNull final ArrayList<SetInterval> setLVarDomains) {

        assert notInitializedLObjects != null;
        assert intLVarDomains != null;
        assert setLVarDomains != null;
        assert notInitializedLObjects.stream().noneMatch(Objects::isNull);
        assert notInitializedLObjects.size() == intLVarDomains.size();
        assert notInitializedLObjects.size() == setLVarDomains.size();

        this.notInitializedLObjects = notInitializedLObjects;
        this.intLVarDomains = intLVarDomains;
        this.setLVarDomains = setLVarDomains;
    }
    
}
