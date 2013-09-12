package org.fixnum.twitsy.util;

public class ByteBuffer {
	private byte[] buff;
	private int byteCount = 0;
	
	public ByteBuffer(int size) {
		buff = new byte[size];
	}
	
	public void add(String str) {
		byte[] data = str.getBytes();
		short strLen = (short)(data.length + Short.MIN_VALUE);
		byte[] len = {(byte)(strLen >> 8), (byte)(strLen & 0x00FF)};
		add(len); // Write out length, unsigned
		add(data);
	}
	
	public void add(byte b) {
		byte[] barr = {b};
		add(barr);
	}
	
	public void add(byte[] data) {
		byteCount += data.length;
		if(byteCount > buff.length)
			resize();
		for(int i = 0; i < data.length; i++)
			buff[byteCount - data.length + i] = data[i]; 
	}
	
	public byte[] getBytes() {
		byte[] newBuff = new byte[byteCount];
		for(int i = 0; i < byteCount; i++)
			newBuff[i] = buff[i];
		return buff;
	}
	
	private void resize() {
		byte[] newBuff = new byte[byteCount * 2];
		for(int i = 0; i < buff.length; i++)
			newBuff[i] = buff[i];
		buff = newBuff;
	}
}
