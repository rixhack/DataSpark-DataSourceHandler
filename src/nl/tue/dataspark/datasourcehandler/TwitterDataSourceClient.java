/**
 * Copyright (c) 2017, The Glowsticks.
 * All rights reserved.
 */
package nl.tue.dataspark.datasourcehandler;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import twitter4j.GeoLocation;
import twitter4j.Query;
import twitter4j.Query.ResultType;
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
    private List<Status> tweets;
    private int tweetTimeWindow;
    private String queryString;

    private GeoLocation location = new GeoLocation(51.446903, 5.488588);

    private TwitterDataSourceClient() {
        super();
        this.count = 0;

        tokenBucket = new RateLimiter(4, TIMEWINDOW / 100);
        twitter = TwitterFactory.getSingleton();
        queryString = "";
        tweetTimeWindow = 15;
        tweets = new ArrayList<>();
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
    public synchronized Double doQuery(DataQuery query) throws Exception {
        tokenBucket.waitForRate();
        Query twitterQuery = new Query(null);
        twitterQuery.setResultType(ResultType.recent);
        twitterQuery.setCount(100);
        String newQuery = query.getParameter("query");
        int newTweetTimeWindow = Integer.parseInt(query.getParameter("timeWindow"));
        if (!queryString.equals(newQuery) || tweetTimeWindow != newTweetTimeWindow) {
            tweets.clear();
            queryString = newQuery;

            int reqTweetTimeWindow = Integer.parseInt(query.getParameter("timeWindow"));
            if (reqTweetTimeWindow < 1) {
                tweetTimeWindow = 1;
            } else if (reqTweetTimeWindow > 10080) {
                tweetTimeWindow = 10080;
            } else {
                tweetTimeWindow = reqTweetTimeWindow;
            }
        }
        twitterQuery.setQuery(queryString);
        setDate(twitterQuery);
        if (tweets.isEmpty()) {
            getAllTweets(twitterQuery);
        } else {
            getNewTweets(twitterQuery);
        }

        deleteOldTweets(); // Delete tweets outside the time window.

        if (!tweets.isEmpty()) {
            System.out.println("First one: https://twitter.com/statuses/" + tweets.get(0).getId());
            System.out.println("Last one: https://twitter.com/statuses/" + tweets.get(tweets.size() - 1).getId());
        }
        System.out.println("Size: " + tweets.size());
        return (double) tweets.size();
    }

    private void getAllTweets(Query tQuery) throws Exception {
        int done = 0;
        QueryResult qra = twitter.search(tQuery);
        List<Status> firstPageTweets = new ArrayList<Status>();
        for (Status s : qra.getTweets()) {
            done = checkTweet(s, firstPageTweets);
            if (done == 1) {
                break;
            }
        }
        tweets.addAll(tweets.size(), firstPageTweets);
        while (done == 0) {
            while (qra.hasNext() && done == 0) {
                Query pageQuery = qra.nextQuery();
                qra = twitter.search(pageQuery);
                List<Status> pageTweets = new ArrayList<Status>();
                for (Status s : qra.getTweets()) {
                    done = checkTweet(s, pageTweets);
                    if (done == 1) {
                        break;
                    }
                }
                tweets.addAll(tweets.size(), pageTweets);
            }
            if (done == 0 && !tweets.isEmpty()) {
                System.out.println("Current size: " + tweets.size());
                System.out.println("Current last: " + tweets.get(tweets.size() - 1).getCreatedAt());
                System.out.println("Be persistent!");
                Query newQuery = new Query();
                newQuery.setQuery(queryString);
                newQuery.setCount(100);
                newQuery.setResultType(ResultType.recent);
                newQuery.setSince(tQuery.getSince());
                if (!tweets.isEmpty()) {
                    newQuery.setMaxId(tweets.get(tweets.size() - 1).getId() - 1);
                }
                qra = twitter.search(newQuery);
                List<Status> nextPageTweets = new ArrayList<Status>();
                for (Status s : qra.getTweets()) {
                    done = checkTweet(s, nextPageTweets);
                    if (done == 1) {
                        break;
                    }
                }
                tweets.addAll(tweets.size(), nextPageTweets);
            }
        }
    }

    private void getNewTweets(Query tQuery) throws Exception {
        int done = 0;
        tQuery.setSinceId(tweets.get(0).getId());
        System.out.println("Getting tweets newer than: " + tweets.get(0).getCreatedAt());
        QueryResult qrn = twitter.search(tQuery);
        List<Status> firstPageTweets = new ArrayList<Status>();
        for (Status s : qrn.getTweets()) {
            if (s.getCreatedAt().getTime() > tweets.get(0).getCreatedAt().getTime()) {
                if (!s.isRetweet() && s.getText().toLowerCase().contains(queryString.toLowerCase())) {
                    firstPageTweets.add(s);
                }
            } else {
                done = 1;
                break;
            }
        }
        tweets.addAll(0, firstPageTweets);
        int index = firstPageTweets.size();
        while (qrn.hasNext() && done == 0) {
            Query pageQuery = qrn.nextQuery();
            qrn = twitter.search(pageQuery);
            List<Status> pageTweets = new ArrayList<Status>();
            for (Status s : qrn.getTweets()) {
                if (s.getCreatedAt().getTime() > tweets.get(0).getCreatedAt().getTime()) {
                    if (!s.isRetweet() && s.getText().toLowerCase().contains(queryString.toLowerCase())) {
                        firstPageTweets.add(s);
                    }
                } else {
                    done = 1;
                    break;
                }
            }
            tweets.addAll(index, pageTweets);
            index = index + pageTweets.size();
        }
    }

    private void deleteOldTweets() {
        for (int i = tweets.size() - 1; i >= 0; i--) {
            Date createdAt = tweets.get(i).getCreatedAt();
            if (System.currentTimeMillis() - createdAt.getTime() > tweetTimeWindow * 60000) {
                System.out.println("Deleted: https://twitter.com/statuses/" + tweets.get(i).getId());
                tweets.remove(i);
            } else {
                break;
            }
        }
    }

    private int checkTweet(Status s, List<Status> list) {
        if (System.currentTimeMillis() - s.getCreatedAt().getTime() <= tweetTimeWindow * 60000) {
            if (!s.isRetweet() && s.getText().toLowerCase().contains(queryString.toLowerCase())) {
                list.add(s);
            }
        } else {
            return 1;
        }
        return 0;
    }

    private void setDate(Query twitterQuery) {
        int days = tweetTimeWindow / 1440 + 1;
        Calendar fDate = Calendar.getInstance();
        fDate.add(Calendar.DATE, -days);
        int year = fDate.get(Calendar.YEAR);
        int month = fDate.get(Calendar.MONTH) + 1;
        int dayOfMonth = fDate.get(Calendar.DAY_OF_MONTH);
        twitterQuery.setSince(year + "-" + month + "-" + dayOfMonth);
    }

    @Override
    public int getRate() {
        return 5;
    }

    @Override
    public String getDataSourceName() {
        return "twitter";
    }

    @Override
    public void setup() {
        
    }
    
    public List<Status> getTweets() {
        return tweets;
    }
    
    public int getTweetTimeWindow() {
        return tweetTimeWindow;
    }
    
    public String getQuery(){
        return queryString;
    }

}
