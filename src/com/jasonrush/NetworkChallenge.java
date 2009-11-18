package com.jasonrush;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.jasonrush.models.NetworkChallengeFeedResultSaver;
import com.jasonrush.models.NetworkChallengeFlickrResultSaver;
import com.jasonrush.models.NetworkChallengeTwitterResultSaver;
import com.jasonrush.spiders.FeedSpider;
import com.jasonrush.spiders.FlickrSpider;
import com.jasonrush.spiders.Spider;
import com.jasonrush.spiders.TwitterSpider;

/**
 * @author jasonrush
 *
 */
public class NetworkChallenge {
	//TODO: Work these into a query object that can be shared among all of them
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
	private static final String[] googleNewsFeeds = {
		"http://news.google.com/news?pz=1&cf=all&ned=us&hl=en&q=%28network+challenge%29+OR+%28DARPA+van+OR+truck+OR+balloon%29&as_qdr=d&as_drrb=q&cf=all&scoring=n&output=rss",
		"http://news.google.com/news?pz=1&cf=all&ned=us&hl=en&as_scoring=n&as_maxm=11&q=balloon+red+OR+orange+OR+purple+OR+burgundy+OR+magenta+OR+maroon+OR+scarlet+OR+weather+OR+sounding&as_qdr=d&as_drrb=q&as_mind=19&as_minm=10&cf=all&as_maxd=18&output=rss",
		"http://news.google.com/news?pz=1&cf=all&ned=us&hl=en&as_scoring=n&as_maxm=11&q=balloon+tether+OR+tethered+OR+moor+OR+moored+OR+large+OR+giant+OR+big&as_qdr=d&as_drrb=q&as_mind=19&as_minm=10&cf=all&as_maxd=18&output=rss"
	};
	private static List<Spider> spiders = new ArrayList<Spider>();
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		//TODO: Need to not start from scratch if this crashes and we start over
		//		Take an arg that will look in DB and see last id/timestamp and start from their?
		//Initialize
		if (!initDriver()) return;
		if (!initSpiders()) return;
		// while (true) {		//TODO: This should loop indefinitely in production
		for (int i=0; i<5; i++) {
			for (Spider spider : spiders) {
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
			return false;
		}
	}
	
	private static boolean initSpiders() {
		//Initialize each spider
		if (!initTwitterSpider()) return false;
		if (!initFlickrSpider()) return false;
		if (!initFeedSpiders()) return false;
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
	
	private static boolean initFeedSpiders() {
		for (String url : googleNewsFeeds) {
			try {
				spiders.add(new FeedSpider("Google News", url, null, new NetworkChallengeFeedResultSaver()));
				return true;
			} catch (SQLException e) {
				System.out.println("Uh oh. Can't create a connection to the MySQL database...");
				return false;
			}
		}
		return true;	//No feeds to crawl...
	}

}
