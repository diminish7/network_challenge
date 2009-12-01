package com.jasonrush.spiders;

import twitter4j.Query;

import com.jasonrush.models.TwitterResultSaver;

public class TwitterUserSpider extends TwitterSpider {

	public TwitterUserSpider(String[] users, TwitterResultSaver resultSaver) {
		super(resultSaver);
		this.queries = new QueryTracker[users.length];
		String userQuery;
		for (int i=0; i<users.length; i++) {
			String user = users[i];
			userQuery = "from:" + user + " OR to:" + user + " OR @" + user + "";
			this.queries[i] = new QueryTracker(new Query(userQuery));
		}
	}
}
