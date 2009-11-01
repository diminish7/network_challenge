package com.jasonrush.spiders;

public interface Spider {
	/**
	 * Process the next batch of data
	 */
	public void processNextBatch();
}
