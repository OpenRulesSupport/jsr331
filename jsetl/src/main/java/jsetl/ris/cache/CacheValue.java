package jsetl.ris.cache;

import jsetl.annotation.NotNull;

import java.util.List;
import java.util.Objects;

/**
 * Instances of this class are the values cached in the Ris Expansion Cache.
 * They contain the actual list of values representing the results of the expansion of an element
 * and a boolean value which tells if the value has been recently got from the cache or not.
 * This last property is used by the clock algorithm that manages the cache.
 *
 * @author Andrea Fois
 * @see CacheKey
 * @see CacheEntry
 */
class CacheValue {

    ///////////////////////////////////////////////////////////////
    ///////// DATA MEMBERS ////////////////////////////////////////
    ///////////////////////////////////////////////////////////////

    /**
     * List containing the values of the expansion of an element
     * of the domain of a {@code Ris}.
     */
    protected final List<Object> value;

    /**
     * Tells if the cache value has been recently got from the cache or not.
     */
    boolean recentlyAccessed = true;


    ///////////////////////////////////////////////////////////////
    ///////// CONSTRUCTORS ////////////////////////////////////////
    ///////////////////////////////////////////////////////////////

    /**
     * Constructs a new {@code CacheValue} with the given {@code value} and
     * the {@code recentlyAccessed} property set to {@code true}.
     * @param value the value to cache. Note that none of the elements of the list
     *              can be {@code null}.
     * @throws NullPointerException if {@code value} contains {@code null} values.
     */
    CacheValue(@NotNull final List<Object> value){
        assert value != null;
        assert value.stream().noneMatch(Objects::isNull);
        if(value.stream().anyMatch(Objects::isNull))
            throw new NullPointerException("NULL VALUES NOT ALLOWED");

        this.value = value;
    }
}
