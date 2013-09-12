package org.fixnum.twitsy;

import java.util.Vector;

public class StatusKeeper {
	public static final int NORMAL = 0, ERROR = 1, WARNING = 2, UPDATE = 3, SERIOUS_ERROR = 4;
	private static Vector listeners = new Vector();
	
	public static void addListener(StatusListener l) {
		listeners.addElement(l);
	}
	
	public static void removeListener(StatusListener l) {
		listeners.removeElement(l);
	}
	
	public static void setStatus(String msg) {
		setStatus(msg, NORMAL);
	}
	
	public static void setStatus(String msg, int type) {
		System.out.println("Status change: " + msg);
		for(int i = 0; i < listeners.size(); i++)
			((StatusListener)listeners.elementAt(i)).statusChanged(msg, type);
	}
}
