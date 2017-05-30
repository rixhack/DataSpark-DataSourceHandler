/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.tue.dataspark.datasourcehandler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author s148084
 */
public class RateLimiterTest {
    
    public RateLimiterTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of waitForRate method, of class RateLimiter.
     */
    @Test
    public void testBulk() throws Exception {
        final int n = 10;
        final long t = 100L;
        System.out.println("waitForRate");
        RateLimiter instance = new RateLimiter(n, t);
        
        Collection<Thread> threads = new ArrayList(1000);
        for (int x = 0; x < 1000; x++) {
            final int count = x;
            Thread thread = new Thread() {
                @Override
                public void run() {
                    instance.waitForRate();
                    System.out.println(count);
                }
            };
            System.out.println("Created task");
            thread.start();
            threads.add(thread);
        }
        
        for(Thread thread: threads) {
            thread.join();
        }
    }
    
}
