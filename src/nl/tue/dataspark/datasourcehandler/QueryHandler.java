/**
 * Copyright (c) 2017, The Glowsticks.
 * All rights reserved.
 */

package nl.tue.dataspark.datasourcehandler;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

/**
 *
 * @author Luuk Godtschalk
 */
public class QueryHandler {
    
    private Cache cache;
    private Map<String, AbstractDataSourceClient> clients;
    
    private ExecutorService pool;
    
    public QueryHandler() {
        cache = new Cache();
        clients = new HashMap();
        clients.put("random", RandomDataSourceClient.getClient());
        clients.put("twitter", TwitterDataSourceClient.getClient());
        
        pool = Executors.newFixedThreadPool(10);
    }
    
    public QueryResult getDataValue(String dataSource, Map<String, String> parameters) throws Exception {
        DataQuery query = new DataQuery(dataSource, parameters);
        CacheItem cacheResult = cache.getStoredDataValue(dataSource, parameters);
        if (cacheResult != null && 1 == 2) {
            return cacheResult.getResult();
        } else {
            //return new DataValue("Coosto", new HashMap(), 12.0);
            AbstractDataSourceClient client = clients.get(dataSource);
            if (client == null) {
                throw new Exception("Not implemented: " + dataSource);
            }
            
            FutureTask<QueryResult> task = client.queryDataValue(query);
            pool.execute(task);
            //task.run();
            QueryResult result = task.get();
            
            //DataValue result = client.queryDataValue(parameters);
            cache.addDataValue(dataSource, parameters, result);
            return result;
        }
    }
    
    public QueryResult getDataValue(String dataSource) throws Exception {
        return getDataValue(dataSource, new HashMap());
    }
    
}
