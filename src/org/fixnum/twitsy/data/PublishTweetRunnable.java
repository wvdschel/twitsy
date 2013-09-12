package org.fixnum.twitsy.data;

import java.io.IOException;
import java.io.OutputStream;

import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;

import org.fixnum.twitsy.StatusKeeper;
import org.fixnum.twitsy.data.entities.Account;
import org.fixnum.twitsy.util.Base64Coder;

public class PublishTweetRunnable implements Runnable {
	private Account acc;
	private String msg, inReplyTo;
	
	public PublishTweetRunnable(Account acc, String msg, String inReplyTo) {
		this.acc = acc;
		this.msg = msg;
		this.inReplyTo = inReplyTo;
	}
	
	public void run() {
		StatusKeeper.setStatus("Tweeting for " + acc.getUsername());
		HttpConnection c;
		try {
			String url = "http://twitter.com/statuses/update.xml";
			if(inReplyTo != null)
				url = url + "?in_reply_to_status_id="+ inReplyTo;
			
			c = (HttpConnection)Connector.open(url);
			c.setRequestProperty("Authorization", "Basic " + Base64Coder.encodeString(acc.getUsername()+":"+acc.getPassword()));
			//c.setRequestProperty("X-Twitter-Client", "Twitsy");
			//c.setRequestProperty("X-Twitter-Client-URL", "http://twitter.com/twitsyapp");
			c.setRequestMethod("POST");
			OutputStream o = c.openDataOutputStream();
			o.write("source=Twitsy&status=".getBytes());
			o.write(msg.getBytes());
			//c.setRequestProperty("Accept-Encoding", "gzip");
			// Execute the request
			int rc = c.getResponseCode();
            if (rc != HttpConnection.HTTP_OK) {
                throw new IOException("HTTP response code: " + rc);
            }
            StatusKeeper.setStatus("Tweeted for " + acc.getUsername());
            TweetStorage.getInstance().updateNow();
		} catch (Exception e) {
			e.printStackTrace();
			StatusKeeper.setStatus("Tweeting for " + acc.getUsername() + " failed.");
		}
	}
}
