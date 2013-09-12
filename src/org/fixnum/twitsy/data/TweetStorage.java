package org.fixnum.twitsy.data;

import java.util.Vector;

import javax.microedition.rms.RecordEnumeration;
import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;

import org.fixnum.twitsy.data.entities.Tweet;
import org.fixnum.twitsy.data.entities.TweetComparator;
import org.fixnum.twitsy.data.entities.TweetFilter;

public class TweetStorage {
	public static TweetStorage instance;
	
	public static TweetStorage getInstance() {
		try {
			if(instance == null)
				instance = new TweetStorage();
			return instance;
		} catch(RecordStoreException e) {
			return null;
		}
	}
	
	public static void reset() {
		try {
			getInstance().close();
			RecordStore.deleteRecordStore("TwitsyTweets");
			instance = new TweetStorage();
		} catch (RecordStoreException e) {
			e.printStackTrace();
		}
	}
	
	private RecordStore tweetStore;
	private boolean updating = false;
	private Vector listeners = new Vector();
	
	private TweetStorage() throws RecordStoreException {
		tweetStore = RecordStore.openRecordStore("TwitsyTweets",true);
	}
	
	private void close() throws RecordStoreException {
		tweetStore.closeRecordStore();
	}
	
	public void addTweetListener(TweetListener list) {
		listeners.addElement(list);
	}
	
	public Tweet getTweet(int storeId) {
		try {
			Tweet tw = new Tweet(tweetStore.getRecord(storeId));
			tw.setStoreId(storeId);
			return tw;
		} catch (RecordStoreException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public void saveTweet(Tweet tw) {
		try {
			byte[] data = tw.toBytes();
			if(tw.getStoreId() == -1)
				tw.setStoreId(tweetStore.addRecord(data, 0, data.length));
			else
				tweetStore.setRecord(tw.getStoreId(), data, 0, data.length);
		} catch (RecordStoreException e) {
			e.printStackTrace();
		}
		fireTweet(tw);
	}
	
	private void fireTweet(Tweet tw) {
		for(int i = 0; i < listeners.size(); i++)
			((TweetListener)listeners.elementAt(i)).tweetAdded(tw);
	}
	
	public Tweet[] getTweets() {
		return getTweets(null);
	}

	public void updateNow() {
		if(!updating) {
			updating = true;
			new Thread(new TweetUpdateRunnable()).start();
		}
	}
	
	public void finishedUpdating() {
		updating = false;
	}
	
	public Tweet[] getTweets(TweetFilter filter) {
		try {
			RecordEnumeration enum = tweetStore.enumerateRecords(filter, new TweetComparator(), false);
			Vector tweetList = new Vector();
			while(enum.hasNextElement()) {
				int id = enum.nextRecordId();
				Tweet tw = new Tweet(tweetStore.getRecord(id));
				tw.setStoreId(id);
				tweetList.addElement(tw);
			}
			Tweet[] res = new Tweet[tweetList.size()];
			tweetList.copyInto(res);
			return res;
		} catch (RecordStoreException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public void deleteTweet(int storeId) {
		try {
			tweetStore.deleteRecord(storeId);
		} catch (RecordStoreException e) {
			e.printStackTrace();
		}
	}
}
