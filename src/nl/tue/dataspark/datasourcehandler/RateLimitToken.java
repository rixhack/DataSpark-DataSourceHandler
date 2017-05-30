/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.tue.dataspark.datasourcehandler;

import java.time.Instant;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author s148084
 */
public class RateLimitToken implements Delayed {
    
    private Instant instant;
    
    public RateLimitToken(Instant instant) {
        this.instant = instant;
    }
    
    public RateLimitToken(long delay) {
        this(Instant.now().plusMillis(delay));
    }

    @Override
    public long getDelay(TimeUnit unit) {
        long millis = this.instant.toEpochMilli() - System.currentTimeMillis();
        return unit.convert(millis, TimeUnit.MILLISECONDS);
    }

    @Override
    public int compareTo(Delayed o) {
        return Long.compare(this.getDelay(TimeUnit.MILLISECONDS), o.getDelay(TimeUnit.MILLISECONDS));
    }
    
}
