package com.jasonrush.models.queries;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class QueryGroup extends Queryable {
	
	private Queryable[] queries;
	private String operator;
	
	public QueryGroup(Queryable[] queries, String operator) {
		this.queries = queries;
		this.operator = operator;
	}
	
	public QueryGroup(String[] queries, String operator) {
		Queryable[] queriables = new Queryable[queries.length];
		for (int i=0; i< queries.length; i++) {
			queriables[i] = new Query(queries[i]);
		}
		this.queries = queriables;
		this.operator = operator;
	}
	
	public void setQueries(Queryable[] queries) {
		this.queries = queries;
	}

	public Queryable[] getQueries() {
		return queries;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}

	public String getOperator() {
		return operator;
	}
	
	public String toString() {
		return toString(IMPLICIT_AND);
	}
	
	public String toString(String type) {
		if (type == GOOGLE_URL) {
			return toGoogleURLString();
		} else {
			StringBuilder sb = new StringBuilder();
			Queryable query;
			for (int i=0; i<queries.length; i++) {
				query = queries[i];
				if (query instanceof Query) {
					sb.append(query.toString());
				} else {
					//It's a query group
					sb.append("(" + query.toString(type) + ")");
				}
				if (i < queries.length-1) {
					sb.append(" ");
					if (type == EXPLICIT_AND || operator == OR) {
						sb.append(operator + " ");
					}
				}
			}
			return sb.toString();
		}
	}
	
	private String toGoogleURLString() {
		StringBuffer sb = new StringBuffer();
		try {
			sb.append("http://news.google.com/news?pz=1&cf=all&ned=us&hl=en&as_scoring=n&as_maxm=11");
			sb.append("&q=" + URLEncoder.encode(toString(IMPLICIT_AND), "UTF-8"));
			sb.append("&as_qdr=d&as_drrb=q&as_mind=19&as_minm=10&cf=all&as_maxd=18&output=rss");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return sb.toString();
	}
}
