/**
 * Copyright (c) 2017, The Glowsticks.
 * All rights reserved.
 */

package nl.tue.dataspark.datasourcehandler;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import twitter4j.GeoLocation;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;




/**
 *
 * @author Luuk Godtschalk
 */
public class TwitterDataSourceClient extends AbstractDataSourceClient {
    
    private int count;
    private static final TwitterDataSourceClient INSTANCE = new TwitterDataSourceClient();
    private final long TIMEWINDOW = 1000L * 60 * 15;
    
    private final RateLimiter tokenBucket;
    private Twitter twitter;
    
    private GeoLocation location = new GeoLocation(51.446903, 5.488588);

    private TwitterDataSourceClient() {
        super();
        this.count = 0;
        
        tokenBucket = new RateLimiter(4, TIMEWINDOW/100);
        twitter = TwitterFactory.getSingleton();
        try {
            twitter.getOAuth2Token();
        } catch (TwitterException ex) {
            Logger.getLogger(TwitterDataSourceClient.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    public static TwitterDataSourceClient getClient() {
        return INSTANCE;
    }

    @Override
    public synchronized Double doQuery(DataQuery query) throws TwitterException {
        tokenBucket.waitForRate();
        Query twitterQuery = new Query(null);
        twitterQuery.setCount(100);
        twitterQuery.setGeoCode(location, 1, Query.Unit.km);
        QueryResult result = twitter.search(twitterQuery);
        for (Status status : result.getTweets()) {
            System.out.println("@" + status.getUser().getScreenName() + "(" + status.getCreatedAt() + "):" + status.getText());
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
