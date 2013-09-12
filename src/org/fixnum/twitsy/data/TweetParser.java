package org.fixnum.twitsy.data;

import org.fixnum.twitsy.StatusKeeper;
import org.fixnum.twitsy.data.entities.Account;
import org.fixnum.twitsy.data.entities.Tweet;
import org.fixnum.twitsy.data.entities.TweetFilter;
import org.fixnum.twitsy.data.entities.User;
import org.fixnum.twitsy.data.entities.UserFilter;
import org.fixnum.twitsy.util.XmlElement;
import org.fixnum.twitsy.util.XmlEscape;
import org.fixnum.twitsy.util.XmlParser;

public class TweetParser {
	public static void parseTweets(String xmlDoc, Account acc) throws Exception {
		parseTweets(xmlDoc, acc, false);
	}
	
	public static void parseTweets(String xmlDoc, Account acc, boolean checkDoubles) throws Exception {
		XmlParser parser = new XmlParser(xmlDoc);
		XmlElement root = parser.getRoot();
		if(root.getType().equals("nilclasses"))
			return;
		if(!root.getType().equals("statuses")) 
			throw new Exception("Expecting a <statuses> tag, got " + root.getType());
		XmlElement[] statuses = root.getChildren("status");
		for(int i = statuses.length - 1; i >= 0; i--)
			parseTweet(statuses[i], acc, checkDoubles);
	}
	
	public static Tweet parseTweet(XmlElement st, Account acc, boolean checkDouble) {
		Tweet tw = new Tweet();
		tw.setAccount(acc.getUsername());
		tw.setDate(st.getChild("created_at").getBody());
		tw.setId(st.getChild("id").getBody());
		// Need double escaping, because Twitter does things like &amp;lt;
		tw.setMessage(XmlEscape.unescape(st.getChild("text").getBody()));
		tw.setReply(tw.getMessage().toLowerCase().indexOf("@" + acc.getUsername().toLowerCase()) >= 0);
		tw.setUser(parseUser(st.getChild("user")));
		
		boolean saveIt;
		if(checkDouble) {
			TweetFilter f = new TweetFilter();
			f.setId(tw.getId());
			f.setAccount(acc.getUsername());
			saveIt = TweetStorage.getInstance().getTweets(f).length == 0;
			//System.out.println("Checked for double, result was: " + !saveIt);
		} else
			saveIt = true;
		
		//System.out.println(tw.getUser().getUsername() + ": " + tw.getMessage());
		
		if(saveIt) {
			TweetStorage.getInstance().saveTweet(tw);
			if(Long.parseLong(tw.getId()) > Long.parseLong(acc.getLastTweet())) {
				acc.setLastTweet(tw.getId());
				AccountStorage.getInstance().saveAccount(acc);
			}
		}
		return tw;
	}
	
	public static User parseUser(XmlElement uXml) {
		UserFilter filter = new UserFilter();
		String id = uXml.getChild("id").getBody();
		filter.setId(id);
		User[] matches = UserStorage.getInstance().getUsers(filter);
		User user;
		if(matches.length == 0) { // Create new user
			user = new User();
		} else { // Use existing instance
			user = matches[0];
		}
		user.setId(id);
		user.setRealName(uXml.getChild("name").getBody());
		user.setUsername(uXml.getChild("screen_name").getBody());
		user.setAvatar(uXml.getChild("profile_image_url").getBody());
		UserStorage.getInstance().saveUser(user);
		return user;
	}
}
