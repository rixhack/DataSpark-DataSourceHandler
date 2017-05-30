/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.tue.dataspark.datasourcehandler;

import java.util.concurrent.DelayQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Luuk Godtschalk
 */
public class RateLimiter extends DelayQueue<RateLimitToken> {
    
    private final long replenishTime;
    
    public RateLimiter(int tokens, long replenishTime) {
        for (int x = 0; x < tokens; x++) {
            this.add(new RateLimitToken(0L));
        }
        this.replenishTime = replenishTime;
    }
    
    public void waitForRate(){
        
        try {
            this.take();
        } catch (InterruptedException ex) {
            Logger.getLogger(RateLimiter.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            this.add(new RateLimitToken(replenishTime));
        }
    }
}
