package jsetl;

import jsetl.annotation.NotNull;
import jsetl.annotation.Nullable;

import java.util.Collection;
import java.util.Objects;

/**
 * This class implements set intervals, which are intervals like {@code Interval} but over sets of
 * integers instead of over integers. The partial ordering of the elements of set intervals is the "subset" relation.
 * Set intervals are intervals of the form {@code [A, B]} in which {@code A, B} are sets of integers.
 * If {@code A} is not a subset of {@code B} then the writing above represents the empty set.
 * If {@code A = B} the writing above represents the singleton {@code {A}}.
 * If {@code  A} is a (strict) subset of {@code B} then the interval represents the set of all sets of integers
 * that contain or are equal to {@code A} and that are contained by or equal to {@code B}.
 * More formally: {@code [A,B] = {X | A subset X subset B}}.
 * A set of integers {@code X} is an element of {@code [A,B]} if and only if
 * {@code X} is a subset of {@code B} and {@code A} is a subset of {@code X}.
 */
public class SetInterval implements Cloneable {

    ///////////////////////////////////////////////////////////////
    //////////////// STATIC METHODS ///////////////////////////////
    ///////////////////////////////////////////////////////////////

    /**
     * Infimum, least possible set (empty set).
     */
    public static final MultiInterval INF = new MultiInterval();

    /**
     * Supremum, greatest possible set (universe).
     */
    public static final MultiInterval SUP =
            new MultiInterval(-Interval.SUP / 2, Interval.SUP / 2);


    ///////////////////////////////////////////////////////////////
    //////////////// DATA MEMBERS /////////////////////////////////
    ///////////////////////////////////////////////////////////////

    /**
     * Greatest lower bound of the set interval.
     */
    private final MultiInterval glb;

    /**
     * Least upper bound of the set interval.
     */
    private final MultiInterval lub;


    ///////////////////////////////////////////////////////////////
    //////////////// CONSTRUCTORS /////////////////////////////////
    ///////////////////////////////////////////////////////////////

    /**
     * Constructs an empty set interval.
     */
    public 
    SetInterval() {
        glb = null;
        lub = null;
    }

    /**
     * Constructs a set interval which has only one element, i.e. {@code multiInterval}
     * If {@code multiInterval} is not a subset of the supremum, {@code SUP} the empty
     * set interval is created.
     * @param multiInterval the only element of the constructed set interval.
     */
    public
    SetInterval(@NotNull MultiInterval multiInterval) {
        Objects.requireNonNull(multiInterval);

        if (multiInterval.subset(SUP)) {
            MultiInterval tmp = multiInterval.clone();
            glb = tmp;
            lub = tmp;
        }
        else {
            glb = null;
            lub = null;
        }
    }

    /**
     * Creates a set interval of the form {@code [lowerBound, upperBound]}.
     * If {@code lowerBound} and {@code upperBound} are not both subsets of {@code SUP}
     * the empty set interval is created.
     * @param lowerBound greatest lower bound of the set interval.
     * @param upperBound least upper bound of the set interval.
     */
    public
    SetInterval(@NotNull MultiInterval lowerBound, @NotNull MultiInterval upperBound) {
        Objects.requireNonNull(lowerBound);
        Objects.requireNonNull(upperBound);

        if (!(lowerBound.subset(upperBound) && lowerBound.subset(SUP))) {
            glb = null;
            lub = null;
            return;
        }
        glb = lowerBound.clone();
        lub = upperBound.intersect(SUP).clone();
    }

    /**
     * Constructs the minimum set interval that contains all multi-intervals in {@code multiIntervals}.
     * @param multiIntervals collection of multi-intervals.
     */
    public
    SetInterval(@NotNull Collection<MultiInterval> multiIntervals) {
        Objects.requireNonNull(multiIntervals);
        if(multiIntervals.stream().anyMatch(Objects::isNull))
            throw new NullPointerException("NULL VALUES NOT ALLOWED");

        MultiInterval lb = SUP;
        MultiInterval ub = new MultiInterval();
        for (MultiInterval mi : multiIntervals) {
            if (!lb.isEmpty())
                lb = mi.intersect(lb);
            if (mi.subset(SUP))
                ub = mi.union(ub);
        }
        if (lb.subset(ub)) {
            glb = lb;
            lub = ub;
            return;
        }
        glb = null;
        lub = null;
    }

    ///////////////////////////////////////////////////////////////
    //////////////// PUBLIC METHODS ///////////////////////////////
    ///////////////////////////////////////////////////////////////

    /**
     * Tests whether {@code set} is an element of {@code this} set interval.
     * @param set the set of integer to test.
     * @return {@code true} if {@code set} is an element of {@code this}, {@code false} otherwise.
     */
    public boolean
    contains(@NotNull MultiInterval set) {
        Objects.requireNonNull(set);

        if (this.isEmpty())
            return false;
        else
            return glb.subset(set) && set.subset(lub);
    }

    /**
     * Checks whether {@code this} is the empty set or not.
     * @return {@code true} if {@code this} is the empty set, {@code false} otherwise.
     */
    public boolean
    isEmpty() {
        return glb == null;
    }

    /**
     * Checks whether {@code this} set interval is a singleton, i.e. contains exactly one element.
     * @return {@code true} if {@code this} is a singleton, {@code false} otherwise.
     */
    public boolean
    isSingleton() {
        return size() == 1;
    }

    /**
     * Checks whether {@code this} is the universe.
     * @return {@code true} if {@code this} is the universe, {@code false} otherwise.
     */
    public boolean
    isUniverse() {
        return this.equals(universe());
    }

    /**
     * Returns the number of elements in {@code this} set interval.
     * @return the size of {@code this}.
     */
    public double
    size() {
        if (isEmpty())
            return 0;
        double exp = lub.size() - glb.size();
        if (exp > Double.MAX_EXPONENT)
            return Double.POSITIVE_INFINITY;
        else
            return Math.pow(2, exp);
    }

    /**
     * Returns the greatest lower bound of {@code this} set interval.
     * @return the greatest lower bound of {@code this} set interval.
     * Returns {@code null} if {@code this} is the empty set interval.
     */
    public @Nullable
    MultiInterval
    getGlb() {
        return glb;
    }

    /**
     * Returns the least upper bound of {@code this} set interval.
     * @return the least upper bound of {@code this} set interval.
     * Returns {@code null} if {@code this} is the empty set interval.
     */
    public @Nullable MultiInterval
    getLub() {
        return lub;
    }

    /**
     * Returns a new set interval which is the universe.
     * @return the implementation universe.
     */
    public static @NotNull SetInterval
    universe() {
        return new SetInterval(INF, SUP);
    }

    /**
     * Computes and returns the intersection of {@code this} with {@code setInterval}
     * @param setInterval the other argument of the set intersection.
     * @return the resulting set, i.e. the intersection.
     */
    public @NotNull SetInterval
    intersect(@NotNull SetInterval setInterval) {
        Objects.requireNonNull(setInterval);

        if (isEmpty() || setInterval.isEmpty())
            return new SetInterval();
        if (this.equals(setInterval))
            return this;
        return new SetInterval(glb.union(setInterval.glb),
                               lub.intersect(setInterval.lub));
    }

    /**
     * Creates and returns a copy of {@code this}.
     * @return a clone of {@code this}.
     */
    @Override
    public @NotNull SetInterval
    clone() {
        return new SetInterval(glb, lub);
    }
    
    /**
     * Checks whether {@code this} and {@code object} are equal.
     * @param object the object to check for equality.
     * @return {@code true} if and only if {@code this} is equal
     * to {@code object}.
     */
    @Override
    public boolean 
    equals(@Nullable Object object) {
        if(object == this)
            return true;
        if (object instanceof SetInterval) {
            SetInterval tmp = (SetInterval) object;
            if (tmp.isEmpty() || isEmpty())
                return tmp.isEmpty() && isEmpty();
            return glb.equals(tmp.glb) && lub.equals(tmp.lub);
        }
        return false;
    }
    
    /**
     * Creates and returns a string representation of {@code this}.
     * The string returned is "{}" if {@code this} is the empty set,
     * {A} if {@code glb = lub = A}({@code this} is a singleton), "[glb..lub]" otherwise.
     * @return a string representation of {@code this}.
     */
    @Override
    public @NotNull String
    toString() {
        if (isEmpty())
            return "{}";
        else if (glb.equals(lub))
            return "{" + glb + "}";
        return "[" + glb + ".." + lub + "]";
    }
}
