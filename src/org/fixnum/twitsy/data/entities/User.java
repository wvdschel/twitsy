package org.fixnum.twitsy.data.entities;

import javax.microedition.lcdui.Image;

import org.fixnum.twitsy.data.UserStorage;
import org.fixnum.twitsy.util.BinaryParser;
import org.fixnum.twitsy.util.ByteBuffer;
import org.fixnum.twitsy.util.HttpFetcher;

public class User {
	private String avatarUrl, username, realName, id, avatarId="";
	private int storeId = -1;
	
	public User() {}
	
	public User(byte[] data) {
		String[] fields = BinaryParser.readStrings(data, 0, 5);
		id = fields[0];
		username = fields[1];
		realName = fields[2];
		avatarId = fields[3];
		avatarUrl = fields[4];
	}
	
	public byte[] toBytes() {
		ByteBuffer buff = new ByteBuffer(50);
		buff.add(id);
		buff.add(username);
		buff.add(realName);
		buff.add(avatarId);
		buff.add(avatarUrl);
		return buff.getBytes();
	}
	
	public int getStoreId() {
		return storeId;
	}

	public void setStoreId(int storeId) {
		this.storeId = storeId;
	}

	public String getUsername() {
		return username;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
	
	public String getRealName() {
		return realName;
	}
	
	public void setRealName(String realName) {
		this.realName = realName;
	}
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public void setAvatar(String url) {
		if(avatarUrl == null || !avatarUrl.equals(url)) {
			byte[] imgData = HttpFetcher.fetch(url);
			if(imgData != null) {
				if(avatarUrl == null || avatarId.equals("")) {
					int newId = UserStorage.getInstance().addAvatar(imgData);
					if(newId != -1) {
						avatarUrl = url;
						avatarId  = "" + newId;
					}
				} else {
					avatarUrl = url;
					UserStorage.getInstance().updateAvatar(Integer.parseInt(avatarId), imgData);
				}
			}
		}
	}
	
	public Image getAvatar() {
		return UserStorage.getInstance().getAvatar(Integer.parseInt(avatarId));
	}
}
