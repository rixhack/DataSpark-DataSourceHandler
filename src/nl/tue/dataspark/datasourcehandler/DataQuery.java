/**
 * Copyright (c) 2017, The Glowsticks.
 * All rights reserved.
 */

package nl.tue.dataspark.datasourcehandler;

import java.util.Map;

/**
 *
 * @author Luuk Godtschalk
 */
public class DataQuery {
    
    private final String dataSource;
    private final Map<String, String> parameters;
    
    
    public DataQuery(String dataSource, Map<String, String> parameters) {
        this.dataSource = dataSource;
        this.parameters = parameters;
    }
    
    public String getDataSource() {
        return this.dataSource;
    }
    
    public String getParameter(String parameter) {
        return this.parameters.get(parameter);
    }
    
    
    @Override
    public String toString() {
        return this.dataSource + "(" + this.parameters + ")";
    }
    
}
