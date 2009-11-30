package com.jasonrush.models;

import java.util.Date;

import uk.org.catnip.eddie.Entry;


public interface FeedResultSaver {
	/**
	 * Saves a search result to a persistent store
	 * @param entry The the article object from the feed
	 * @param searchPhrase The search query
	 */
	public void saveResult(String source, Entry entry, String searchPhrase);/**
	 * Gets the last saved date from the database
	 */
	public Date getLastPostDate(String source, String searchPhrase);
}
