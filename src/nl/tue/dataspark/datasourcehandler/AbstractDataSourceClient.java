/**
 * Copyright (c) 2017, The Glowsticks.
 * All rights reserved.
 */

package nl.tue.dataspark.datasourcehandler;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

/**
 *
 * @author Luuk Godtschalk
 */
public abstract class AbstractDataSourceClient {
    
    private FutureTask<Void> setupTask;
    
    
    public FutureTask<QueryResult> queryDataValue(final DataQuery query) {
        Callable callable = new Callable() {
            Double result;
            @Override
            public QueryResult call() {
                try {
                    waitReady();
                    result = doQuery(query);
                } catch (Exception ex) {
                    return new QueryResult(query, null, ex);
                }
                return new QueryResult(query, result, null);
            }
        };
        return new FutureTask<>(callable);
    }
    
    public QueryResult queryDataValueSync(final DataQuery query) throws Exception {
        Double result = doQuery(query);
        return new QueryResult(query, result, null);
    }
    
    protected abstract Double doQuery(DataQuery query) throws Exception;
    
    public abstract int getRate();
    
    public abstract String getDataSourceName();
    
    public void waitReady() throws InterruptedException, ExecutionException {
        if (this.setupTask == null) {
            this.setupTask = new FutureTask<>(new Runnable() {
                @Override
                public void run() {
                    setup();
                }
            }, null);
            this.setupTask.run();
        }
        this.setupTask.get();
    };
    
    public void setup() {
        
    };
    
}
