/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.tue.dataspark.datasourcehandler;

import java.util.Map;
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
public class TwitterDataSourceClientTest {
    
    public TwitterDataSourceClientTest() {
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
     * Test of doQuery method, of class TwitterDataSourceClient.
     */
    @Test
    public void testDoQuery() {
        System.out.println("doQuery");
        Map<String, String> parameters = null;
        TwitterDataSourceClient instance = TwitterDataSourceClient.getClient();
        Double result = instance.doQuery(parameters);
    }

    
}
