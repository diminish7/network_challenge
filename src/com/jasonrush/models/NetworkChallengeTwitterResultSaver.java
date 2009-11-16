package com.jasonrush.models;

import java.sql.Connection;
import java.sql.Timestamp;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import twitter4j.Tweet;

import com.jasonrush.Constants;

public class NetworkChallengeTwitterResultSaver implements TwitterResultSaver {

	private static Connection conn = null;
	private static PreparedStatement statement = null;
	
	public NetworkChallengeTwitterResultSaver() throws SQLException {
		String mysqlUrl = "jdbc:mysql://localhost:3306/network_challenge";
		String user = "root";
		String password = "";
		conn = DriverManager.getConnection(mysqlUrl, user, password);
		statement = conn.prepareStatement(Constants.INSERT_STATEMENT);
	}
	
	@Override
	public void saveResult(Tweet tweet, String searchPhrase) {
		try {
			//Source
			statement.setString(1, "Twitter");
			//URL
			statement.setString(2, "http://twitter.com/" + tweet.getFromUser() + "/status/" + tweet.getId());
			//Search Phrase
			statement.setString(3, searchPhrase);
			//Surrounding Text
			statement.setString(4, tweet.getText());
			//Time Stamp
			statement.setTimestamp(5, new Timestamp(tweet.getCreatedAt().getTime()));
			//Location information
			statement.setString(6, null);	//TODO: Can we get location info from the user or tweet?
			//Unique ID
			statement.setString(7, "" + tweet.getId());
			statement.executeUpdate();
			System.out.println("Saved tweet " + tweet.getId());
		} catch (SQLException e) {
			System.out.println("Uh oh. Unable to save the tweet: " + tweet.getId() + ": " + tweet.getText());
		}
	}
}
