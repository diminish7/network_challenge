package com.jasonrush.spiders;

import com.jasonrush.models.FeedResultSaver;
import com.jasonrush.models.queries.Queryable;

public class GoogleNewsSpider extends FeedSpider {
	
	public GoogleNewsSpider(String source, Queryable query, FeedResultSaver resultSaver) {
		super(source, query.toString(Queryable.GOOGLE_URL), null, resultSaver);
		this.queryFromUrl = query.toString(Queryable.IMPLICIT_AND);
	}
	
}
