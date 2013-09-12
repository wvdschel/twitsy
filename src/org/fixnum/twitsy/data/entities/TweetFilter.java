package org.fixnum.twitsy.data.entities;

import javax.microedition.rms.RecordFilter;

import org.fixnum.twitsy.util.BinaryParser;

public class TweetFilter implements RecordFilter {
	private boolean isReply;
	private String account, user, date, id;

	public boolean matches(byte[] data) {
		boolean result = true;
		byte reply = data[0];
		String[] fields = BinaryParser.readStrings(data, 1, 4);
		if(isReply)
			result = result && (0 != reply);
		if(account != null)
			result = result && (fields[3].equals(account));
		if(user != null)
			result = result && (fields[1].equals(user));
		if(date != null)
			result = result && (fields[2].equals(date));
		if(id != null)
			result = result && (fields[0].equals(id));
		return result;
	}

	public void setIsReply(boolean isReply) {
		this.isReply = isReply;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	
}
