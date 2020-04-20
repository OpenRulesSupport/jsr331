package jsetl.ris.cache;

import jsetl.Ris;
import jsetl.annotation.NotNull;
import jsetl.annotation.Nullable;

import java.util.*;

/**
 * This class implements a cache for the expansion of {@code Ris}.
 * It stores the associations between cache keys and cache values to provide
 * fast access to the cached {@code Ris} elements expansions.
 * When the cache {@code maxSize} is reached and a new cache entry is to be
 * added a cache entry is removed from the cache using the clock replacement algorithm.
 * @author Andrea Fois
 * @see CacheEntry
 * @see CacheKey
 * @see CacheValue
 * @see Ris
 */
public class RisExpansionCache implements Cloneable {

    ///////////////////////////////////////////////////////////////
    ///////// STATIC MEMBERS //////////////////////////////////////
    ///////////////////////////////////////////////////////////////

    /**
     * This property contains the default cache maximum size. Its value is {@code 1000}.
     */
    public static final int DEFAULT_CACHE_MAX_SIZE = 1000;


    ///////////////////////////////////////////////////////////////
    ///////// DATA MEMBERS ////////////////////////////////////////
    ///////////////////////////////////////////////////////////////

    /**
     * The maximum size of the cache.
     */
    private int maxSize = DEFAULT_CACHE_MAX_SIZE;

    /**
     * Map containing the cache entries.
     */
    private @NotNull Map<CacheKey,  CacheValue> cacheMap;

    /**
     * Cyclic list enclosed in an easy to use interface that contains
     * the same cache entries as {@code cacheMap}. It is used
     * internally to implement the clock replacement algorithm to decide
     * which cache entry should be removed when there is no more space.
     */
    private  @NotNull CacheEntriesAccessList cacheEntriesAccessList;


    ///////////////////////////////////////////////////////////////
    ///////// CONSTRUCTORS ////////////////////////////////////////
    ///////////////////////////////////////////////////////////////

    /**
     * Constructs an empty Ris Expansion Cache.
     */
    public RisExpansionCache(){
        cacheMap = new HashMap<>();
        cacheEntriesAccessList = new CacheEntriesAccessList();
    }

    /**
     * Constructs a Ris Expansion Cache that is the copy of the argument {@code risExpansionCache}.
     * The copy contains a copy of both the map and the list of entries.
     * @param risExpansionCache the cache to copy.
     */
    private RisExpansionCache(@NotNull RisExpansionCache risExpansionCache){
        assert risExpansionCache != null;

        maxSize = risExpansionCache.maxSize;
        cacheMap = new HashMap<>(risExpansionCache.cacheMap);
        cacheEntriesAccessList = new CacheEntriesAccessList(risExpansionCache.cacheEntriesAccessList);
    }


    ///////////////////////////////////////////////////////////////
    ///////// PUBLIC METHODS //////////////////////////////////////
    ///////////////////////////////////////////////////////////////

    /**
     * Adds to the cache the fact that the provided {@code Ris} expands the {@code element}
     * into the {@code expansions} list.
     * @param ris a {@code Ris}.
     * @param element an element that is expanded by {@code Ris}.
     * @param expansions the expansion of {@code element} according to {@code ris}.
     */
    public void put(@NotNull Ris ris, @NotNull Object element, @NotNull List<Object> expansions){
        Objects.requireNonNull(ris);
        Objects.requireNonNull(element);
        Objects.requireNonNull(expansions);

        CacheKey cacheKey = new CacheKey(ris.getControlTerm(), element, ris.getFilter(), ris.getPattern());

        if(size() >= maxSize()){
            removeOneEntry();
        }

        CacheValue cacheValue =  new CacheValue(expansions);
        cacheMap.put(cacheKey, cacheValue);
        cacheEntriesAccessList.add(cacheKey, cacheValue);
    }

    /**
     * Returns the expansion of the {@code element} according to {@code ris}. The result is {@code null}
     * if there is no cached expansion found.
     * @param ris a {@code Ris}.
     * @param element an element of the {@code Ris}.
     * @return the expansion, if found, {@code null} otherwise.
     */
    public @Nullable
    List<Object> getExpansion(@NotNull Ris ris, @NotNull Object element){
        Objects.requireNonNull(ris);
        Objects.requireNonNull(element);

        CacheKey cacheKey = new CacheKey(ris.getControlTerm(), element, ris.getFilter(), ris.getPattern());

        CacheValue cacheValue = cacheMap.get(cacheKey);
        if(cacheValue != null){
            cacheValue.recentlyAccessed = true;
            return cacheValue.value;
        }
        else
            return null;
    }

    /**
     * Returns the number of entries in the cache.
     * @return the size of the cache.
     */
    public int size(){
        return cacheMap.size();
    }

    /**
     * Creates a clone of the cache. It is the same as {@code new RisExpansionCache(this)}.
     * @return a clone of the cache.
     */
    public @NotNull RisExpansionCache clone(){
        return new RisExpansionCache(this);
    }

    /**
     * Sets the cache map and list to those in the parameter {@code risExpansionCache}.
     * @param risExpansionCache a cache.
     */
    public void restore(RisExpansionCache risExpansionCache){
        maxSize = risExpansionCache.maxSize;
        cacheMap = risExpansionCache.cacheMap;
        cacheEntriesAccessList = risExpansionCache.cacheEntriesAccessList;
    }

    /**
     * Returns the maximum size of the cache.
     * @return the maximum size of the cache.
     */
    public int maxSize(){
        return maxSize;
    }

    /**
     * Sets the maximum size of the cache.
     * If the new size is less than the current number of
     * elements in the cache, the exceeding elements are removed
     * according to the clock replacement algorithm.
     * @param maxSize the new maximum size of the cache. It must be greater than 0.
     */
    public void setMaxSize(int maxSize){
        assert maxSize > 0;
        if(this.maxSize > maxSize){
            int diff = maxSize - this.maxSize;
            for(int i = 0; i < diff; ++i)
                removeOneEntry();
        }
        this.maxSize = maxSize;
    }

    /**
     * Clears the cache.
     */
    public void clear(){
        cacheMap.clear();
        cacheEntriesAccessList = new CacheEntriesAccessList();
    }


    ///////////////////////////////////////////////////////////////
    ///////// PRIVATE METHODS /////////////////////////////////////
    ///////////////////////////////////////////////////////////////

    /**
     * Removes one entry from the cache according to the clock replacement algorithm.
     * @throws IllegalStateException if the cache is empty.
     */
    private void removeOneEntry(){
        if(cacheMap.isEmpty())
            throw new IllegalStateException("CANNOT REMOVE ONE ENTRY FROM AN EMPTY CACHE");
        do{
            CacheEntry cacheEntry = cacheEntriesAccessList.nextEntry();
            CacheValue cacheValue = cacheEntry.value;
            if(!cacheValue.recentlyAccessed) {
                cacheMap.remove(cacheEntry.key);
                cacheEntriesAccessList.removeLast();
                break;
            }
            else
                cacheValue.recentlyAccessed = false;
        }while(true);
    }





}
