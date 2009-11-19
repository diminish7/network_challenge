package com.jasonrush.models.queries;

public class Query extends Queryable {
	
	private String query;
	
	public Query(String query) {
		this.query = query;
	}
		
	public void setQuery(String query) {
		this.query = query;
	}

	public String getQuery() {
		return query;
	}
	
	public String toString() {
		return query;
	}
	
	public String toString(String type) {
		//At this level, there is no difference between types
		return toString();
	}
}
