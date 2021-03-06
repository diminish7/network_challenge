package com.jasonrush;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.jasonrush.models.NetworkChallengeFeedResultSaver;
import com.jasonrush.models.NetworkChallengeFlickrResultSaver;
import com.jasonrush.models.NetworkChallengeTwitterResultSaver;
import com.jasonrush.models.queries.Query;
import com.jasonrush.models.queries.QueryGroup;
import com.jasonrush.models.queries.Queryable;
import com.jasonrush.spiders.FlickrSpider;
import com.jasonrush.spiders.GoogleNewsSpider;
import com.jasonrush.spiders.Spider;
import com.jasonrush.spiders.TwitterSpider;
import com.jasonrush.spiders.TwitterUserSpider;

/**
 * @author jasonrush
 *
 */
public class NetworkChallenge {
	private static List<Spider> spiders = new ArrayList<Spider>();
	
	private static final Queryable[] queries;
	//Build out the queries
	static {
		queries = new Queryable[3];
		// (network AND challenge) OR (DARPA AND (van OR truck OR balloon))
		QueryGroup networkAndChallenge = new QueryGroup(new String[] { "network", "challenge" }, Queryable.AND);
		QueryGroup vanTruckOrBalloon = new QueryGroup(new String[] { "van", "truck", "balloon" }, Queryable.OR);
		QueryGroup darpaAndVanTruckOrBalloon = new QueryGroup(new Queryable[] { new Query("DARPA"), vanTruckOrBalloon }, Queryable.AND);
		queries[0] = new QueryGroup(new Queryable[] { networkAndChallenge, darpaAndVanTruckOrBalloon }, Queryable.OR);
		// (red OR orange OR purple OR burgundy OR magenta OR maroon OR scarlet) AND balloon
		QueryGroup balloonColors = new QueryGroup(new String[] { "red", "orange", "purple", "burgundy", "magenta", "maroon", "scarlet" }, Queryable.AND);
		queries[1] = new QueryGroup(new Queryable[] { balloonColors, new Query("balloon") }, Queryable.AND);
		// (weather OR sounding OR tether OR moor OR large OR giant OR big) AND balloon
		QueryGroup balloonDescriptors = new QueryGroup(new String[] { "weather", "sounding", "tether", "moor", "large", "giant", "big" }, Queryable.OR);
		queries[2] = new QueryGroup(new Queryable[] { balloonDescriptors, new Query("balloon") }, Queryable.AND);
	}
	private static final String[] twitterUsers = new String[] {
		"THEREDBALLOONS",
		"findredballoons",
		"darpaforcharity",
		"Cash4RedBalloon",
		"darpaballoon",
		"10loons",
		"dwlz",
		"DARPA_News",
		"10Balloons"
	};
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		//Initialize
		if (!initDriver()) return;
		if (!initSpiders()) return;
		while (true) {
			for (Spider spider : spiders) {
				System.out.println("#### " + spider.getClass().toString() + ": Processing next batch ####");
				spider.processNextBatch();
			}
		}
	}
	
	private static boolean initDriver() {
		//Set up the JDBC driver
		try {
			Class.forName("com.mysql.jdbc.Driver");
			return true;
		} catch (ClassNotFoundException e) {
			System.out.println("Uh oh. Can't find the MySQL JDBC driver...");
			e.printStackTrace();
			return false;
		}
	}
	
	private static boolean initSpiders() {
		//Initialize each spider
		if (!initTwitterSpider()) return false;
		if (!initTwitterUserSpider()) return false;
		if (!initFlickrSpider()) return false;
		if (!initFeedSpiders()) return false;
		return true;
	}
	
	private static boolean initTwitterSpider() {
		try {
			spiders.add(new TwitterSpider(queries, new NetworkChallengeTwitterResultSaver()));
			return true;
		} catch (SQLException e) {
			System.out.println("Uh oh. Can't create a connection to the MySQL database...");
			e.printStackTrace();
			return false;
		}
	}
	
	private static boolean initTwitterUserSpider() {
		try {
			spiders.add(new TwitterUserSpider(twitterUsers, new NetworkChallengeTwitterResultSaver()));
			return true;
		} catch (SQLException e) {
			System.out.println("Uh oh. Can't create a connection to the MySQL database...");
			e.printStackTrace();
			return false;
		}
	}
	
	private static boolean initFlickrSpider() {
		try {
			spiders.add(new FlickrSpider(queries, new NetworkChallengeFlickrResultSaver()));
			return true;
		} catch (SQLException e) {
			System.out.println("Uh oh. Can't create a connection to the MySQL database...");
			e.printStackTrace();
			return false;
		}
	}
	
	private static boolean initFeedSpiders() {
		for (Queryable query : queries) {
			try {
				spiders.add(new GoogleNewsSpider("Google News", query, new NetworkChallengeFeedResultSaver()));
				return true;
			} catch (SQLException e) {
				System.out.println("Uh oh. Can't create a connection to the MySQL database...");
				e.printStackTrace();
				return false;
			}
		}
		return true;	//No feeds to crawl...
	}

}
