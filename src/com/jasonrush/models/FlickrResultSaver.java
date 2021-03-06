package com.jasonrush.models;

import java.util.Date;

import com.aetrion.flickr.photos.Photo;

public interface FlickrResultSaver {
	/**
	 * Saves a search result to a persistent store
	 * @param photo The photo object from Flickr
	 * @param searchPhrase The search query
	 */
	public void saveResult(Photo photo, String searchPhrase);
	/**
	 * Gets the last saved date from the database
	 */
	public Date getLastPhotoDate(String searchPhrase);
}
