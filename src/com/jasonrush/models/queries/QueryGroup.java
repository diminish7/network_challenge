package com.jasonrush.models.queries;

public class QueryGroup extends Queryable {
	
	private Queryable[] queries;
	private String operator;
	
	public QueryGroup(Queryable[] queries, String operator) {
		this.queries = queries;
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
		//TODO: I think this is the base url followed by a URL-escaped version of toString(IMPLICIT_AND)
		return null;
	}
}
