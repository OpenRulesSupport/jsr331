package jsetl;

import jsetl.annotation.NotNull;
import jsetl.annotation.Nullable;

/**
 * Interface needed for the Visitor design pattern.
 * Classes implementing this interface are visitable by an object of type {@code Visitor}.
 *
 * @see Visitor
 * @author Andrea Fois
 */
interface Visitable {

    /**
     * The implementation of this method should be {@code return visitor.visit(this);}, and should be
     * re-implemented in each class, to ensure that the double-dispatch used by the Visitor design pattern
     * works as intended.
     *
     * @param visitor the visitor that wants to visit the object.
     * @return the return of the visit of the {@code visitor}.
     * @see Visitor#visit
     */
    @Nullable Object accept(@NotNull Visitor visitor);
}

