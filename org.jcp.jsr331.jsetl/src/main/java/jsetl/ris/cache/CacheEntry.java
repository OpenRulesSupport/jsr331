package jsetl.ris.cache;

import jsetl.annotation.NotNull;

/**
 * Instances of this class are entries of the Ris Expansion Cache, i.e.: pairs
 * of the form [key, value] in which key is an instance of {@code CacheKey} and
 * value is an instance of {@code CacheValue}.
 * @author Andrea Fois
 * @see CacheKey
 * @see CacheEntry
 */
public class CacheEntry {

    ///////////////////////////////////////////////////////////////
    ///////// DATA MEMBERS ////////////////////////////////////////
    ///////////////////////////////////////////////////////////////

    /**
     * The key of the cache entry.
     */
    public final CacheKey key;

    /**
     * The value of the cache entry.
     */
    public final CacheValue value;


    ///////////////////////////////////////////////////////////////
    ///////// CONSTRUCTORS ////////////////////////////////////////
    ///////////////////////////////////////////////////////////////

    /**
     * Constructs a new cache entry with the given {@code key} and {@code value}.
     * @param key key of the cache entry.
     * @param value value of the cache entry.
     */
    public CacheEntry(@NotNull final CacheKey key, @NotNull final CacheValue value) {
        assert key != null;
        assert value != null;

        this.key = key;
        this.value = value;
    }

}
