package com.jasonrush.models.queries;

public abstract class Queryable {
	//Boolean Operators
	public static final String AND = "AND";
	public static final String OR = "OR";
	//Types for toString()
	public static final String IMPLICIT_AND = "IMPLICIT_AND";
	public static final String EXPLICIT_AND = "EXPLICIT_AND";
	public static final String GOOGLE_URL = "GOOGLE_URL";
	
	public abstract String toString(String type);
}
