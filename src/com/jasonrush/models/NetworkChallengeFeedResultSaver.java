package com.jasonrush.models;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

import uk.org.catnip.eddie.Entry;

import com.jasonrush.Constants;

public class NetworkChallengeFeedResultSaver implements FeedResultSaver {
	private static Connection conn = null;
	private static PreparedStatement statement = null;
	
	public NetworkChallengeFeedResultSaver() throws SQLException {
		String mysqlUrl = "jdbc:mysql://localhost:3306/network_challenge";
		String user = "root";
		String password = "";
		conn = DriverManager.getConnection(mysqlUrl, user, password);
		statement = conn.prepareStatement(Constants.INSERT_STATEMENT);
	}

	@Override
	public void saveResult(Entry entry, String searchPhrase) {
		try {
			//Source
			statement.setString(1, "Article: " + entry.get("title"));
			//URL
			statement.setString(2, entry.get("link"));
			//Search Phrase
			statement.setString(3, searchPhrase);
			//Text: Description
			statement.setString(4, entry.get("description"));
			//Time Stamp
			java.util.Date date = entry.getCreated();
			if (date == null) date = entry.getModified();
			if (date == null) {
				statement.setTimestamp(5, null);
			} else {
				statement.setTimestamp(5, new Timestamp(date.getTime()));
			}
			//Location information
			statement.setString(6, null);
			//Unique ID
			String guid = entry.get("guid");
			if (guid == null) guid = entry.get("link");
			statement.setString(7, guid);
			statement.executeUpdate();
			System.out.println("Saved feed entry " + guid);
		} catch (SQLException e) {
			String guid = entry.get("guid");
			if (guid == null) guid = entry.get("link");
			System.out.println("Uh oh. Unable to save the feed entry: " + guid);
		}
	}
}
