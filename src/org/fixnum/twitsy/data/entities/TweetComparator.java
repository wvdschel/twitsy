package org.fixnum.twitsy.data.entities;

import javax.microedition.rms.RecordComparator;

import org.fixnum.twitsy.util.BinaryParser;

public class TweetComparator implements RecordComparator {

	public int compare(byte[] arg0, byte[] arg1) {
		String val1 = BinaryParser.readString(arg0, 1), val2 = BinaryParser.readString(arg1, 1);
		long diff = Long.parseLong(val1) - Long.parseLong(val2);
		if(diff == 0)
			return RecordComparator.EQUIVALENT;
		else if(diff < 0)
			return RecordComparator.PRECEDES;
		else
			return RecordComparator.FOLLOWS;
	}

}
