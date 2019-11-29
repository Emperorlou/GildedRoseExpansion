package com.gildedrose.api.services;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * This is only being used in lieu of a proper memcache service which would have
 * entries that expire naturally. In reality I would have used a real memcache, assuming
 * there is one available in a production environment.
 *
 * Note1: Using a pool to store the MemcacheItem classes for reuse would make this class more GC efficient.
 * Note2: I recognize that this will generate a memory leak as old entries are not purged after they expire unless
 * they're code explicitly calls get() on them.
 */
@Service
public class MemcacheServiceImpl implements MemcacheService {
    private Map<String, MemcacheItem> cache = new HashMap<>();

    public MemcacheServiceImpl() { }

    /**
     * Stores a value in the cache expires by default in 1 hour.
     *
     * @param key
     * @param value
     */
    public void put(String key, Object value) {
        put(key, value, 360000L);
    }

    public void put (String key, Object value, long expiryMs) {
        MemcacheItem existingMemcacheItem = cache.get(key);
        if (existingMemcacheItem==null)
        {
            MemcacheItem item = new MemcacheItem();
            item.value = value;
            item.expiry = System.currentTimeMillis()+expiryMs;
            cache.put(key, item);
        }
        else
        {
            existingMemcacheItem.value = value;
            existingMemcacheItem.expiry = System.currentTimeMillis()+expiryMs;
        }
    }

    /**
     * If a value has expired, this method will return null.
     *
     * @param key
     * @return
     */
    public Object get(String key)
    {
        MemcacheItem item = cache.get(key);

        if (item==null) return null;
        if (item.expiry<=System.currentTimeMillis()) {
            cache.remove(key);
            return null;
        }

        return item.value;
    }

    public class MemcacheItem
    {
        long expiry;
        Object value;
    }
}
