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

/**
 *
 * @author s148084
 */
public class CacheTest {
    
    private Cache instance;
    
    public CacheTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
        instance = new Cache();
    }
    
    @After
    public void tearDown() {
    }
    

    @Test
    public void testEmptyCache() {
        String dataSource = "twitter";
        Map<String, String> parameters = new HashMap();
        
        CacheItem result = instance.getStoredDataValue(dataSource, parameters);
        assertNull(result);
    }

    @Test
    public void testStoredDataValue() {
        String dataSource = "twitter";
        Map<String, String> parameters = new HashMap();
        QueryResult qResult = new QueryResult(new DataQuery(dataSource, parameters), 0.0, null);
        instance.addDataValue(dataSource, parameters, qResult);
        
        CacheItem result = instance.getStoredDataValue(dataSource, parameters);
        assertEquals(qResult, result.getResult());
    }
    
    @Test
    public void testDifferentParameters() {
        String dataSource = "twitter";
        
        Map<String, String> parametersA = new HashMap();
        parametersA.put("query", "#Eindhoven");
        QueryResult qResultA = new QueryResult(new DataQuery(dataSource, parametersA), 1.0, null);
        instance.addDataValue(dataSource, parametersA, qResultA);
        
        Map<String, String> parametersB = new HashMap();
        parametersB.put("query", "#Eindhoven");
        QueryResult qResultB = new QueryResult(new DataQuery(dataSource, parametersB), 2.0, null);
        instance.addDataValue(dataSource, parametersB, qResultB);
        
        CacheItem result = instance.getStoredDataValue(dataSource, parametersB);
        assertEquals(qResultB, result.getResult());
        System.out.println(result.getResult());
        
    }
    
    @Test
    public void testDuplicateKey() {
        String dataSource = "twitter";
        
        Map<String, String> parameters = new HashMap();
        parameters.put("query", "#Eindhoven");
        DataQuery dataQuery = new DataQuery(dataSource, parameters);
                
        instance.addDataValue(dataSource, parameters, new QueryResult(dataQuery, 1.0, null));
        instance.addDataValue(dataSource, parameters, new QueryResult(dataQuery, 2.0, null));
        
        CacheItem result = instance.getStoredDataValue(dataSource, parameters);
        assertEquals(new Double(2.0), result.getResult().getValue());
        System.out.println(result);
    }
    
}
