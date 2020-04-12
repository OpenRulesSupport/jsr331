package jsetl;

import jsetl.annotation.NotNull;
import jsetl.ris.cache.RisExpansionCache;

/**
 * Objects of type {@code ChoicePoint} are used to store choice points.
 * The list of all uninitialized variables to restore is stored in {@code varsState}.
 * The constraint store to restore when backtracking (i.e. with the atomic constraint that opened the
 * alternative already updated with the next alternative) stored in {@code constraintStore}.
 * The choice point also has a copy of the {@code Ris} expansion cache.
 */
class ChoicePoint {

    ///////////////////////////////////////////////////////////////
    //////////////// DATA MEMBERS /////////////////////////////////
    ///////////////////////////////////////////////////////////////

    /**
     * Variables state to restore.
     */    
    final VariablesState varsState;

    /**
     * Constraint store to restore.
     */     
    final ConstraintStore constraintStore;

    /**
     * Copy of the {@code Ris} expansion cache.
     */
    final RisExpansionCache risExpansionCache;


    ///////////////////////////////////////////////////////////////
    //////////////// CONSTRUCTORS /////////////////////////////////
    ///////////////////////////////////////////////////////////////

    /**
     * Creates a new instance of {@code ChoicePoint} with the specified variables state,
     * constraint store and {@code Ris} expansion cache.
     *
     * @param varsState variables state to restore.
     * @param constraintStore the constraint store to restore.
     * @param risExpansionCache copy of the {@code Ris} expansion cache.
     */
    ChoicePoint(@NotNull final VariablesState varsState, @NotNull final ConstraintStore constraintStore, @NotNull final RisExpansionCache risExpansionCache) {
        assert varsState != null;
        assert constraintStore != null;
        assert risExpansionCache != null;

        this.varsState  = varsState;
        this.constraintStore = constraintStore;
        this.risExpansionCache = risExpansionCache;
    }

}
