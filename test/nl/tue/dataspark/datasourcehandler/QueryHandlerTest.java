/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.tue.dataspark.datasourcehandler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
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
public class QueryHandlerTest {
    
    public QueryHandlerTest() {
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
     * Test of getDataValue method, of class QueryHandler.
     * @throws java.lang.Exception
     */
    @Test(timeout=500)
    public void testGetDataValue() throws Exception {
        String dataSource = "random";
        QueryHandler instance = new QueryHandler();
        QueryResult result = instance.getDataValue(dataSource);
        assert(result.isSuccesful());
        assertEquals(dataSource, result.getDataQuery().getDataSource());
        System.out.println("c1");
        //throw new NullPointerException("screw you");
        assertNotNull(result.getValue());
    }
    
    @Test(timeout=1200)
    public void testGetDataValueBulk() throws Exception {
        final QueryHandler instance = new QueryHandler();
        
        class getDataRunnable implements Runnable {

            @Override
            public void run() {
                try {
                    System.out.println("run");
                    instance.getDataValue("random");
                } catch (Exception ex) {
                    Logger.getLogger(QueryHandlerTest.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            
        }
        Collection<Thread> threads = new ArrayList(100);
        for (int x = 0; x < 100; x++) {
            Thread t = new Thread(new getDataRunnable());
            System.out.println("Created task");
            t.start();
            threads.add(t);
        }
        
        for(Thread t: threads) {
            t.join();
            System.out.println("done");
        }
    }

    
}
