package com.jasonrush.spiders;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;

import uk.org.catnip.eddie.Category;
import uk.org.catnip.eddie.Entry;
import uk.org.catnip.eddie.FeedData;
import uk.org.catnip.eddie.parser.Parser;

import com.jasonrush.models.FeedResultSaver;

public class FeedSpider implements Spider {
	private String source;
	private URL feedUrl;
	private String[] queries;
	private String queryFromUrl;
	private FeedResultSaver resultSaver;
	private Parser parser;
	private Date earliest;
	
	public FeedSpider(String source, String feedUrl, String[] queries, FeedResultSaver resultSaver) {
		try {
			this.source = source;
			this.feedUrl = new URL(feedUrl);
			this.queries = queries;
			this.resultSaver = resultSaver;
			this.parser = new Parser();
			if (queries == null) {
				String query = this.feedUrl.getQuery();
				int startIndex = query.indexOf("q=");
				int endIndex = query.indexOf("&", startIndex);
				if (startIndex == -1 || endIndex == -1) {
					queryFromUrl = null;
				} else {
					queryFromUrl = query.substring(startIndex+2, endIndex);
				}
			} else {
				queryFromUrl = null;
			}
			Calendar calendar = Calendar.getInstance();
			calendar.set(Calendar.HOUR_OF_DAY, 0);
			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.SECOND, 0);
			calendar.set(Calendar.MILLISECOND, 0);
			this.earliest = calendar.getTime();	//Initialize to start of day
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	public void processNextBatch() {
		try {
			HttpURLConnection conn = (HttpURLConnection)feedUrl.openConnection();
			conn.setRequestProperty("User-Agent", "NewsFeedSpider");
			FeedData feedData = parser.parse(new InputStreamReader(conn.getInputStream()));
			Iterator entries = feedData.entries();
			Date latestInBatch = null;
			while (entries.hasNext()) {
				Entry entry = (Entry)entries.next();
				Date date = entry.getCreated();
				if (date == null) date = entry.getModified();
				if (date != null && !date.after(this.earliest)) continue;
				if (latestInBatch == null || (date != null && date.after(latestInBatch))) latestInBatch = date;
				searchEntry(entry);
			}
			if (latestInBatch != null) this.earliest = latestInBatch;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	//Return the first query that matches
	private void searchEntry(Entry entry) {
		StringBuilder sb = new StringBuilder();
		sb.append(entry.get("description"));
		sb.append(" ");
		sb.append(entry.get("title"));
		sb.append(" ");
		sb.append(entry.get("summary"));
		Iterator categories = entry.categories();
		while (categories.hasNext()) {
			Category category = (Category)categories.next();
			sb.append(" ");
			sb.append(category.getLabel());
		}
		String fullText = sb.toString().toLowerCase();
		if (queries == null || queries.length == 0) {
			//This must be a feed from an existing search (like Google News), so no additional query necessary
			resultSaver.saveResult(source, entry, queryFromUrl);
		} else {
			//Need to run this against a query or queries
			for (String query : queries) {
				if (searchText(fullText, query)) {
					//Found a match
					resultSaver.saveResult(source, entry, query);
					break;
				}
			}
		}
	}
	
	//TODO: This should be a more complicated search than just string matching
	// 		Should look at word stems, and should look at word proximity
	private boolean searchText(String text, String query) {
		String[] words = query.split(" ");
		boolean found = true;
		for (String word : words) {
			if (text.indexOf(word.toLowerCase()) == -1) {
				found = false;
				break;
			}
		}
		return found;
	}
}
