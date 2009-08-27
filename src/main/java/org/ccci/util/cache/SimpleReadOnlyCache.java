package org.ccci.util.cache;

import java.io.Serializable;
import java.util.Map;

import com.google.common.collect.Maps;

/**
 * No way to clear entries; should be used e.g. for a request-scoped cache
 * @author Matt Drees
 *
 * @param <E>
 * @param <K>
 */

//TODO: use or delete
public class SimpleReadOnlyCache<E, K> implements Serializable
{

    private static final long serialVersionUID = 1L;

    private Map<K, E> cacheMap = Maps.newHashMap();
    
    public E get(K key)
    {
        return cacheMap.get(key);
    }
    
    public void set(K key, E value)
    {
        cacheMap.put(key, value);
    }
    
    public boolean contains(K key)
    {
        return cacheMap.containsKey(key);
    }
}
