package jsetl;

import jsetl.annotation.NotNull;
import jsetl.annotation.Nullable;

import java.util.*;

/**
 * This class represents a set of integers as an interval of the form
 * {@code [glb, lub]}.
 * 
 * @see MultiInterval
 * @author Roberto Amadini.
 */
 
public class Interval implements Iterable<Integer>, 
                                 Cloneable {

    ///////////////////////////////////////////////////////////////
    //////////////// STATIC MEMBERS ///////////////////////////////
    ///////////////////////////////////////////////////////////////

    /**
     * Maximum representable value.
     */
    public static final int SUP = Integer.MAX_VALUE / 2;

    /**
     * Minimum representable value.
     */
    public static final int INF = -SUP;


    ///////////////////////////////////////////////////////////////
    //////////////// STATIC METHODS ///////////////////////////////
    ///////////////////////////////////////////////////////////////

    /**
     * Returns an interval representing the entire universe of representable values.
     * @return the universe of representable values.
     */
    public static @NotNull Interval
    universe() {
        Interval uni = new Interval();
        uni.glb = INF;
        uni.lub = SUP;
        assert uni != null;
        return uni;
    }

    ///////////////////////////////////////////////////////////////
    //////////////// PUBLIC MEMBERS ///////////////////////////////
    ///////////////////////////////////////////////////////////////

    /** Greatest lower bound of the interval */
    private Integer glb;

    /** Least upper bound of the interval */
    private Integer lub;


    ///////////////////////////////////////////////////////////////
    //////////////// CONSTRUCTORS /////////////////////////////////
    ///////////////////////////////////////////////////////////////

    /**
     * Builds an empty interval.
     */
    public 
    Interval() {
        glb = null;
        lub = null;
    }

    /**
     * Constructs a copy of the {@code Interval}.
     * @param interval an interval.
     */
    public Interval(@NotNull Interval interval){
        assert interval != null;

        this.glb = interval.glb;
        this.lub = interval.lub;
    }
    
    /**
     * Builds the interval {@code [x, x]}, i.e. the singleton 
     * {@code {x}}.
     * 
     * @param x the only element of the interval.
     */
    public 
    Interval(@NotNull Integer x) {
        assert x != null;

        if (x < INF || x > SUP) {
            glb = null;
            lub = null;
            return;
        }
        glb = x;
        lub = x;
    }

    /**
     * Builds the interval {@code [lb, ub]}.
     * Note that if {@code lb greater than ub}, it constructs the empty
     * interval.
     * Note that if {@code up} is lower than the minimum representable value, or if
     * {@code lb} is greater than the maximum representable value, then the empty interval is constructed.
     *
     * @param lb the lower bound.
     * @param ub the upper bound.
     */
    public 
    Interval(@NotNull Integer lb, @NotNull Integer ub) {
        assert lb != null;
        assert ub != null;

        if (lb > ub || ub < INF || lb > SUP) {
            glb = null;
            lub = null;
            return;
        }
        glb = Math.max(lb, INF);
        lub = Math.min(ub, SUP);
    }

    /**
     * Construct a new interval which contains every representable value in {@code set}.
     * Such interval is equal to [glb set, lub set] if {@code set} has only representable values,
     * otherwise non representable values are ignored.
     * If there are no representable values in {@code set} the empty interval is constructed.
     *
     * @param set The set whose elements define the interval.
     */
    public
    Interval(@NotNull Set<Integer> set) {
        assert set != null;

        TreeSet<Integer> ts;
        if (set instanceof TreeSet)
            ts = (TreeSet<Integer>) set;
        else
            ts = new TreeSet<>(set);
        Iterator<Integer> it = ts.iterator();
        int lb = INF - 1;
        while (it.hasNext()) {
            lb = it.next();
            if (lb >= INF)
                break;
        }
        if (lb < INF || lb > SUP) {
            glb = null;
            lub = null;
            return;
        }
        glb = lb;
        int ub = SUP + 1;
        it = ts.descendingIterator();
        while (it.hasNext()) {
            ub = it.next();
            if (ub <= SUP)
                break;
        }
        if (ub > SUP)
            lub = glb;
        else
            lub = ub;
    }

    /**
     * Builds the interval {@code [lb, ub]}.
     * Note that if {@code lb greater than ub}, it constructs the empty
     * interval.
     * Note that if {@code up} is lower than the minimum representable value, or if
     * {@code lb} is greater than the maximum representable value, then the empty interval is constructed.
     *
     * @param lb the lower bound.
     * @param ub the upper bound.
     */
    private
    Interval(@NotNull Long lb, @NotNull Long ub) {
        assert lb != null;
        assert ub != null;

        if (lb > ub || ub < INF || lb > SUP) {
            glb = null;
            lub = null;
            return;
        }
        glb = (int) Math.max(lb, Interval.INF);
        lub = (int) Math.min(ub, Interval.SUP);
    }


    ///////////////////////////////////////////////////////////////
    //////////////// PUBLIC METHODS ///////////////////////////////
    ///////////////////////////////////////////////////////////////

    /**
     * Checks if {@code x} is an element of {@code this}.
     * 
     * @param x the element.
     * 
     * @return {@code true} if {@code x} belongs to 
     *         {@code this}, {@code false} otherwise.
     */
    public boolean 
    contains(@NotNull Integer x) {
        assert x != null;

        if (this.isEmpty())
            return false;
        else
            return glb <= x && x <= lub;
    }
    
    /**
     * Checks if {@code interval} contains {@code this}.
     * 
     * @param interval the interval.
     * 
     * @return {@code true} if {@code interval} is a subset of
     *         {@code this}, {@code false} otherwise.
     */
    public boolean 
    subset(@NotNull Interval interval) {
        assert interval != null;

        if (this.isEmpty())
            return interval.isEmpty();
        else
            return interval.glb <= glb && lub <= interval.lub;
    }
    
    
    /**
     * Returns {@code true} if and only if {@code this} is empty.
     * 
     * @return {@code true} if {@code this} is empty, 
     *         {@code false} otherwise.
     */
    public boolean 
    isEmpty() {
        return glb == null;
    }

    /**
     * Returns {@code true} if and only if {@code this} interval contains exactly one value.
     *
     * @return {@code true} if {@code this} interval has size 1,
     *         {@code false} otherwise.
     */
    public boolean
    isSingleton() {
        return size() == 1;
    }

    /**
     * Returns {@code true} if and only if {@code this} interval contains every representable value.
     *
     * @return {@code true} if {@code this} interval contains every representable value,
     *         {@code false} otherwise.
     */
    public boolean
    isUniverse() {
        return glb == INF && lub == SUP;
    }
    
    /**
     * Returns the size of {@code this}, i.e. the cardinality of the
     * set of integers corresponding to {@code this}.
     * 
     * @return the size.
     */
    public int
    size() {
        if (isEmpty())
            return 0;
        else
            return lub - glb + 1;
    }

    /**
     * Sets the greatest lower bound of the interval.
     *
     * @param lb the new greatest lower bound.
     */
    protected void 
    setGlb(@NotNull Integer lb) {
        assert lb != null;

        if (lb < INF)
            glb = INF;
        else
            glb = lb;
    }

    /**
     * Returns the greatest lower bound of {@code this}.
     * 
     * @return the greatest lower bound.
     */
    public @NotNull Integer
    getGlb() {
        assert glb != null;
        return glb;
    }

    /**
     * Sets the least upper bound of the interval.
     * @param ub the new least upper bound.
     */
    protected void
    setLub(@NotNull Integer ub) {
        assert ub != null;

        if (ub > SUP)
            lub = SUP;
        else
            lub = ub;   
    }

    /**
     * Returns the least upper bound of {@code this}.
     * 
     * @return the least upper bound.
     */
    public @NotNull Integer
    getLub() {
        assert lub != null;
        return lub;
    }
    
    /**
     * Returns the interval corresponding to the intersection between 
     * {@code this} and {@code interval}.
     * 
     * @param interval the second operand of the intersection.
     * 
     * @return the interval corresponding to the intersection.
     */
    public @NotNull Interval
    intersect(@NotNull Interval interval) {
        assert interval != null;

        if (isEmpty() || interval.isEmpty())
            return new Interval();
        return new Interval(Math.max(glb, interval.glb), 
                            Math.min(lub, interval.lub));
    }
    
    /**
     * Returns the sum of {@code this} and {@code interval}.
     * <br>For example, if {@code X = [a, b]} and
     * {@code Y = [c, d]} the sum will be 
     * {@code X + Y = [a + c, b + d]} 
     * 
     * @param interval the second operand of the sum.
     * @return the interval corresponding to {@code this + interval}
     */
    public @NotNull Interval
    sum(@NotNull Interval interval) {
        assert interval != null;

        if (isEmpty() || interval.isEmpty())
            return new Interval();
        return new Interval(glb + interval.glb, lub + interval.lub);
    }

    /**
     * Returns the sum of {@code this} and a singleton interval of the form {{@code x}}.
     * <br>For example, if {@code Y = [a, b]} the sum will be
     * {@code {x} + Y = [a + x, b + x]}
     *
     * @param x the second operand of the sum.
     *
     * @return the interval corresponding to {@code this + x}
     */
    protected @NotNull Interval
    sum(@NotNull Integer x) {
        assert x != null;
        return new Interval(glb + x, lub + x);
    }
    
    /**
     * Returns the difference of {@code this} and {@code interval}.
     * <br>For example, if {@code X = [a, b]} and
     * {@code Y = [c, d]} the difference will be 
     * {@code X - Y = [a - d, b - c]} 
     * 
     * @param interval the second operand of the difference.
     * @return the interval corresponding to {@code this - interval}.
     */
    public @NotNull Interval
    sub(@NotNull Interval interval) {
        assert interval != null;

        if (isEmpty() || interval.isEmpty())
            return new Interval();
        return new Interval(glb - interval.lub, lub - interval.glb);
    }

    /**
     * Returns the opposite of this interval. If this interval is [glb, lub] the returned interval will be [-lub, -glb].
     * @return the opposite of this interval
     */
    public @NotNull Interval
    opposite() {
        if (isEmpty())
            return new Interval();
        return new Interval(-lub, -glb);
    }

    /**
     * Returns the multiplication of {@code this} and {@code interval}.
     * @param interval the second operand of the multiplication.
     * @return the interval corresponding to {@code this * interval}.
     */
    protected @NotNull Interval
    mul(@NotNull Interval interval) {
        assert interval != null;

        if (isEmpty() || interval.isEmpty())
            return new Interval();

        long glb0 = this.glb;
        long glb1 = interval.glb;
        long lub0 = this.lub;
        long lub1 = interval.lub;
        if (glb0 >= 0) {
            if (glb1 >= 0)
                return new Interval(glb0 * glb1, lub0 * lub1);
            if (lub1 <= 0)
                return new Interval(glb1 * lub0, glb0 * lub1);
        }
        if (lub0 <= 0) {
            if (lub1 <= 0)
                return new Interval(lub0 * lub1, glb0 * glb1);
            if (glb1 >= 0)
                return new Interval(glb0 * lub1, glb1 * lub0);
        }   
        ArrayList<Long> v = new ArrayList<>(4);
        v.add(glb0 * glb1);
        v.add(glb0 * lub1);
        v.add(lub0 * glb1);
        v.add(lub0 * lub1);
        return new Interval(Collections.min(v), Collections.max(v));
    }

    /**
     * Returns the multiplication of {@code this} and {@code x}.
     * @param x the second operand of the multiplication.
     * @return the interval corresponding to {@code this * x}.
     */
    protected @NotNull Interval
    mul(@NotNull Integer x) {
        assert x != null;

        long lb;
        long ub;
        if (x > 0) {
            lb = x * glb;
            ub = x * lub;   
        }
        else {
            lb = x * lub;
            ub = x * glb;
        }
        return new Interval(lb, ub);
    }

    /**
     * Returns the division of {@code this} and {@code interval}.
     * @param interval the second operand of the division.
     * @return the interval corresponding to {@code this / interval}.
     */
    protected @NotNull Interval
    div(@NotNull Interval interval) {
        assert interval != null;

        if (isEmpty() || interval.isEmpty())
            return new Interval();
        double glb0 = this.glb;
        double glb1 = interval.glb;
        double lub0 = this.lub;
        double lub1 = interval.lub;
        if (glb0 <= 0 && 0 <= lub0 && glb1 <= 0 && 0 <= lub1)
            return universe();
        if (0 < glb0 || 0 > lub0)
            if (glb1 == 0)
                if (lub1 == 0)
                    return new Interval();
                else
                    glb1 = 1; 
            else 
                if (lub1 == 0)
                    lub1 = -1;
                else if (glb1 <= -1 && 1 <= lub1) {
                    int a = Math.max(Math.abs((int) glb0), 
                                     Math.abs((int) lub0));
                    return new Interval(-a, a);
                    }
        ArrayList<Double> v = new ArrayList<>(4);
        v.add(glb0 / glb1);
        v.add(glb0 / lub1);
        v.add(lub0 / glb1);
        v.add(lub0 / lub1);
        return new Interval((int) Math.ceil(Collections.min(v)), 
                            (int) Math.floor(Collections.max(v)));
    }

    /**
     * Returns the division of {@code this} and {@code x}.
     * @param x the second operand of the division.
     * @return the interval corresponding to {@code this / x}.
     */
    protected Interval
    div(Integer x) {
        assert x != null;
        assert x != 0;

        double lb = glb;
        double ub = lub;
        double y = x;
        if (x > 0)
            return new Interval((int) Math.ceil(lb / y),
                                (int) Math.floor(ub / y));
        else
            return new Interval((int) Math.ceil(ub / y),
                                (int) Math.floor(lb / y));
    }

    /**
     * Returns a {@code TreeSet} containing each element in the interval.
     * @return a {@code TreeSet} containing the elements of the interval.
     */
    public @NotNull TreeSet<Integer>
    toSet() {
        TreeSet<Integer> set = new TreeSet<>();
        if (this.isEmpty())
            return set;
        for (Integer current : this)
            set.add(current);
        assert set != null;
        return set;
    }
    
    /**
     * Returns an iterator for the interval.
     * It iterates from the greatest lower bound to the least upper bound of the interval.
     *
     * @return an iterator for this interval
     */
    public @NotNull Iterator<Integer>
    iterator() {
        return new LocalIterator(glb);
    }

    /**
     * Creates and returns a copy of {@code this}.
     * @return a clone of this interval.
     */
    @Override
    public @NotNull Interval
    clone() {
        Interval clone = new Interval(this);
        return clone;
    }
    
    /**
     * Returns {@code true} if and only if {@code this} is equal
     * to {@code obj}.
     *
     * @return {@code true} if the intervals are equal, {@code false} otherwise.
     */
    @Override
    public boolean 
    equals(@Nullable Object obj) {
        if(obj == null)
            return false;

        if(obj == this)
            return true;

        if (obj instanceof Interval) {
            Interval tmp = (Interval) obj;
            if (tmp.isEmpty() || this.isEmpty())
                return tmp.isEmpty() && this.isEmpty();
            return glb.equals(tmp.glb) && lub.equals(tmp.lub);
        }
        if (obj instanceof MultiInterval) {
            MultiInterval tmp = (MultiInterval) obj;
            return tmp.equals(this);
        }
        return false;
    }
    
    /**
     * Returns a string representation of {@code this}.
     * @return a string representation of this interval.
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


    /**
     * This class provides an implementation for iterable interface.
     * It is used to iterate over the elements of the interval.
     */
    private class LocalIterator implements Iterator<Integer> {
        /**
         * The current element to return
         */
        private int current;

        /**
         * Constructs an iterator that iterates the elements of the interval starting from {@code c}
         * @param c starting point of iteration
         */
        private 
        LocalIterator(int c) {
            current = c;
        }

        /**
         * Checks whether there is another element in the interval or not.
         * @return true if there are other elements to iterate through, false otherwise.
         */
        public boolean 
        hasNext() {
            return current <= lub;
        }

        /**
         * Returns the next element of the iteration
         * @return the next element of the iteration
         * @throws NoSuchElementException if there are no more elements in the interval to iterate through
         */
        public @NotNull Integer
        next()
        throws NoSuchElementException {
            if (!hasNext())
                throw new NoSuchElementException();
            return current++;
        }

    }
}
