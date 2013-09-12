package org.fixnum.twitsy.data;

import java.io.IOException;
import java.io.InputStream;

import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;

import org.fixnum.twitsy.StatusKeeper;
//import org.fixnum.twitsy.Twitsy;
import org.fixnum.twitsy.data.entities.Account;
import org.fixnum.twitsy.util.Base64Coder;

public class TweetUpdateRunnable implements Runnable {
	
	public void run() {
		Account[] userInfo = AccountStorage.getInstance().getAccounts();
		for(int i = 0; i < userInfo.length; i++)
			updateUser(userInfo[i]);
		TweetStorage.getInstance().finishedUpdating();
		
		for(int i = 0; i < userInfo.length; i++)
			informMe(userInfo[i].getUsername());
	}
	
	private void processUrl(String url, boolean checkDoubles, Account acc) {
		HttpConnection c;
		try {
			c = (HttpConnection)Connector.open(url);
			c.setRequestProperty("Authorization", "Basic " + Base64Coder.encodeString(acc.getUsername()+":"+acc.getPassword()));
			//c.setRequestProperty("Accept-Encoding", "gzip");
			// Execute the request
			int rc = c.getResponseCode();
            // Read the data
            byte[] data = readConnection(c);
            if (rc != HttpConnection.HTTP_OK) {
                throw new IOException("HTTP response code: " + rc);
            }
            // Decompress it
            //StatusKeeper.setStatus("Decompressing tweets for " + username);
            //data = GZIP.inflate(data);
            // Parse the XML
            StatusKeeper.setStatus("Parsing tweets for " + acc.getUsername());
            TweetParser.parseTweets(new String(data), acc, checkDoubles);
            StatusKeeper.setStatus("Updated " + acc.getUsername());
		} catch (Exception e) {
			e.printStackTrace();
			StatusKeeper.setStatus("Update for " + acc.getUsername() + " failed.", StatusKeeper.ERROR);
		}
	}
	
	private void updateUser(Account acc) {
		String since = acc.getLastTweet();
		StatusKeeper.setStatus("Fetching replies: " + acc.getUsername());
		processUrl("http://twitter.com/statuses/mentions.xml?since_id=" + since, true, acc);
		StatusKeeper.setStatus("Fetching tweets: " + acc.getUsername());
		processUrl("http://twitter.com/statuses/friends_timeline.xml?since_id=" + since, true, acc);
	}
	
	private byte[] readConnection(HttpConnection c) throws IOException {
		InputStream is = c.openDataInputStream();;
		try {
			byte[] data;
		    // Get the length and process the data
		    int len = (int)c.getLength();
		    if (len > 0) {
		        int actual = 0;
		        int bytesread = 0 ;
		        data = new byte[len];
		        while ((bytesread != len) && (actual != -1)) {
		           actual = is.read(data, bytesread, len - bytesread);
		           bytesread += actual;
		        }
		    } else {
		        int bytesread = 0, lastread;
		        data = new byte[1024];
		        while ((lastread = is.read(data, bytesread, Math.min(1024, data.length - bytesread))) != -1) {
		        	bytesread += lastread;
		        	if(data.length <= bytesread) {
		        		byte[] newData = new byte[data.length * 2];
		        		for(int i = 0; i < data.length; i++)
		        			newData[i] = data[i];
		        		data = newData;
		        	}
		        }
		    }
		    return data;
		} finally { is.close(); }
	}
	
	private void informMe(String username) {/*
		String version = Twitsy.getVersionNumber(), newVersion;
		try {
			HttpConnection c = (HttpConnection)Connector.open("http://fixnum.org/twitsyapp/usedby/"+username);
			c.getResponseCode();
			newVersion = new String(readConnection(c)).trim();
			if(!version.equals(newVersion))
				StatusKeeper.setStatus("A new version of Twitsy is available.\n"+
						"Please visit fixnum.org/twitsy to update to " + newVersion + ".\n"+
						"(You are running " + version + ")", StatusKeeper.UPDATE);
		} catch(IOException e) {
			System.out.println("Can't update usage stats");
		}*/
	}
}
