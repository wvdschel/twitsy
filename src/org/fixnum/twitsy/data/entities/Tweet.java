package org.fixnum.twitsy.data.entities;

import org.fixnum.twitsy.data.UserStorage;
import org.fixnum.twitsy.util.BinaryParser;
import org.fixnum.twitsy.util.ByteBuffer;

public class Tweet {
	private byte isReply = 0;
	private String account, user, date, id, message;
	private int storeId = -1;
	
	public Tweet() {}
	
	public Tweet(byte[] data) {
		isReply = data[0];
		String[] fields = BinaryParser.readStrings(data, 1, 5);
		id = fields[0];
		user = fields[1];
		date = fields[2];
		account = fields[3];
		message = fields[4];
	}
	
	public int getStoreId() {
		return storeId;
	}

	public void setStoreId(int storeId) {
		this.storeId = storeId;
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public boolean isReply() {
		return isReply != 0;
	}
	
	public void setReply(boolean isReply) {
		this.isReply = (byte)(isReply ? 1 : 0);
	}
	
	public String getAccount() {
		return account;
	}
	
	public void setAccount(String account) {
		this.account = account;
	}
	
	public User getUser() {
		return UserStorage.getInstance().getUser(Integer.parseInt(user));
	}
	
	public void setUser(User user) {
		this.user = ""+user.getStoreId();
	}
	
	public String getDate() {
		return date;
	}
	
	public void setDate(String date) {
		this.date = date;
	}
	
	public byte[] toBytes() {
		ByteBuffer buff = new ByteBuffer(50);
		buff.add(isReply);
		buff.add(id);
		buff.add(user);
		buff.add(date);
		buff.add(account);
		buff.add(message);
		return buff.getBytes();
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
