package com.gildedrose.api.services;

public interface MemcacheService {
    Object get(String key);

    void put(String key, Object value);
    void put(String key, Object value, long expiry);
}
