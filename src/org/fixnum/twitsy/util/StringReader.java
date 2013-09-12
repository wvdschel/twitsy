package org.fixnum.twitsy.util;

import java.io.IOException;

public class StringReader extends java.io.Reader {
	private String data;
	private int charsRead = 0, markPos = 0;
	
	public StringReader(String data) {
		this.data = data;
	}

	public void close() throws IOException {
	}

	public int read(char[] dest, int offset, int len) {
		int length = Math.min(data.length() - charsRead, len); 
		for(int i = 0; i < length; i++)
			dest[offset + i] = data.charAt(charsRead + i);
		charsRead += length;
		return length;
	}
	
	public void mark(int limit) {
		markPos = charsRead;
	}
	
	public void reset() {
		charsRead = markPos;
		markPos = 0;
	}
	
	public boolean markSupported() { return true; }
}
