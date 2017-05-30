/**
 * Copyright (c) 2017, The Glowsticks.
 * All rights reserved.
 */

package nl.tue.dataspark.datasourcehandler;

import java.io.IOException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.*;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;


/**
 *
 * @author Luuk Godtschalk
 */
public class TwitterDataSourceClientOld extends AbstractDataSourceClient {
    
    private int count;
    private static final TwitterDataSourceClientOld instance = new TwitterDataSourceClientOld();
    private final long TIMEWINDOW = 1000L * 60 * 15;
    
    private final RateLimiter tokenBucket;
    private CloseableHttpClient httpClient;

    private TwitterDataSourceClientOld() {
        super();
        this.count = 0;
        
        tokenBucket = new RateLimiter(15, TIMEWINDOW);
        httpClient = HttpClients.createDefault();
        
    }
    
    public static TwitterDataSourceClientOld getClient() {
        return instance;
    }

    @Override
    public synchronized Double doQuery(Map<String, String> parameters) {
        tokenBucket.waitForRate();
        HttpGet request = new HttpGet("https://api.twitter.com/1.1/search/tweets.json?q=eindhoven");
        request.setHeader(HttpHeaders.AUTHORIZATION, "Bearer AAAAAAAAAAAAAAAAAAAAAErk0gAAAAAAhGkNpAItosu1b3w8T9Hm1tN3AwQ%3D5QXEesWv0NAlxTjHpZtv3GAhXNtKwBnOhKTVWD6mpXlycDjj20");
        HttpResponse response;
        try {
            response = httpClient.execute(request);
            System.out.println(response);
        } catch (IOException ex) {
            Logger.getLogger(TwitterDataSourceClientOld.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public int getRate() {
        return 5;
    }

    @Override
    public String getDataSourceName() {
        return "random";
    }

    @Override
    public void setup() {
        return;
    }
    
    
}
