package jsetl.ris.cache;

import jsetl.annotation.NotNull;
import jsetl.ris.cache.circularlist.CircularList;
import jsetl.ris.cache.circularlist.CircularListIterator;

/**
 * This class provides an easier to use interface to
 * add elements and iterate through a circular list containing
 * the Ris Expansion Cache entries.
 *
 * @author Andrea Fois
 * @see CacheEntry
 * @see CircularList
 * @see CircularListIterator
 * @see RisExpansionCache
 */
class CacheEntriesAccessList implements Cloneable{

    ///////////////////////////////////////////////////////////////
    ///////// DATA MEMBERS ////////////////////////////////////////
    ///////////////////////////////////////////////////////////////

    /**
     * Circular list containing the cache entries.
     */
    private final @NotNull
    CircularList<CacheEntry> entries;

    /**
     * Current position in the (cyclic) iteration of the list.
     */
    private @NotNull CircularListIterator<CacheEntry> currentPosition;

    ///////////////////////////////////////////////////////////////
    ///////// CONSTRUCTORS ////////////////////////////////////////
    ///////////////////////////////////////////////////////////////

    /**
     * Creates a new cyclic list of cache entries.
     */
    CacheEntriesAccessList(){
        entries = new CircularList<>();
        currentPosition = null;
    }

    /**
     * Creates a copy of the parameter {@code cacheEntriesAccessList}, i.e. a completely separate
     * list that contains the same elements of {@code cacheEntriesAccessList} in the same order.
     * @param cacheEntriesAccessList cache entries list to copy.
     */
    CacheEntriesAccessList(final @NotNull CacheEntriesAccessList cacheEntriesAccessList){
        assert  cacheEntriesAccessList != null;

        entries = new CircularList<>(cacheEntriesAccessList.entries);
        currentPosition = entries.isEmpty() ? null : entries.iterator();
    }


    ///////////////////////////////////////////////////////////////
    ///////// PUBLIC METHODS //////////////////////////////////////
    ///////////////////////////////////////////////////////////////

    /**
     * Creates a cache entry using the given key and value and adds it to
     * the circular list.
     * @param cacheKey key of the cache entry.
     * @param cacheValue value of the cache entry.
     */
    void add(final @NotNull CacheKey cacheKey, final @NotNull CacheValue cacheValue){
        assert cacheKey != null;
        assert cacheValue != null;

        boolean wasEmpty = entries.isEmpty();
        entries.add(new CacheEntry(cacheKey, cacheValue));
        if(wasEmpty)
            currentPosition = entries.iterator();
    }

    /**
     * Removes the last entry returned by the method {@code nextEntry}.
     */
    void removeLast(){
        currentPosition.remove();
    }

    /**
     * Returns the next entry in the cyclic list.
     * @return the next entry.
     */
    @NotNull CacheEntry nextEntry(){
        CacheEntry cacheEntry = currentPosition.next();

        assert cacheEntry != null;
        return cacheEntry;
    }

    /**
     * Checks whether the list is empty or not.
     * @return {@code true} if the list is empty, {@code false} otherwise.
     */
    boolean isEmpty(){
        return entries.isEmpty();
    }

}
