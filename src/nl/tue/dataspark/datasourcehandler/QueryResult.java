/**
 * Copyright (c) 2017, The Glowsticks.
 * All rights reserved.
 */

package nl.tue.dataspark.datasourcehandler;

/**
 *
 * @author Luuk Godtschalk
 */
public class QueryResult {
    
    private final DataQuery query;
    private final Double result;
    private final Exception exception;
    
    public QueryResult(DataQuery query, Double result, Exception e) {
        if (query == null) {
            throw new IllegalArgumentException();
        }
        if (result == null && e == null) {
            throw new IllegalArgumentException();
        }
        this.query = query;
        this.result = result;
        this.exception = e;
    }
    
    public DataQuery getDataQuery() {
        return this.query;
    }
    
    public Boolean isSuccesful() {
        return (exception == null);
    }
    
    public Double getValue() {
        if (!this.isSuccesful()) {
            throw new IllegalStateException();
        }
        return this.result;
    }
    
    @Override
    public String toString() {
        if (exception != null) {
            return this.query.toString() + ": " + this.exception.toString();
        }
        return this.query.toString() + ": " + this.result;
    }
    
}
