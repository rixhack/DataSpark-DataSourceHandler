/**
 * Copyright (c) 2017, The Glowsticks.
 * All rights reserved.
 */

package nl.tue.dataspark.datasourcehandler;

import java.util.Random;

/**
 *
 * @author Luuk Godtschalk
 */
public class RandomDataSourceClient extends AbstractDataSourceClient {
    
    private int count;
    private static final AbstractDataSourceClient instance = new RandomDataSourceClient();
    
    private final RateLimiter tokenBucket;

    private RandomDataSourceClient() {
        super();
        this.count = 0;
        
        tokenBucket = new RateLimiter(10, 100L);
    }
    
    public static AbstractDataSourceClient getClient() {
        return instance;
    }

    @Override
    public synchronized Double doQuery(DataQuery query) {
        tokenBucket.waitForRate();
        Random rand = new Random();
        //System.out.println(count++);
        return rand.nextDouble();
    }

    @Override
    public int getRate() {
        return 5;
    }

    @Override
    public String getDataSourceName() {
        return "random";
    }

    @Override
    public void setup() {
        return;
    }
    
    
}
