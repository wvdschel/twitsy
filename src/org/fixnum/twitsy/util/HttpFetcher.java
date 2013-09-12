package org.fixnum.twitsy.util;

import java.io.IOException;
import java.io.InputStream;

import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;

public class HttpFetcher {
	public static byte[] fetch(String url) {
		try {
			HttpConnection c = (HttpConnection)Connector.open(url);
			// Execute the request
			int rc = c.getResponseCode();
            if (rc != HttpConnection.HTTP_OK) {
                throw new IOException("HTTP response code " + rc + " when fetching " + url); 
            }
            // Read the data
            return readConnection(c);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	private static byte[] readConnection(HttpConnection c) throws IOException {
		InputStream is = c.openDataInputStream();;
		try {
			byte[] data;
		    // Get the length and process the data
		    int len = (int)c.getLength();
		    if (len > 0) {
		        int actual = 0;
		        int bytesread = 0 ;
		        data = new byte[len];
		        while ((bytesread != len) && (actual != -1)) {
		           actual = is.read(data, bytesread, len - bytesread);
		           bytesread += actual;
		        }
		    } else {
		        int bytesread = 0, lastread;
		        data = new byte[1024];
		        while ((lastread = is.read(data, bytesread, Math.min(1024, data.length - bytesread))) != -1) {
		        	bytesread += lastread;
		        	if(data.length <= bytesread) {
		        		byte[] newData = new byte[data.length * 2];
		        		for(int i = 0; i < data.length; i++)
		        			newData[i] = data[i];
		        		data = newData;
		        	}
		        }
		    }
		    return data;
		} finally { is.close(); }
	}
}
