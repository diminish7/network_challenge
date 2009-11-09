package com.jasonrush.models;

import uk.org.catnip.eddie.Entry;


public interface FeedResultSaver {
	/**
	 * Saves a search result to a persistent store
	 * @param entry The the article object from the feed
	 * @param searchPhrase The search query
	 */
	public void saveResult(Entry entry, String searchPhrase);
}
