/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.tue.dataspark.datasourcehandler;

import java.util.HashMap;
import java.util.Map;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.FixMethodOrder;
import org.junit.runners.MethodSorters;
import twitter4j.Status;

/**
 *
 * @author s148084
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TwitterDataSourceClientTest {
    
    TwitterDataSourceClient tdsc;
    int margin = 5000;

    public TwitterDataSourceClientTest() {
        tdsc = TwitterDataSourceClient.getClient();
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

    
    // Test default values for parameters in class TwitterDataSourceClient.
    @Test
    public void test1Default() {
        assertEquals("", tdsc.getQuery());
        assertTrue(15 == tdsc.getTweetTimeWindow());
        assertTrue(tdsc.getTweets().isEmpty());
    }
    
    
     // Test doQuery method of class TwitterDataSourceClient.
    @Test
    public void test2DoQuery() throws Exception {
        Map<String, String> parameters = new HashMap();
        parameters.put("query", "eindhoven");
        parameters.put("timeWindow", "30");
        DataQuery dq = new DataQuery("twitter", parameters);
        Double result = tdsc.doQuery(dq);
        assertEquals("eindhoven", tdsc.getQuery());
        assertTrue(30 == tdsc.getTweetTimeWindow());
        assertFalse(tdsc.getTweets().isEmpty());
        assertTrue(result.intValue() == tdsc.getTweets().size());
    }
    @Test
    public void test3TweetsContainQuery() throws InterruptedException {
        for (Status s : tdsc.getTweets()) {
            assertTrue(s.getText().toLowerCase().contains("eindhoven"));
        }
    }
    
    @Test
    public void test4TweetsInsideTimeWindow() throws InterruptedException {
        for (Status s : tdsc.getTweets()) {
            assertTrue(System.currentTimeMillis() - s.getCreatedAt().getTime() <= (tdsc.getTweetTimeWindow() * 60000 + margin));
        }
    }

    @Test
    public void test5MinTimeWindow() throws Exception {
        Map<String, String> parameters = new HashMap();
        parameters.put("query", "eindhoven");
        parameters.put("timeWindow", "0");
        DataQuery dq = new DataQuery("twitter", parameters);
        Double result = tdsc.doQuery(dq);
        assertEquals(1, tdsc.getTweetTimeWindow());
    }
}
