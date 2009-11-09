package com.jasonrush.spiders;

import java.io.InputStreamReader;
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
	private URL feedUrl;
	private String[] queries;
	private FeedResultSaver resultSaver;
	private Parser parser;
	private Date earliest;
	
	public FeedSpider(String feedUrl, String[] queries, FeedResultSaver resultSaver) {
		try {
			this.feedUrl = new URL(feedUrl);
			this.queries = queries;
			this.resultSaver = resultSaver;
			this.parser = new Parser();
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
			FeedData feedData = parser.parse(new InputStreamReader(feedUrl.openConnection().getInputStream()));
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
		for (String query : queries) {
			if (searchText(fullText, query)) {
				//Found a match
				resultSaver.saveResult(entry, query);
				break;
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
