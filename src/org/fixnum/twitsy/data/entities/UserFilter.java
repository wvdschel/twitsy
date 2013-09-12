package org.fixnum.twitsy.data.entities;

import javax.microedition.rms.RecordFilter;

import org.fixnum.twitsy.util.BinaryParser;

public class UserFilter implements RecordFilter {
	private String username, realName, id;

	public void setUsername(String username) {
		this.username = username;
	}

	public void setRealName(String realName) {
		this.realName = realName;
	}

	public void setId(String id) {
		this.id = id;
	}

	public boolean matches(byte[] data) {
		boolean result = true;
		String[] fields = BinaryParser.readStrings(data, 0, 3);
		if(id != null)
			result = result && (fields[0].equals(id));
		if(username != null)
			result = result && (fields[1].equals(username));
		if(realName != null)
			result = result && (fields[2].equals(realName));
		return result;
	}
}
