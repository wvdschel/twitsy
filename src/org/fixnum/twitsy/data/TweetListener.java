package org.fixnum.twitsy.data;

import org.fixnum.twitsy.data.entities.Tweet;

public interface TweetListener {
	public void tweetAdded(Tweet tw);
}
