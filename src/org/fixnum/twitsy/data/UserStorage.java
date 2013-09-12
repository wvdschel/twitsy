package org.fixnum.twitsy.data;

import java.util.Hashtable;
import java.util.Vector;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.rms.RecordEnumeration;
import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;

import org.fixnum.twitsy.data.entities.User;
import org.fixnum.twitsy.data.entities.UserFilter;

public class UserStorage {
	public static UserStorage instance;
	
	public static UserStorage getInstance() {
		try {
			if(instance == null)
				instance = new UserStorage();
			return instance;
		} catch(RecordStoreException e) {
			return null;
		}
	}
	
	public static void reset() {
		try {
			getInstance().close();
			RecordStore.deleteRecordStore("TwitsyAvatars");
			RecordStore.deleteRecordStore("TwitsyUsers");
			instance = new UserStorage();
		} catch (RecordStoreException e) {
			e.printStackTrace();
		}
	}
	
	private Hashtable avatars = new Hashtable();
	
	public User getUser(int storeId) {
		try {
			User u = new User(userStore.getRecord(storeId));
			u.setStoreId(storeId);
			return u;
		} catch (RecordStoreException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public void saveUser(User u) {
		try {
			byte[] data = u.toBytes();
			if(u.getStoreId() == -1)
				u.setStoreId(userStore.addRecord(data, 0, data.length));
			else
				userStore.setRecord(u.getStoreId(), data, 0, data.length);
		} catch (RecordStoreException e) {
			e.printStackTrace();
		}
	}
	
	public Image getAvatar(int id) {
		try {
			Integer idObj = new Integer(id);
			Image img = (Image)avatars.get(idObj);
			if(img != null)
				return (Image)avatars.get(idObj);
			else {
				byte[] imageData = avatarStore.getRecord(id);
				img = Image.createImage(imageData, 0, imageData.length);
				if(img.getWidth() != 48 || img.getWidth() != 48)
					img = createThumbnail(img);
				avatars.put(idObj, img);
				return img;
			}
		} catch (RecordStoreException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	private Image createThumbnail(Image image)  
	{  
	   int sourceWidth = image.getWidth();  
	   int sourceHeight = image.getHeight();  
	   int thumbWidth = 48;  
	   int thumbHeight = 48;  
	   
	   if (thumbHeight == -1)  
	      thumbHeight = thumbWidth * sourceHeight / sourceWidth;  
	   
	   Image thumb = Image.createImage(thumbWidth, thumbHeight);  
	   Graphics g = thumb.getGraphics();  
	   
	   for (int y = 0; y < thumbHeight; y++)  
	   {  
	      for (int x = 0; x < thumbWidth; x++)  
	      {  
	        g.setClip(x, y, 1, 1);  
	        int dx = x * sourceWidth / thumbWidth;  
	        int dy = y * sourceHeight / thumbHeight;  
	        g.drawImage(image, x - dx, y - dy, Graphics.LEFT | Graphics.TOP);  
	      }  
	   }  
	   
	   Image immutableThumb = Image.createImage(thumb);  
	   
	   return immutableThumb;  
	}
	
	public int addAvatar(byte[] data) {
		try {
			return avatarStore.addRecord(data, 0, data.length);
		} catch (RecordStoreException e) {
			e.printStackTrace();
			return -1;
		}
	}
	
	public void updateAvatar(int id, byte[] data) {
		try {
			avatarStore.setRecord(id, data, 0, data.length);
		} catch (RecordStoreException e) {
			e.printStackTrace();
		}
	}
	
	private RecordStore userStore, avatarStore;
	
	private UserStorage() throws RecordStoreException {
		userStore = RecordStore.openRecordStore("TwitsyUsers",true);
		avatarStore = RecordStore.openRecordStore("TwitsyAvatars",true);
	}
	
	private void close() throws RecordStoreException {
		userStore.closeRecordStore();
		avatarStore.closeRecordStore();
	}
	
	public User[] getUsers() {
		return getUsers(null);
	}
	
	public User[] getUsers(UserFilter filter) {
		try {
			RecordEnumeration enum = userStore.enumerateRecords(filter, null, false);
			Vector uList = new Vector();
			while(enum.hasNextElement()) {
				int id = enum.nextRecordId();
				User u = new User(userStore.getRecord(id));
				u.setStoreId(id);
				uList.addElement(u);
			}
			User[] res = new User[uList.size()];
			uList.copyInto(res);
			return res;
		} catch (RecordStoreException e) {
			e.printStackTrace();
			return null;
		}
	}
}
