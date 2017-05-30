/**
 * Copyright (c) 2017, The Glowsticks.
 * All rights reserved.
 */

package nl.tue.dataspark.datasourcehandler;

import java.time.Instant;

/**
 *
 * @author Luuk Godtschalk
 */
public class CacheItem implements Comparable<CacheItem> {
    
    private final QueryResult result;
    private final Integer key;
    private final Instant inserted;
    private final Instant expires;
    
    public CacheItem(Integer key, QueryResult result, Instant inserted, Instant expires) {
        this.key = key;
        this.result = result;
        this.inserted = inserted;
        this.expires = expires;
    }
    
    public CacheItem(Integer key, QueryResult result, long ttl) {
        this(key, result, Instant.now(), Instant.now().plusMillis(ttl));
    }
    
    public QueryResult getResult() {
        return this.result;
    }
    
    public Instant getExpires() {
        return this.expires;
    }
    
    public long getTTL() {
        return this.expires.toEpochMilli() - System.currentTimeMillis();
    }
    
    public boolean isStale() {
        return this.getTTL() < 0;
    }

    @Override
    public int compareTo(CacheItem o) {
        return Long.compare(this.getExpires().toEpochMilli(), o.getExpires().toEpochMilli());
    }
    
}
