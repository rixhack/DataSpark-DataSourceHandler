/**
 * Copyright (c) 2017, The Glowsticks.
 * All rights reserved.
 */

package nl.tue.dataspark.datasourcehandler;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Luuk Godtschalk
 */
public class Cache {
    
    private final Map<Integer, CacheItem> cache = new HashMap();
    private final long TTL = 1000 * 60;
    
    public void addDataValue(String dataSource, Map<String, String> parameters, QueryResult result) {
        Integer key = getKey(dataSource, parameters);
        cache.put(key, new CacheItem(key, result, TTL));
    }
    
    public CacheItem getStoredDataValue(String dataSource, Map<String, String> parameters) {
        Integer key = getKey(dataSource, parameters);
        CacheItem result = cache.get(key);
        if (result == null || result.isStale()) {
            return null;
        }
        return result;
    };
    
    public int getKey(String dataSource, Map<String, String> parameters) {
        if (parameters == null) {
            parameters = new HashMap();
        }
        return dataSource.hashCode() + parameters.hashCode();
    }
    
}
