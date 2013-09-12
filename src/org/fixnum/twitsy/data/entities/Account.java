package org.fixnum.twitsy.data.entities;

import org.fixnum.twitsy.util.BinaryParser;
import org.fixnum.twitsy.util.ByteBuffer;

public class Account {
	private String username, password, lastTweet;
	private int storeId = -1;
	
	public int getStoreId() {
		return storeId;
	}

	public void setStoreId(int storeId) {
		this.storeId = storeId;
	}

	public Account(byte[] data) {
		String[] fields = BinaryParser.readStrings(data, 0, 3);
		username = fields[0];
		password = fields[1];
		lastTweet = fields[2];
	}
	
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getLastTweet() {
		return lastTweet;
	}

	public void setLastTweet(String lastTweet) {
		this.lastTweet = lastTweet;
	}

	public Account() {}
	
	public byte[] toBytes() {
		ByteBuffer buff = new ByteBuffer(50);
		buff.add(username);
		buff.add(password);
		buff.add(lastTweet);
		return buff.getBytes();
	}
}
