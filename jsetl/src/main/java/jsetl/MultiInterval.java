package jsetl;

import jsetl.comparator.CmpIntervalsByGlb;
import jsetl.annotation.NotNull;
import jsetl.annotation.Nullable;
import jsetl.annotation.UnsupportedOperation;

import java.lang.reflect.Array;
import java.util.*;

/**
 * This class represents a set of integers as a set of intervals of the form
 * <br>{@code [a_1, b_1] U [a_2, b_2] U ... U [a_n, b_n]} in which for
 * each {@code i = 1, ..., n} we have that {@code a_i <= b_i} and
 * for each {@code j > i} we have that {@code b_i < a_j - 1}. 
 * 
 * @see Interval
 * @author Roberto Amadini.
 */
public class MultiInterval implements Set<Integer>, 
                                      Cloneable {

    ///////////////////////////////////////////////////////////////
    //////////////// STATIC MEMBERS ///////////////////////////////
    ///////////////////////////////////////////////////////////////

    /**
     * Infimum, Lowest representable value
     */
    public static int INF = Interval.INF;

    /**
     * Supremum, greatest representable value.
     */
    public static int SUP = Interval.SUP;


    ///////////////////////////////////////////////////////////////
    //////////////// STATIC METHODS ///////////////////////////////
    ///////////////////////////////////////////////////////////////

    /**
     * Returns the multi-interval corresponding to the 'universe', i.e. the
     * maximum representable multi-interval.
     *
     * @return the multi-interval corresponding to the universe.
     */
    public static @NotNull MultiInterval
    universe() {
        return new MultiInterval(MultiInterval.INF, MultiInterval.SUP);
    }


    ///////////////////////////////////////////////////////////////
    //////////////// DATA MEMBERS /////////////////////////////////
    ///////////////////////////////////////////////////////////////

    /**
     * List of intervals that comprise the multi-interval.
     */
    private LinkedList<Interval> intervals;

    /**
     * Size of the multi-interval, i.e. number of elements contained.
     */
    private int size;


    ///////////////////////////////////////////////////////////////
    //////////////// CONSTRUCTORS /////////////////////////////////
    ///////////////////////////////////////////////////////////////
    
    /**
     * Builds an empty multi-interval.
     */
    public 
    MultiInterval() {
        intervals = new LinkedList<>();
        size = 0;
    }

    /**
     * Constructs a copy of the {@code MultiInterval}.
     * @param multiInterval the multi interval to copy.
     */
    public MultiInterval(@NotNull MultiInterval multiInterval){
        Objects.requireNonNull(multiInterval);
        this.size = multiInterval.size;
        intervals = new LinkedList<>();
        for(Interval interval : multiInterval.intervals)
            intervals.add(new Interval(interval));
    }

    /**
     * Builds a multi-interval of the form {@code [a, a] = {a}}.
     * 
     * @param a the only element of the set.
     * 
     */
    public 
    MultiInterval(@NotNull Integer a) {
        this(new Interval(a));
        Objects.requireNonNull(a);
    }

    /**
     * Builds a multi-interval of the form {@code [glb, lub]}.
     * 
     * @param glb the lower bound of the interval.
     * @param lub the upper bound of the interval.
     * 
     */
    public 
    MultiInterval(@NotNull Integer glb, @NotNull Integer lub) {
        this(new Interval(glb, lub));
        Objects.requireNonNull(glb);
        Objects.requireNonNull(lub);
    }
    
    /**
     * Builds the multi-interval corresponding to the interval
     * {@code interval}.
     * 
     * @param interval the only interval of the set.
     */
    public 
    MultiInterval(@NotNull Interval interval) {
        Objects.requireNonNull(interval);

        intervals = new LinkedList<>();
        if (interval.isEmpty())
            size = 0;
        else {
            intervals.add(interval);
            size = interval.size();
        }
    }
    
    /**
     * Builds the multi-interval from a set of integers.
     * 
     * @param set the set of integers. It must not contain {@code null} values.
     * @throws NullPointerException if {@code set} contains {@code null} values.
     */
    public
    MultiInterval(@NotNull Set<Integer> set) {
        Objects.requireNonNull(set);
        if(set.stream().anyMatch(Objects::isNull))
            throw new NullPointerException("NULL VALUES NOT ALLOWED");

        intervals = new LinkedList<>();
        size = 0;
        if (set.isEmpty())
            return;
        LinkedList<Integer> list = new LinkedList<>(set);
        Collections.sort(list);
        ListIterator<Integer> listIterator = list.listIterator();
        int current = listIterator.next();
        if (current > MultiInterval.SUP)
            return;
        int next = current;
        if (current < MultiInterval.INF) {
            while (listIterator.hasNext()) {
                next = listIterator.next();
                if (next < MultiInterval.INF)
                    continue;
                current = next;
                break;
            }
            if (next < MultiInterval.INF)
                return;
        }
        Interval interval = new Interval(current);
        int currentSize = 0;
        while (listIterator.hasNext()) {
            next = listIterator.next();
            if (next > MultiInterval.SUP)
                break;
            ++currentSize;
            if (next == current + 1)
                interval.setLub(next);
            else {
                intervals.add(interval);
                interval = new Interval(next);
            }
            current = next;
        }
        intervals.add(interval);
        size = currentSize + 1;
    }
    
    /**
     * Builds the multi-interval from a collection of intervals.
     * 
     * @param col the collection of intervals. It must not contain {@code null} values.
     * @throws NullPointerException if {@code col} contains {@code null} values.
     */
    public
    MultiInterval(@NotNull Collection<Interval> col) {
        Objects.requireNonNull(col);
        if(col.stream().anyMatch(Objects::isNull))
            throw new NullPointerException("NULL VALUES NOT ALLOWED");

        intervals = new LinkedList<>();
        size = 0;
        if (col.isEmpty())
            return;
        // Sort and remove duplicates from list.
        CmpIntervalsByGlb comp = new CmpIntervalsByGlb();
        TreeSet<Interval> tmp = new TreeSet<>(comp);
        tmp.addAll(col);
        if (tmp.first().isEmpty())
            tmp.pollFirst();
        if (tmp.isEmpty())
            return;
        Iterator<Interval> it = tmp.iterator();
        Interval current = it.next();
        int currentSize = 0;
        while (it.hasNext()) {
            Interval next = it.next();
            int nextGlb = next.getGlb();
            int currentLub = current.getLub();
            if (currentLub < nextGlb - 1) {
                intervals.add(current);
                currentSize += current.size();
                current = next;
            }
            else {
                int nextLub = next.getLub();
                if (nextLub > currentLub)
                    // Interval merge.
                    current = new Interval(current.getGlb(), nextLub);
            }
        }
        if (!current.isEmpty())
            intervals.add(current);
        size = currentSize + current.size();
    }

    /**
     * Checks if {@code value} belongs to one of the intervals of 
     * {@code this}.
     * 
     * @param value the value.
     * @return {@code true} if {@code value} belongs to one of
     *         the intervals of {@code this}, {@code false} 
     *         otherwise.
     */
    public boolean
    contains(@NotNull Integer value) {
        Objects.requireNonNull(value);
        if (this.isEmpty() || value < this.getGlb() 
                           || value > this.getLub())
            return false;
        for (Interval current : intervals)
            if (current.contains(value))
                return true;
        return false;
    }
    
    /**
     * Checks if {@code interval} is contained into one of the intervals of
     * {@code this}.
     * 
     * @param interval the interval.
     * @return {@code true} if {@code interval} is contained into one
     *         of the intervals of {@code this}, {@code false} 
     *         otherwise.
     */
    protected boolean
    contains(@NotNull Interval interval) {
        Objects.requireNonNull(interval);

        if (this.isEmpty())
            return interval.isEmpty();
        if (interval.getGlb() < this.getGlb() || interval.getLub() > this.getLub() ||
            this.size < interval.size())
            return false;
        for (Interval current : intervals)
            if (interval.subset(current))
                return true;
        return false;
    }
    
    /**
     * Returns the greatest lower bound of {@code this}.
     * 
     * @return the greatest lower bound. Returns {@code null} if this multi-interval is empty.
     */
    public @Nullable Integer
    getGlb() {
        if (this.isEmpty())
            return null;
        return intervals.getFirst().getGlb();
    }
    
    /**
     * Returns the least upper bound of {@code this}.
     * 
     * @return the least upper bound.{@code null} if this multi-interval is empty.
     */
    public @Nullable Integer
    getLub() {
        if (this.isEmpty())
            return null;
        return intervals.getLast().getLub();
    }
    
    /**
     * Returns the order of {@code this}, i.e.,
     * the number of intervals that form this multi-interval.
     * 
     * @return the order of this multi-interval.
     */
    public int
    getOrder() {
        return intervals.size();
    }
    
    /**
     * Checks if {@code this} is the 'universe', i.e. the maximum 
     * representable multi-interval.
     * 
     * @return {@code true} if {@code this} the universe, 
     *         {@code false} otherwise.
     */
    public boolean
    isUniverse() {
        return this.equals(universe());
    }

    /**
     * Checks whether this multi-interval is a singleton or not.
     * A singleton is a (completely specified) set with only one element.
     * @return {@code true} if {@code this} is a singleton, {@code false} otherwise
     */
    public boolean
    isSingleton() {
        return size == 1;
    }
    
    /**
     * Returns the convex closure of {@code this}, i.e. the minimum
     * convex set containing {@code this}.
     * 
     * @return the {@code Interval} corresponding to the convex closure.
     */
    public @NotNull Interval
    convexClosure() {
        if (isEmpty())
            return new Interval();
        return new Interval(getGlb(), getLub());
    }
    
    /**
     * Checks if {@code this} is a subset of {@code multiInterval}.
     *
     * @param multiInterval the second operand of the inclusion.
     * 
     * @return {@code true} if {@code this} is a subset of
     *         {@code multiInterval}, {@code false} otherwise.
     */
    public boolean
    subset(@NotNull MultiInterval multiInterval) {
        Objects.requireNonNull(multiInterval);

        if (this.isEmpty())
            return true;
        if (multiInterval.isEmpty())
            return false;
        if (multiInterval.isUniverse())
            return true;
        if (this.isUniverse())
            return multiInterval.isUniverse();
        if (this.size > multiInterval.size)
            return false;
        int lub0 = this.getLub();
        int glb0 = this.getGlb();
        int lub1 = multiInterval.getLub();
        int glb1 = multiInterval.getGlb();
        if (glb0 < glb1 || lub0 > lub1)
            return false;
        int h = 0;
        for (int i = 0, ni = intervals.size(); i < ni; ++i) {
            int j = h;
            int nj = multiInterval.intervals.size();
            for (; j < nj; ++j)
                if (intervals.get(i).subset(multiInterval.intervals.get(j))) {
                    h = j;
                    break;
                }
            if (j == nj)
                return false;
        }
        return true;
    }
    
    /**
     * Returns the multi-interval corresponding to the complement of
     * {@code this} with respect to the universe multi-interval.
     * 
     * @return the complement of this multi-interval.
     */
    public @NotNull MultiInterval
    complement() {
        if (isEmpty())
            return universe();
        if (isUniverse())
            return new MultiInterval();

        MultiInterval result = new MultiInterval();
        Iterator<Interval> it = intervals.iterator();
        Interval current = it.next();
        Interval tmp = new Interval(Interval.INF, current.getGlb() - 1);
        if (!tmp.isEmpty()) {
            result.intervals.add(tmp);
            result.size += tmp.size();
        }
        int currentGlb = current.getLub() + 1;
        while (it.hasNext()) {
            current = it.next();
            tmp = new Interval(currentGlb, current.getGlb() - 1);
            result.intervals.add(tmp);
            result.size += tmp.size();
            currentGlb = current.getLub() + 1;
        }
        current = new Interval(currentGlb, Interval.SUP);
        if (!current.isEmpty()) {
            result.intervals.add(current);
            result.size += current.size();
        }
        assert result != null;
        return result;
    }

    /**
     * Returns the result of the complement operation using the given multi-interval as universe.
     * @param universe universe to use for the complement.
     * @return the complemented multi-interval.
     */
    public @NotNull MultiInterval
    complement(@NotNull MultiInterval universe) {
        Objects.requireNonNull(universe);
        MultiInterval result = universe.intersect(this.complement());
        assert result != null;
        return result;
    }
    
    /**
     * Returns the multi-interval corresponding to the union between
     * {@code this} and {@code multiInterval}.
     * 
     * @param multiInterval the second operand of the union.
     * 
     * @return the multi-interval corresponding to the union.
     */
    public @NotNull MultiInterval
    union(@NotNull MultiInterval multiInterval) {
        Objects.requireNonNull(multiInterval);
        if (isEmpty())
            return multiInterval;
        if (multiInterval.isEmpty())
            return this;
        if (isUniverse() || multiInterval.isUniverse())
            return universe();
        if (this.subset(multiInterval))
            return multiInterval;
        if (multiInterval.subset(this))
            return this;
        LinkedList<Interval> list = new LinkedList<Interval>();
        if (this.getGlb() > multiInterval.getLub()) {
            int n = multiInterval.intervals.size();
            for (int i = 0; i < n - 1; ++i)
                list.add(multiInterval.intervals.get(i));
            list.add(multiInterval.intervals.get(n - 1).clone());
            if (this.getGlb() == multiInterval.getLub() + 1) {
                list.getLast().setLub(this.intervals.getFirst().getLub());
                for (int i = 1; i < intervals.size(); ++i)
                    list.add(intervals.get(i));
            }
            else 
                list.addAll(this.intervals);
            MultiInterval result = new MultiInterval();
            result.intervals = list;
            result.size = this.size + multiInterval.size;
            assert result != null;
            return result;
        }
        if (this.getLub() < multiInterval.getGlb()) {
            int n = intervals.size();
            for (int i = 0; i < n - 1; ++i)
                list.add(intervals.get(i));
            list.add(intervals.get(n - 1).clone());
            if (this.getLub() == multiInterval.getGlb()- 1) {
                list.getLast().setLub(multiInterval.intervals.getFirst().getLub());
                for (int i = 1; i < multiInterval.intervals.size(); ++i)
                    list.add(multiInterval.intervals.get(i));
            }
            else
                list.addAll(multiInterval.intervals);
            MultiInterval result = new MultiInterval();
            result.intervals = list;
            result.size = this.size + multiInterval.size;
            assert result != null;
            return result;
        }
        list.addAll(intervals);
        list.addAll(multiInterval.intervals);
        return new MultiInterval(list);
    }
    
    /**
     * Returns the multi-interval corresponding to the intersection between
     * {@code this} and {@code multiInterval}.
     * 
     * @param multiInterval the second operand of the intersection.
     * 
     * @return the multi-interval corresponding to the intersection.
     */
    public @NotNull MultiInterval
    intersect(@NotNull MultiInterval multiInterval) {
        Objects.requireNonNull(multiInterval);

        if (isEmpty() || multiInterval.isEmpty()) {
            return new MultiInterval();
        }
        if (isUniverse())
            return multiInterval;
        if (multiInterval.isUniverse())
            return this;
        int lub0 = getLub();
        int glb0 = getGlb();
        int lub1 = multiInterval.getLub();
        int glb1 = multiInterval.getGlb();
        if (lub0 < glb1 || glb0 > lub1)
            return new MultiInterval();
        if (lub0 == glb1)
            return new MultiInterval(lub0);
        if (lub1 == glb0)
            return new MultiInterval(lub1);
        if (this.subset(multiInterval))
            return this;
        if (multiInterval.subset(this))
            return multiInterval;
        int h = 0;
        MultiInterval result = new MultiInterval();
        for (int i = 0, ni = intervals.size(); i < ni; ++i) {
            boolean exit = false;
            for (int j = h, nj = multiInterval.intervals.size(); j < nj; ++j) {
                Interval current = intervals.get(i)
                     .intersect(multiInterval.intervals.get(j));
                if (current.isEmpty())
                    if (exit) {
                        h = j - 1;
                        break;
                    }
                    else
                        continue;
                result.intervals.add(current);
                result.size += current.size();
                exit = true;
            }
        }
        assert result != null;
        return result;
    }
    
    /**
     * Returns the multi-interval corresponding to the set difference between
     * {@code this} and {@code multiInterval}.
     * 
     * @param multiInterval the second operand of the difference.
     * 
     * @return the multi-interval corresponding to the difference.
     */
    public @NotNull MultiInterval
    diff(@NotNull MultiInterval multiInterval) {
        Objects.requireNonNull(multiInterval);

        if (this.isEmpty() || multiInterval.isUniverse())
            return new MultiInterval();
        if (multiInterval.isEmpty() || this.isUniverse())
            return this;
        if (this.subset(multiInterval))
            return new MultiInterval();

        MultiInterval result = this.intersect(multiInterval.complement());
        assert result != null;
        return result;
    }
    
    /**
     * Returns the sum of {@code this} and {@code multiInterval}.
     * 
     * @param multiInterval the second operand of the sum.
     * @return the MultiInterval corresponding to {@code this + multiInterval}
     */
    public @NotNull MultiInterval
    sum(@NotNull MultiInterval multiInterval) {
        Objects.requireNonNull(multiInterval);

        if (this.isEmpty() || multiInterval.isEmpty())
            return new MultiInterval();
        if (multiInterval.size == 1)
            return sum(multiInterval.getGlb());
        if (size == 1)
            return multiInterval.sum(this.getGlb());
        LinkedList<Interval> list = new LinkedList<Interval>();
        for (int i = 0, n = intervals.size(); i < n; ++i)
            for (int j = 0, m = multiInterval.intervals.size(); j < m; ++j)
                list.add(intervals.get(i).sum(multiInterval.intervals.get(j)));
        return new MultiInterval(list);
    }
    
    /**
     * Returns the difference of {@code this} and {@code multiInterval}.
     * 
     * @param multiInterval the second operand of the difference.
     * @return the MultiInterval corresponding to {@code this - multiInterval}
     */
    public @NotNull MultiInterval
    sub(@NotNull MultiInterval multiInterval) {
        Objects.requireNonNull(multiInterval);

        MultiInterval result = new MultiInterval();
        if (isEmpty() || multiInterval.isEmpty())
            return result;
        if (multiInterval.size == 1)
            return sum(-multiInterval.getGlb());
        if (this.size == 1) {
            int x = this.getGlb();
            if (x == 0)
                return multiInterval.opposite();
            for (int i = multiInterval.intervals.size() - 1; i >= 0; --i) {
                Interval tmp = multiInterval.intervals.get(i).opposite().sum(x);
                if (!tmp.isEmpty()) {
                    result.intervals.add(tmp);
                    result.size += tmp.size();
                }
                else 
                    if (x > 0)
                        return result;
            }
            return result;
        }
        LinkedList<Interval> list = new LinkedList<Interval>();
        for (int i = 0, n = intervals.size(); i < n; ++i)
            for (int j = 0, m = multiInterval.intervals.size(); j < m; ++j)
                list.add(intervals.get(i).sub(multiInterval.intervals.get(j)));
        return new MultiInterval(list);
    }

    /**
     * Returns a new multi-interval in wich each interval is the opposite of the original
     * @return the new multi-interval
     */
    public @NotNull MultiInterval
    opposite() {
        MultiInterval result = new MultiInterval();
        if (isEmpty())
            return result;
        for (int i = intervals.size() - 1; i >= 0; --i)
            result.intervals.add(intervals.get(i).opposite());
        result.size = size;
        assert result != null;
        return result;
    }
    
    
    /**
     * Returns a {@code Set} representation of {@code this}.
     * @return a set representation of {@code this}.
     */
    public @NotNull Set<Integer>
    toSet() {
        return new HashSet<>(this);
    }
    
    /**
     * Creates and returns a copy of {@code this}.
     * @return a clone of {@code this}.
     */
    @Override
    public @NotNull MultiInterval
    clone() {
        MultiInterval clone = new MultiInterval();
        for (Interval current : intervals)
            clone.intervals.add(current.clone());
        clone.size = size;
        return clone;
    }
    
    /**
     * Returns a string representation of {@code this}.
     * @return a string representation of {@code this}.
     */
    @Override
    public @NotNull String
    toString() {
        if (size == 0)
            return "{}";
        if (this.getOrder() == 1)
          return intervals.get(0).toString();
        StringBuilder result = new StringBuilder();
        result.append('{');
        Interval tmp;
        for (int i = 0, n = intervals.size() - 1; i < n; ++i ) {
            tmp = intervals.get(i);
            result.append(tmp.getGlb());
            if (tmp.size() > 1) {
                result.append("..");
                result.append(tmp.getLub());
            }
            result.append(", ");
        }
        tmp = intervals.get(intervals.size() - 1);
        result.append(tmp.getGlb());
        if (tmp.size() > 1) {
            result.append("..");
            result.append(tmp.getLub());
        }
        result.append('}');
        return result.toString();
    }

    /**
     * Adds an integer value to this multi-interval.
     * @param value the value to add.
     * @return {@code true} if the value was not present and was added, {@code false} otherwise.
     */
    public boolean 
    add(@NotNull Integer value) {
        Objects.requireNonNull(value);

        if (value < INF || value > SUP)
            return false;
        ListIterator<Interval> listIterator = intervals.listIterator();
        while (listIterator.hasNext()) {
            Interval interval = listIterator.next();            
            // The current interval already contains value. 
            if (interval.contains(value))
                return false;
            int lub = interval.getLub();
            // The current interval terminates in value - 1.
            if (value == lub + 1) {
                interval.setLub(value);
                // See if merge with next interval is needed.
                if (listIterator.hasNext()) {
                    Interval next = listIterator.next();
                    if (next.getGlb() == value + 1) {
                        // Merge.
                        interval.setLub(next.getLub());
                        listIterator.remove();
                    }
                }
                ++size;
                return true;
            } 
            int glb = interval.getGlb();
            // The current interval initiates in value + 1.
            if (value == glb - 1) {
                interval.setGlb(value);
                // No need to merge here (merging is captured before).
                ++size;
                return true;
            } 
            // Add the new singleton interval before the current interval.
            if (value < glb) {
                Interval fresh = new Interval(value);
                listIterator.previous();
                listIterator.add(fresh);
                ++size;
                return true;
            }
        }
        // Add the new singleton interval at the end of the list.
        Interval fresh = new Interval(value);
        listIterator.add(fresh);
        ++size;
        return true;
    }

    /**
     * Adds all the integer of {@code collection} to the multi-interval.
     * @param collection collection of integers to add to {@code this}.
     *                   It must not contain {@code null} values.
     * @return {@code true} if at least one (new) element was added, {@code false} otherwise.
     * @throws NullPointerException if {@code collection} contains {@code null} values.
     */
    public boolean 
    addAll(@NotNull Collection<? extends Integer> collection) {
        Objects.requireNonNull(collection);
        if(collection.stream().anyMatch(Objects::isNull))
            throw new NullPointerException("NULL VALUES NOT ALLOWED");

        boolean result = false;
        for (Integer integer : collection) {
            result = add(integer) || result;
        }
        return result;
    }

    /**
     * Empties this multi-interval.
     * After the execution of this method {@code this} becomes the empty set.
     */
    public void 
    clear() {
        intervals.clear();
        size = 0;
    }

    /**
     * Checks whether this multi-interval contains {@code object} or not.
     * @param object object to test.
     * @return {@code true} if {@code object} is an element (or a subset) of {@code this}, {@code false} otherwise.
     */
    public boolean 
    contains(@NotNull Object object) {
        Objects.requireNonNull(object);

        if (object instanceof Integer)
            return contains((Integer) object);
        else if (object instanceof Interval)
            return contains((Interval) object);
        else if (object instanceof MultiInterval)
            return ((MultiInterval) object).subset(this);
        else
            return false;
    }

    /**
     * Checks whether this multi-interval contains all the elements of {@code collection}.
     * @param collection collection of objects to test.
     * @return {@code true} if each element of {@code collection} is an element (or subset) of {@code this},
     *          {@code false} otherwise.
     */
    public boolean 
    containsAll(@NotNull Collection<?> collection) {
        Objects.requireNonNull(collection);
        return collection.stream().allMatch(this::contains);
    }

    /**
     * Checks whether {@code this} is equal to {@code object}.
     * @param object object to test.
     * @return {@code true} if {@code this} is equal to {@code object}, {@code false} otherwise.
     */
    @Override
    public boolean
    equals(@Nullable Object object) {
        if(object == null)
            return false;
        if(object == this)
            return true;

        if (object instanceof MultiInterval) {
            MultiInterval tmp = (MultiInterval) object;
            return size == tmp.size && intervals.equals(tmp.intervals);
        }
        else if (object instanceof Interval) {
            MultiInterval temp = new MultiInterval((Interval) object);
            return this.equals(temp);
        }
        else 
            return false;
    }
    
    @Override
    public int
    hashCode() {
        return (this.getGlb() != null? this.getGlb() : 0)
                + (this.getLub() != null? this.getLub() : 0);
    }

    /**
     * Checks whether this multi-interval represents the empty set or not.
     * @return {@code true} if this multi-interval represents the empty set, {@code false} otherwise.
     */
    public boolean 
    isEmpty() {
        return size == 0;
    }

    /**
     * Returns an iterator over the elements of this multi-interval.
     * @return an iterator over the elements of this multi-interval.
     */
    public @NotNull Iterator<Integer>
    iterator() {
        return new LocalIterator();
    }

    /**
     * Removes an integer from this multi-interval.
     * @param object element to remove.
     * @return {@code true} if the element was present and was removed, {@code false} otherwise.
     * @throws ClassCastException if {@code object} is not an {@code Integer}.
     */
    public boolean 
    remove(@NotNull Object object) {
        Objects.requireNonNull(object);

        if (size == 0)
            return false;
        if (!(object instanceof Integer))
            throw new ClassCastException(object.getClass().getName());
        Integer value = (Integer) object;
        if (value < getGlb() || value > getLub())
            return false;
        ListIterator<Interval> listIterator = intervals.listIterator();
        while (listIterator.hasNext()) {
            Interval interval = listIterator.next();
            if (interval.contains(value)) {
                int glb = interval.getGlb();
                int lub = interval.getLub();
                // The current interval is a singleton and should be removed.
                if (lub == glb && glb == value) {
                    listIterator.remove();
                    --size;
                    return true;
                }
                // Adjust GLB if needed.
                if (glb == value) {
                    interval.setGlb(glb + 1);
                    --size; 
                    return true;
                }
                // Adjust LUB if needed.
                if (lub == value) {
                    interval.setLub(lub - 1);
                    --size;
                    return true;
                }
                interval.setLub(value - 1);
                Interval fresh = new Interval(value + 1, lub);
                listIterator.add(fresh);
                --size;
                return true;
            }
        }
        return false;
    }

    /**
     * Removes all elements (which must be integers) of {@code collection} from {@code this}.
     * @param collection collection of elements to remove.
     * @return {@code true} if the at least one element was present and was removed, {@code false} otherwise.
     */
    public boolean 
    removeAll(@NotNull Collection<?> collection) {
        Objects.requireNonNull(collection);

        boolean result = false;
        for (Object object : collection)
            result = remove(object) || result;
        return result;
    }

    /**
     * Removes each integer that is not an element of  {@code collection} from this multi-interval.
     * @param collection collection of objects to retain.
     * @return {@code true} if at least one element was retained, {@code false} otherwise.
     */
    public boolean 
    retainAll(@NotNull Collection<?> collection) {
        Objects.requireNonNull(collection);

        MultiInterval tmp = new MultiInterval(); 
        boolean result = false;
        for (Object object : collection)
            if (contains(object))
                result = tmp.add((Integer)object) || result;
        intervals = tmp.intervals;
        size = tmp.size;
        return result;
    }

    /**
     * Returns the size of the multi-interval, i.e. the number of its elements.
     * @return the number of elements of the multi-interval.
     */
    public int
    size() {
        return size;
    }

    /**
     * Returns an array containing all integers that are elements of {@code this}.
     * @return an array representation of {@code this}.
     */
    public Object[] 
    toArray() {
        Object[] result = new Object[size]; 
        int index = 0;
        for (Interval interval : intervals)
            for (Integer integer : interval)
                result[index++] = integer;
        return result;
    }

    /**
     * Puts all integers in {@code this} into {@code array}, if it can contain them, otherwise
     * creates a new array and puts all the integers into it.
     * @param array array to (possibly) use to store the integers.
     * @param <T> type of the elements.
     * @return the array that was used to store the integers (either {@code array} or a new array).
     */
    @SuppressWarnings("unchecked")
    @NotNull
    public <T>  T[]
    toArray(@NotNull T[] array) {
        Objects.requireNonNull(array);

        T[] result;
        if(size > array.length) 
            result = (T[]) Array.newInstance(
                           array.getClass().getComponentType(), size);
        else
            result = array;
        int index = 0;
        for (Interval interval : intervals)
            for (Integer integer : interval)
                if (index < size)
                    result[index++] = (T)integer;
                else
                    result[index++] = null;
        return result;
    }


    ///////////////////////////////////////////////////////////////
    //////////////// PROTECTED METHODS ////////////////////////////
    ///////////////////////////////////////////////////////////////

    /**
     * Returns (a reference to) the linked list of intervals contained in this multi-interval.
     * @return the list of intervals.
     */
    protected @NotNull LinkedList<Interval>
    getIntervals() {
        assert intervals != null;
        return intervals;
    }

    /**
     * Returns a multi-interval which is the result of the addition of {@code x} to every element of {@code this}.
     * @param x element to add.
     * @return the resulting multi-interval.
     */
    protected @NotNull MultiInterval
    sum(@NotNull Integer x) {
        assert x != null;

        MultiInterval result = new MultiInterval();
        if (isEmpty())
            return result;
        if (x == 0)
            return this;
        for (int i = 0, n = intervals.size(); i < n; ++i) {
            Interval tmp = intervals.get(i).sum(x);;
            if (!tmp.isEmpty()) {
                result.intervals.add(tmp);
                result.size += tmp.size();
            }
            else
            if (x > 0)
                return result;
        }
        assert result != null;
        return result;
    }

    /**
     * Returns a multi-interval which is the result of the subtraction of {@code integer} from every element of {@code this}.
     * @param integer element to subtract.
     * @return the resulting multi-interval.
     */
    protected @NotNull MultiInterval
    sub(@NotNull Integer integer) {
        return sum(-integer);
    }

    /**
     * Returns a new multi-interval which is the result of the multiplication
     * of each of the elements of {@code this} by {@code x}.
     * @param x number to multiply by.
     * @return the constructed multi-interval.
     */
    protected @NotNull MultiInterval
    mul(@NotNull Integer x) {
        assert x != null;
        MultiInterval result = new MultiInterval();
        if (isEmpty())
            return result;
        if (x == 0)
            return new MultiInterval(0);
        if (x == 1)
            return this;
        if (x == -1)
            return opposite();
        if (x < -1) {
            for (int i = intervals.size() - 1; i >= 0; --i) {
                Interval tmp = intervals.get(i).mul(x);
                if (!tmp.isEmpty()) {
                    result.intervals.add(tmp);
                    result.size += tmp.size();
                }
                else
                    return result;
            }
            assert result != null;
            return result;
        }
        else {
            for (int i = 0, n = intervals.size(); i < n; ++i) {
                Interval tmp = intervals.get(i).mul(x);
                if (!tmp.isEmpty()) {
                    result.intervals.add(tmp);
                    result.size += tmp.size();
                }
                else
                    return result;
            }
            assert result != null;
            return result;
        }
    }

    /**
     * Returns a new multi-interval which is the result of the division
     * of each of the elements of {@code this} by {@code x}.
     * @param x number to divide by.
     * @return the constructed multi-interval.
     */
    protected @NotNull MultiInterval
    div(@NotNull Integer x) {
        assert x != null;

        MultiInterval result = new MultiInterval();
        if (isEmpty() || x == 0)
            return result;
        if (x == 1)
            return this;
        if (x == -1)
            return opposite();
        if (x < -1) {
            for (int i = intervals.size() - 1; i >= 0; --i) {
                Interval tmp = intervals.get(i).div(x);
                if (!tmp.isEmpty()) {
                    result.intervals.add(tmp);
                    result.size += tmp.size();
                }
                else
                    return result;
            }
            assert result != null;
            return result;
        }
        else {
            for (int i = 0, n = intervals.size(); i < n; ++i) {
                Interval tmp = intervals.get(i).div(x);
                if (!tmp.isEmpty()) {
                    result.intervals.add(tmp);
                    result.size += tmp.size();
                }
                else
                    return result;
            }
            assert result != null;
            return result;
        }
    }

    /**
     * Returns the midmost element of this multi-interval.
     * Returns {@code null} if the multi-interval is empty.
     * This method is used for labeling.
     * @return the midmost element.
     */
    protected @Nullable Integer
    getMidMostElement() {
        if (size == 0)
            return null;
        if (size == 1)
            return getGlb();
        int index = (intervals.size() - 1) / 2;
        Interval interval = intervals.get(index);
        return (interval.getLub() + interval.getGlb()) / 2;
    }

    /**
     * Returns the mid element of one of the intervals (chosen randomly) of this multi-interval.
     * Returns {@code null} if the multi-interval is empty.
     * This method is used for labeling.
     * @return the mid element of the randomly chosen interval.
     */
    protected @Nullable Integer
    getMidRandomElement() {
        if (size == 0)
            return null;
        if (size == 1)
            return getGlb();
        Random rnd = new Random();
        int index = rnd.nextInt(intervals.size());
        Interval interval = intervals.get(index);
        return (interval.getLub() + interval.getGlb()) / 2;
    }

    /**
     * Returns a random element from a random interval of this multi-interval.
     * Returns {@code null} if the multi-interval is empty.
     * This method is used for labeling.
     * @return the chosen element.
     */
    protected @Nullable Integer
    getRangeRandomElement() {
        if (size == 0)
            return null;
        if (size == 1)
            return getGlb();
        Random rnd = new Random();
        int index = rnd.nextInt(intervals.size());
        Interval interval = intervals.get(index);
        return rnd.nextInt(interval.size()) + interval.getGlb();
    }

    /**
     * Returns a random element of this multi-interval (chosen using a flat distribution).
     * Returns {@code null} if the multi-interval is empty.
     * This method is used for labeling
     * @return the chosen element
     */
    protected @Nullable Integer
    getEquiRandomElement() {
        if (size == 0)
            return null;
        if (size == 1)
            return getGlb();
        Random rnd = new Random();
        int num = rnd.nextInt(size);
        int currentSize = 0;
        for (Interval interval : intervals) {
            int preSize = currentSize;
            currentSize += interval.size();
            if (num < currentSize)
                return interval.getGlb() + num - preSize;
        }
        // Unreachable point.
        throw new IllegalStateException("EXECUTION SHOULD NEVER REACH THIS POINT");
    }

    /**
     * Returns the median element of this multi-interval.
     * Returns {@code null} if the multi-interval is empty.
     * This method is used for labeling.
     * @return the median element.
     */
    protected @Nullable Integer
    getMedianElement() {
        if (size == 0)
            return null;
        if (size == 1)
            return getGlb();
        int num = (size - 1) / 2;
        int currentSize = 0;
        for (Interval interval : intervals) {
            int preSize = currentSize;
            currentSize += interval.size();
            if (num < currentSize)
                return interval.getGlb() + num - preSize;
        }
        // Unreachable point.
        throw new IllegalStateException("EXECUTION SHOULD NEVER REACH THIS POINT");
    }


    ///////////////////////////////////////////////////////////////
    //////////////// INNER CLASSES ////////////////////////////////
    ///////////////////////////////////////////////////////////////

    /**
     * Class whose elements are iterators over the integers in the multi-interval.
     */
    private class LocalIterator implements Iterator<Integer> {

        ///////////////////////////////////////////////////////////////
        //////////////// DATA MEMBERS /////////////////////////////////
        ///////////////////////////////////////////////////////////////

        /**
         * Iterator over the intervals of the multi-interval.
         */
        private ListIterator<Interval> intervalIterator;

        /**
         * Iterator over the elements of the current interval.
         */
        private Iterator<Integer> integerIterator;

        /**
         * Constructs a new iterator.
         */
        private 
        LocalIterator() {
            intervalIterator = intervals.listIterator();
            integerIterator = null;
        }

        /**
         * Checks whether there are more elements to return or not.
         * @return {@code true} if there are more elements to iterate through, {@code false} otherwise.
         */
        public boolean 
        hasNext() {
            if (integerIterator != null && integerIterator.hasNext())
                return true;
            if (!intervalIterator.hasNext())
                return false;
            Interval interval = intervalIterator.next();
            integerIterator = interval.iterator();
            return integerIterator.hasNext();
        }

        /**
         * Returns the next integer in the iteration.
         * @return the next integer in the iteration.
         * @throws NoSuchElementException if there is not a next element in the iteration.
         */
        public @NotNull Integer
        next() {
            if (!hasNext())
                throw new NoSuchElementException();
            Integer result = integerIterator.next();
            if (!integerIterator.hasNext())
                integerIterator = null;
            assert result != null;
            return result;
        }

        /**
         * @throws UnsupportedOperationException always.
         */
        @UnsupportedOperation
        @Override
        public void 
        remove() {
            throw new UnsupportedOperationException();
        }
    }
    
}
