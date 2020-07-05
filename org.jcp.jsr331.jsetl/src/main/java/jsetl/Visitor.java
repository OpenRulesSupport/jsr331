package jsetl;

import jsetl.annotation.NotNull;
import jsetl.annotation.Nullable;

/**
 * Interface for the Visitor design pattern.
 * Implementations of this interface are visitors of the tree structure of a
 * logical object.
 *
 * @see Visitable
 * @author Andrea Fois
 */
interface Visitor {

    ///////////////////////////////////////////
    /////// VISIT JAVA OBJECT /////////////////
    ///////////////////////////////////////////

    /**
     * Visits an {@code Object}.
     * The standard implementation of this method should be
     * {@code
     *      if(object != null && object instanceof Visitable)
     *          return ((Visitable) object).accept(this);
     *      else
     *          return doSomethingWithObject(object);
     * }
     * @param object object to visit.
     * @return the desired {@code Object}.
     */
    @Nullable Object visit(@Nullable Object object);


    ///////////////////////////////////////////
    /////// VISIT CONSTRAINTS /////////////////
    ///////////////////////////////////////////

    /**
     * Visits an {@code AConstraint}.
     * @param aConstraint object to visit.
     * @return the desired {@code Object}.
     */
    @Nullable Object visit(@NotNull AConstraint aConstraint);

    /**
     * Visits a {@code ConstraintClass}.
     * @param constraint object to visit.
     * @return the desired {@code Object}.
     */
    @Nullable Object visit(@NotNull ConstraintClass constraint);


    ///////////////////////////////////////////
    /////// VISIT LOGICAL VARIABLES ///////////
    ///////////////////////////////////////////

    /**
     * Visits a {@code LVar}.
     * @param lVar object to visit.
     * @return the desired {@code Object}.
     */
    @Nullable Object visit(@NotNull LVar lVar);

    /**
     * Visits a {@code BoolLVar}.
     * @param boolLVar object to visit.
     * @return the desired {@code Object}.
     */
    @Nullable Object visit(@NotNull BoolLVar boolLVar);

    /**
     * Visits an {@code IntLVar}.
     * @param intLVar object to visit.
     * @return the desired {@code Object}.
     */
    @Nullable Object visit(@NotNull IntLVar intLVar);

    /**
     * Visits a {@code SetLVar}.
     * @param setLVar object to visit.
     * @return the desired {@code Object}.
     */
    @Nullable Object visit(@NotNull SetLVar setLVar);


    ///////////////////////////////////////////
    /////// VISIT LOGICAL LISTS ///////////////
    ///////////////////////////////////////////

    /**
     * Visits a {@code LList}.
     * @param lList object to visit.
     * @return the desired {@code Object}.
     */
    @Nullable Object visit(@NotNull LList lList);

    /**
     * Visits a {@code LPair}.
     * @param lPair object to visit.
     * @return the desired {@code Object}.
     */
    @Nullable Object visit(@NotNull LPair lPair);


    ///////////////////////////////////////////
    /////// VISIT LOGICAL SETS ////////////////
    ///////////////////////////////////////////

    /**
     * Visits a {@code LSet}.
     * @param lSet object to visit.
     * @return the desired {@code Object}.
     */
    @Nullable Object visit(@NotNull LSet lSet);

    /**
     * Visits an {@code IntLSet}.
     * @param intLSet object to visit.
     * @return the desired {@code Object}.
     */
    @Nullable Object visit(@NotNull IntLSet intLSet);

    /**
     * Visits a {@code LRel}.
     * @param lRel object to visit.
     * @return the desired {@code Object}.
     */
    @Nullable Object visit(@NotNull LRel lRel);

    /**
     * Visits a {@code LMap}.
     * @param lMap object to visit.
     * @return the desired {@code Object}.
     */
    @Nullable Object visit(@NotNull LMap lMap);

    /**
     * Visits a {@code Ris}.
     * @param ris object to visit.
     * @return the desired {@code Object}.
     */
    @Nullable Object visit(@NotNull Ris ris);

    /**
     * Visits a {@code CP}.
     * @param cp object to visit.
     * @return the desired {@code Object}.
     */
    @Nullable Object visit(@NotNull CP cp);
}
