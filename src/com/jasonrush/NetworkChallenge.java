package com.jasonrush;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.jasonrush.models.NetworkChallengeFlickrResultSaver;
import com.jasonrush.models.NetworkChallengeTwitterResultSaver;
import com.jasonrush.spiders.FlickrSpider;
import com.jasonrush.spiders.Spider;
import com.jasonrush.spiders.TwitterSpider;

/**
 * @author jasonrush
 *
 */
public class NetworkChallenge {
	private static final String[] twitterQueryStrings = {
		"(network challenge) OR (DARPA (van OR truck OR balloon))",
		"(red OR orange OR purple OR burgundy OR magenta OR maroon OR scarlet OR weather OR sounding) balloon",
		"(tether OR tethered OR moor OR moored OR large OR giant OR big) balloon"
	};
	private static final String[] flickrQueryStrings = {
		"(network AND challenge) OR (DARPA AND (van OR truck OR balloon))",
		"(red OR orange OR purple OR burgundy OR magenta OR maroon OR scarlet OR weather OR sounding) AND balloon",
		"(tether OR tethered OR moor OR moored OR large OR giant OR big) AND balloon"
	};
	private static List<Spider> spiders = new ArrayList<Spider>();
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		//Initialize
		if (!initDriver()) return;
		if (!initSpiders()) return;
		// while (true) {		//TODO: This should loop indefinitely in production
			for (Spider spider : spiders) {
				spider.processNextBatch();
			}
		//}
	}
	
	private static boolean initDriver() {
		//Set up the JDBC driver
		try {
			Class.forName("com.mysql.jdbc.Driver");
			return true;
		} catch (ClassNotFoundException e) {
			System.out.println("Uh oh. Can't find the MySQL JDBC driver...");
			return false;
		}
	}
	
	private static boolean initSpiders() {
		//Initialize each spider
		if (!initTwitterSpider()) return false;
		if (!initFlickrSpider()) return false;
		return true;
	}
	
	private static boolean initTwitterSpider() {
		try {
			spiders.add(new TwitterSpider(twitterQueryStrings, new NetworkChallengeTwitterResultSaver()));
			return true;
		} catch (SQLException e) {
			System.out.println("Uh oh. Can't create a connection to the MySQL database...");
			return false;
		}
	}
	
	private static boolean initFlickrSpider() {
		try {
			spiders.add(new FlickrSpider(flickrQueryStrings, new NetworkChallengeFlickrResultSaver()));
			return true;
		} catch (SQLException e) {
			System.out.println("Uh oh. Can't create a connection to the MySQL database...");
			return false;
		}
	}

}
