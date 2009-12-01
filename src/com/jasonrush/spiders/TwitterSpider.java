package com.jasonrush.spiders;

import java.util.ArrayList;
import java.util.List;

import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Tweet;
import twitter4j.Twitter;
import twitter4j.TwitterException;

import com.jasonrush.models.TwitterResultSaver;
import com.jasonrush.models.queries.Queryable;

/**
 * @author jasonrush - 10/31/2009
 * Periodically crawls Twitter search results based on one or more search criteria	
 */
public class TwitterSpider implements Spider {
	//Array of query objects
	protected QueryTracker[] queries;
	//Instance of Twitter API
	protected Twitter twitter;
	//DAO to persist results if needed
	protected TwitterResultSaver resultSaver;
	
	
	public TwitterSpider(TwitterResultSaver resultSaver) {
		this.resultSaver = resultSaver;
		this.twitter = new Twitter();
	}
	
	public TwitterSpider(Queryable[] queries, TwitterResultSaver resultSaver) {
		this(resultSaver);
		this.queries = new QueryTracker[queries.length];
		for (int i=0; i<queries.length; i++) {
			this.queries[i] = new QueryTracker(queries[i]);
		}
	}
	
	/* (non-Javadoc)
	 * @see com.jasonrush.spiders.Spider#processNextBatch()
	 */
	@Override
	public void processNextBatch() {
		List<Tweet> tweets = new ArrayList<Tweet>();
		for (QueryTracker query : queries) {
			//TODO: is there anything we can do to narrow down the raw results some? add some weight?
			QueryResult result = query.doQuery();
			if (result != null) {
				//Weed out duplicate tweets (there's some overlap in these queries)
				List<Tweet> resultTweets = result.getTweets();
				for (Tweet tweet : resultTweets) {
					if (tweets.indexOf(tweet) == -1) {
						//We haven't saved this tweet already
						resultSaver.saveResult(tweet, query.getQuery().getQuery());
						tweets.add(tweet);
					}
				}
			}
		}
	}
	
	public class QueryTracker {
		private Query query;
		
		public QueryTracker(Query query) {
			this.query = query;
			this.query.setRpp(100);
			this.query.setPage(1);
			Long sinceId = initializeSinceId();
			if (sinceId != null) this.query.setSinceId(sinceId);
		}
		
		public QueryTracker(Queryable query) {
			this(new Query(query.toString(Queryable.IMPLICIT_AND)));
		}
		
		public QueryResult doQuery() {
			QueryResult result = null;
			try {
				result = twitter.search(query);
				//Increment the query's sinceId
				query.setSinceId(result.getMaxId());
			} catch (TwitterException e) {
				e.printStackTrace();
			}
			return result;
		}
		
		public Query getQuery() {
			return this.query;
		}
		public void setQuery(Query query) {
			this.query = query;
		}
		
		private Long initializeSinceId() {
			return resultSaver.getLastTweetId(query.getQuery());
		}
	}
	
}