package org.ccci.util;

import java.util.Map;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;

/**
 * A simple, single-threaded cache.  No invalidation, etc.
 * 
 * @author Matt Drees
 *
 * @param <K> type of keys
 * @param <V> type of values
 */
public class SimpleCache<K, V>
{
    private final Map<K, V> timesheetCache = Maps.newHashMap();

    /**
     * Returns true if a value has been cached against the given {@code key}
     * @param key
     * @return
     */
    public boolean containsKey(K key)
    {
        return timesheetCache.containsKey(key);
    }
    
    /**
     * Returns the cached value.  Returns null only if null was cached.
     * 
     * @param key
     * @return
     * @throws IllegalArgumentException if nothing has been cached against the given {@code key}
     */
    public V get(K key)
    {
        Preconditions.checkNotNull(key, "key is null");
        Preconditions.checkArgument(containsKey(key), "cache does not contain key %s", key);
        return timesheetCache.get(key);
    }

    /**
     * Caches a value against the given {@code key}
     * @param key may not be null
     * @param value may be null
     */
    public void put(K key, V value)
    {
        Preconditions.checkNotNull(key, "key is null");
        timesheetCache.put(key, value);
    }

}
