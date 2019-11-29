package com.gildedrose.api.services;

public interface MemcacheService {
    public abstract Object get(String key);

    public abstract void put(String key, Object value);
    public abstract void put(String key, Object value, long expiry);
}
