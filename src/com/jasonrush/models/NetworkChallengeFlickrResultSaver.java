package com.jasonrush.models;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Date;

import com.aetrion.flickr.photos.GeoData;
import com.aetrion.flickr.photos.Photo;
import com.aetrion.flickr.tags.Tag;
import com.jasonrush.Constants;

public class NetworkChallengeFlickrResultSaver implements FlickrResultSaver {

	private static Connection conn = null;
	private static PreparedStatement statement = null;
	
	public NetworkChallengeFlickrResultSaver() throws SQLException {
		String mysqlUrl = "jdbc:mysql://localhost:3306/network_challenge";
		String user = "root";
		String password = "";
		conn = DriverManager.getConnection(mysqlUrl, user, password);
		statement = conn.prepareStatement(Constants.INSERT_STATEMENT);
	}
	
	@Override
	public void saveResult(Photo photo, String searchPhrase) {
		try {
			//Source
			statement.setString(1, "Flickr");
			//URL
			statement.setString(2, photo.getUrl());
			//Search Phrase
			statement.setString(3, searchPhrase);
			//Text: Title, Tags and Description
			String text = "Title: " + photo.getTitle() + " -- Tags: ";
			for (Object obj : photo.getTags()) {
				Tag tag = (Tag)obj;
				text += tag.getValue() + " ";
			}
			text += "-- Description: " + photo.getDescription();
			statement.setString(4, text);
			//Time Stamp
			statement.setTimestamp(5, new Timestamp(photo.getDatePosted().getTime()));
			//Location information
			GeoData geoData = photo.getGeoData();
			String geoString = null;
			if (geoData != null) geoString = "Latitude: " + geoData.getLatitude() + ", Longitude: " + geoData.getLongitude();
			statement.setString(6, geoString);
			//Unique ID
			statement.setString(7, photo.getId());
			statement.executeUpdate();
			System.out.println("Saved photo " + photo.getId());
		} catch (SQLException e) {
			System.out.println("Uh oh. Unable to save the photo: " + photo.getId());
		}
	}

	@Override
	public Date getLastPhotoDate(String searchPhrase) {
		try {
			Statement query = conn.createStatement();
			ResultSet result = query.executeQuery("SELECT timestamp FROM search_results WHERE source = 'Flickr' AND search_phrase = '" + searchPhrase + "' ORDER BY timestamp DESC LIMIT 1");
			if (result.first())
				return (Date)result.getTimestamp("timestamp");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
}
