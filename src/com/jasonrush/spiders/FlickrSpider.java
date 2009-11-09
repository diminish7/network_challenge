package com.jasonrush.spiders;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import com.aetrion.flickr.Flickr;
import com.aetrion.flickr.REST;
import com.aetrion.flickr.photos.Photo;
import com.aetrion.flickr.photos.PhotoList;
import com.aetrion.flickr.photos.PhotosInterface;
import com.aetrion.flickr.photos.SearchParameters;
import com.aetrion.flickr.util.IOUtilities;
import com.jasonrush.models.FlickrResultSaver;

public class FlickrSpider implements Spider {
	private static String apiKey;
    private static String sharedSecret;
    private Flickr flickr;
    private REST rest;
    private boolean hasConnection;
    private PhotosInterface photosInterface;
    
    private ParamsTracker[] queries;
    private FlickrResultSaver resultSaver;
    
	public FlickrSpider(String[] queries, FlickrResultSaver resultSaver) {
		InputStream in = null;
		Properties properties = null;
		hasConnection = true;
        try {
            in = getClass().getResourceAsStream("/flickr.properties");
            properties = new Properties();
			properties.load(in);
			rest = new REST();
			apiKey = properties.getProperty("apiKey");
	        sharedSecret = properties.getProperty("secret");
	        flickr = new Flickr(apiKey, sharedSecret, rest);
	        photosInterface = flickr.getPhotosInterface();
	        this.queries = new ParamsTracker[queries.length];
	        for (int i=0; i<queries.length; i++) {
	        	this.queries[i] = new ParamsTracker(queries[i], photosInterface);
	        }
	        this.resultSaver = resultSaver;
		} catch (Exception e) {
			e.printStackTrace();
			hasConnection = false;
        } finally {
            IOUtilities.close(in);
        }
	}
	
	@Override
	public void processNextBatch() {
		if (!hasConnection) return;	//Something failed to initialize!
		List<FlickrResult> results = new ArrayList<FlickrResult>();
		for (ParamsTracker query : queries) {
			query.doQuery(results);
		}
		for (FlickrResult result : results) {
			resultSaver.saveResult(result.getPhoto(), result.getQuery());
		}
	}
	
	public class ParamsTracker {
		private SearchParameters params;
		private PhotosInterface photosInterface;
		private Date lastDate;
		
		public ParamsTracker(String query, PhotosInterface photosInterface) {
			this.photosInterface = photosInterface;
			Calendar calendar = Calendar.getInstance();
			calendar.set(Calendar.HOUR_OF_DAY, 0);
			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.SECOND, 0);
			calendar.set(Calendar.MILLISECOND, 0);
			this.lastDate = calendar.getTime();	//Initialize to start of day
			this.params = new SearchParameters();
			this.params.setText(query);
			this.params.setMinUploadDate(this.lastDate);
		}
		
		public void doQuery(List<FlickrResult> existingResults) {
			try {
				PhotoList result = photosInterface.search(params, 500, 0);
				for (Object obj : result) {
					Photo photo = (Photo)obj;
					boolean dup = false;
					for (FlickrResult existingResult : existingResults) {
						if (existingResult.getPhoto().getId() == photo.getId()) {
							dup = true;
							break;
						}
					}
					if (!dup) {
						Photo photoInfo = photosInterface.getInfo(photo.getId(), photo.getSecret());
						existingResults.add(new FlickrResult(photoInfo, params.getText()));
						if (photoInfo.getDatePosted().after(lastDate)) lastDate = photoInfo.getDatePosted();
					}
				}
				this.params.setMinUploadDate(new Date(lastDate.getTime() + 1000)); //One second after last photo
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		public SearchParameters getParams() {
			return this.params;
		}
		public void setParams(SearchParameters params) {
			this.params = params;
		}
	}
	
	public class FlickrResult {
		private Photo photo;
		private String query;
		
		public FlickrResult(Photo photo, String query) {
			this.photo = photo;
			this.query = query;
		}
		
		public Photo getPhoto() {
			return photo;
		}
		public String getQuery() {
			return query;
		}
		
		
	}

}
