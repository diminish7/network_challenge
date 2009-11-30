package com.jasonrush.models;

import twitter4j.Tweet;

public interface TwitterResultSaver {
	/**
	 * Saves a search result to a persistent store
	 * @param result A query result from a Twitter search
	 */
	public void saveResult(Tweet tweet, String searchPhrase);
	/**
	 * Gets the most last saved id from the database
	 */
	public Long getLastTweetId(String searchPhrase);
}
