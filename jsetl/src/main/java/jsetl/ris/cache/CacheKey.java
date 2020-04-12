package jsetl.ris.cache;

import jsetl.Constraint;
import jsetl.LObject;
import jsetl.annotation.NotNull;
import jsetl.annotation.Nullable;

/**
 * Instances of this class are the keys of the Ris Expansion Cache.
 * The keys are quadruples of the form {@code [controlTerm, element, filter, pattern]},
 * in which {@code controlTerm, filter, pattern} are the control term, filter and pattern of a {@code Ris}
 * and {@code element} is an element from the domain of such {@code Ris}.
 *
 * @author Andrea Fois
 * @see jsetl.Ris
 * @see CacheValue
 * @see CacheEntry
 */
public class CacheKey {

    ///////////////////////////////////////////////////////////////
    ///////// DATA MEMBERS ////////////////////////////////////////
    ///////////////////////////////////////////////////////////////

    /**
     * Control term of a {@code Ris}.
     */
    private final LObject controlTerm;

    /**
     * Element of the domain of a {@code Ris}.
     */
    private final Object element;

    /**
     * Filter of a {@code Ris}.
     */
    private final Constraint filter;

    /**
     * Pattern of a {@code Ris}.
     */
    private final Object pattern;


    ///////////////////////////////////////////////////////////////
    ///////// CONSTRUCTORS ////////////////////////////////////////
    ///////////////////////////////////////////////////////////////

    /**
     * Constructs a {@code CacheKey} with the given {@code controlTerm}, {@code element}, {@code filter}, {@code pattern}.
     * @param controlTerm control term of a {@code Ris}.
     * @param element element of the domain of a {@code Ris}.
     * @param filter filter of a {@code Ris}.
     * @param pattern pattern of a {@code Ris}.
     */
    public CacheKey(@NotNull final LObject controlTerm, @NotNull final Object element, @NotNull final Constraint filter, @NotNull final Object pattern) {
        assert controlTerm != null;
        assert element != null;
        assert filter != null;
        assert pattern != null;

        this.controlTerm = controlTerm;
        this.element = element;
        this.filter = filter;
        this.pattern = pattern;
    }


    ///////////////////////////////////////////////////////////////
    ///////// OVERRIDDEN METHODS //////////////////////////////////
    ///////////////////////////////////////////////////////////////

    /**
     * Checks if {@code this} and {@code obj} are equal. They are equal if and only if
     * they have the same (in the sense of the operator {@code ==}) control term, element, filter and pattern.
     * @param obj other object.
     * @return {@code true} if {@code this} equals {@code obj}, {@code false} otherwise.
     */
    @Override
    public boolean equals(@Nullable Object obj) {
        if(obj == null)
            return false;

        if(this == obj)
            return true;

        if(!(obj instanceof CacheKey))
            return false;

        CacheKey ck = (CacheKey) obj;
        return controlTerm == ck.controlTerm
                && element == ck.element
                && filter == ck.filter
                && pattern == ck.pattern;
    }

    /**
     * Returns an integer containing the hash code of {@code this}.
     * @return the hash code of {@code this}.
     */
    @Override
    public int hashCode() {
        return controlTerm.hashCode() + element.hashCode() + filter.hashCode() + pattern.hashCode();
    }
}
