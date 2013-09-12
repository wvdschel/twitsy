package org.fixnum.twitsy.util;

public class BinaryParser {
	public static String readString(byte[] byteArray, int offset) {	
		short len = parseUnsignedShort(byteArray[offset], byteArray[offset+1]);
		return new String(byteArray, offset+2, len); // Signed to unsigned conversion
	}
	
	public static String[] readStrings(byte[] byteArray, int offset, int count) {
		int currentPos = offset;
		String[] strs = new String[count];
		for(int i = 0; i < count; i++) {
			strs[i] = readString(byteArray, currentPos);
			currentPos += strs[i].getBytes().length + 2; // two bytes = length
		}
		return strs;
	}
	
	public static void printArray(byte[] byteArray) {
		StringBuffer buff = new StringBuffer();
		for(int i = 0; i < byteArray.length; i++) {
			String b = "" + byteArray[i];
			while(b.length() < 5) b = b + " ";
			buff.append(b);
			if(i % 10 == 9)
				buff.append('\n');
		}
		System.out.println(buff.toString());
	}
	
	private static short parseUnsignedShort(byte b1, byte b2) {
		short sh_p = (short)(b1 << 8);
		short n = 1;
		for(int i = 0; i < 8; i++) {
			byte mask = (byte)(1 << i);
			if((b2 & mask) != 0)
				sh_p += n;
			n *= 2;
		}
		sh_p = (short)(sh_p - Short.MIN_VALUE);
		return sh_p;
	}
}
