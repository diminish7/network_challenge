package com.jasonrush;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.jasonrush.models.NetworkChallengeTwitterResultSaver;
import com.jasonrush.spiders.Spider;
import com.jasonrush.spiders.TwitterSpider;

/**
 * @author jasonrush
 *
 */
public class NetworkChallenge {
	private static final String[] twitterQueryStrings = {
		"(network challenge) OR (DARPA (van OR truck OR balloon OR sphere OR ball OR orb OR globe))",
		"(red OR orange OR purple OR burgundy OR magenta OR maroon OR scarlet OR weather OR sounding) (balloon OR sphere OR ball OR orb OR globe)",
		"(tether OR tethered OR moor OR moored OR large OR giant OR big) (balloon OR sphere OR ball OR orb OR globe)"
		
	};
	private static List<Spider> spiders = new ArrayList<Spider>();
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		//Initialize
		if (!initDriver()) return;
		if (!initSpiders()) return;
		for (Spider spider : spiders) {
			spider.processNextBatch();
		}	
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

}
