package org.fixnum.twitsy;

import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;

import org.fixnum.twitsy.data.AccountStorage;
import org.fixnum.twitsy.data.PublishTweetRunnable;
import org.fixnum.twitsy.data.TweetListener;
import org.fixnum.twitsy.data.TweetStorage;
import org.fixnum.twitsy.data.UserStorage;
import org.fixnum.twitsy.data.entities.Account;
import org.fixnum.twitsy.data.entities.Tweet;
import org.fixnum.twitsy.data.entities.TweetFilter;

public class Twitsy extends MIDlet implements CommandListener, TweetListener, StatusListener {
	private static String version;
	
	public static String getVersionNumber() { return version; }
	
	private Timeline timeLine;
	private AddAccountForm accountCreation;
	private AccountList accountList; 
	private UpdateStatus updateStatus;
	private Display display;
	private String activeUsername;
	
	public Twitsy() {}

	protected void destroyApp(boolean arg0) throws MIDletStateChangeException {}

	protected void pauseApp() {}

	protected void startApp() throws MIDletStateChangeException {
		version = this.getAppProperty("MIDlet-Version");
		
		StatusKeeper.addListener(this);
		display = Display.getDisplay(this);
		
		timeLine = new Timeline();
		timeLine.setCommandListener(this);
		
		accountList = new AccountList();
		
		accountCreation = new AddAccountForm();
		
		updateStatus = new UpdateStatus();
		
		accountList.setCommandListener(this);
		accountCreation.setCommandListener(this);
		updateStatus.setCommandListener(this);
		
		int accountCount = AccountStorage.getInstance().getAccounts().length;
		TweetStorage.getInstance().addTweetListener(this);
		if(accountCount == 0)
			display.setCurrent(accountCreation);
		else if(accountCount == 1) {
			activeUsername = AccountStorage.getInstance().getAccounts()[0].getUsername();
			refreshList();
			display.setCurrent(timeLine);
		} else
			display.setCurrent(accountList);
	}
	
	public void tweetAdded(Tweet tw) {
		if(tw.getAccount().equals(activeUsername))
			timeLine.addEntry(new TweetView(tw));
		else
			System.out.println(tw.getAccount() + " != " + activeUsername);
	}
	
	private void refreshList() {
		timeLine.clear();
		TweetFilter filter = new TweetFilter();
		System.out.println("user: " + activeUsername);
		filter.setAccount(activeUsername);
		Tweet[] tweets = TweetStorage.getInstance().getTweets(filter);
		for(int i = 0; i < tweets.length; i++)
			timeLine.addEntry(new TweetView(tweets[i]));
	}

	public void commandAction(Command command, Displayable disp) {
		if(disp == accountCreation)
			processCommandForAccountCreation(command.getLabel());
		else if(disp == accountList)
			processCommandForAccountList(command.getLabel());
		else if(disp == timeLine)
			processCommandForTimeline(command.getLabel());
		else if(disp == updateStatus)
			processCommandForTweet(command.getLabel());
		else // Let's just assume it's an alert dialog ;)
			// System.out.println("Unknown command: " + command.getLabel() + " from " + disp.getTitle());
			display.setCurrent(timeLine);
	}
	
	private void processCommandForAccountList(String label) {
		if(label.equals("Select")) {
			activeUsername = accountList.getSelectedUser();
			System.out.println("Activated " + activeUsername);
			display.setCurrent(timeLine);
			refreshList();
		} else if(label.equals("Add account")) {
			display.setCurrent(accountCreation);
		} else if(label.equals("Remove")) {
			String removeUser = accountList.getSelectedUser();
			AccountStorage.getInstance().deleteAccount(removeUser);
			accountList.updateList();
			if(activeUsername.equals(removeUser)) {
				Account[] accs = AccountStorage.getInstance().getAccounts();
				if(accs.length > 0)
					activeUsername = accs[0].getUsername();
				else
					activeUsername = "";
				refreshList();
			}
			// Remove all tweets from this user
			TweetFilter filter = new TweetFilter();
			filter.setAccount(removeUser);
			Tweet[] tweets = TweetStorage.getInstance().getTweets(filter);
			for(int i = 0; i < tweets.length; i++)
				TweetStorage.getInstance().deleteTweet(tweets[i].getStoreId());
		} else if(label.equals("Cancel")) {
			clearForms();
			display.setCurrent(timeLine);
		} else
			System.out.println("Unknown command: " + label);
	}
	
	private void processCommandForAccountCreation(String label) {
		if(label.equals("Save")) {
			Account acc = new Account();
			acc.setLastTweet("1");
			acc.setPassword(accountCreation.getPassword());
			acc.setUsername(accountCreation.getUsername());
			AccountStorage.getInstance().saveAccount(acc);
			activeUsername = acc.getUsername();
			accountList.updateList();
			display.setCurrent(timeLine);
			refreshList();
			clearForms();
			TweetStorage.getInstance().updateNow();
		} else if(label.equals("Cancel")) {
			clearForms();
			display.setCurrent(accountList);
		} else
			System.out.println("Unknown command: " + label);
	}
	
	private void processCommandForTweet(String label) {
		if(label.equals("Update")) {
			display.setCurrent(timeLine);
			publishTweet(updateStatus.getString(), updateStatus.getInReplyTo());
			clearForms();
			refreshList();
		} else if(label.equals("Cancel")) {
			clearForms();
			display.setCurrent(timeLine);
		} else
			System.out.println("Unknown command: " + label);
	}
	
	private void processCommandForTimeline(String label) {
		if(label.equals("Accounts")) {
			display.setCurrent(accountList);
		} else if(label.equals("Tweet")) {
			display.setCurrent(updateStatus);
		} else if(label.equals("Refresh")) {
			display.setCurrent(timeLine);
			TweetStorage.getInstance().updateNow();
		} else if(label.equals("Exit")) {
			this.notifyDestroyed();
		} else if(label.equals("Reply")) {
			Tweet tw = timeLine.getSelectedTweet();
			if(tw != null) {
				updateStatus.setString("@"+tw.getUser().getUsername()+" ");
				updateStatus.setInReplyTo(tw.getId());
				display.setCurrent(updateStatus);
			} else {
				display.setCurrent(new Alert("No tweet selected", "You can't reply or retweet if you don't select a tweet, silly!", null, AlertType.ERROR));
			}
		} else if(label.equals("Retweet")) {
			Tweet tw = timeLine.getSelectedTweet();
			if(tw != null) {
				updateStatus.setString("RT @"+tw.getUser().getUsername()+" "+tw.getMessage());
				display.setCurrent(updateStatus);
			} else {
				display.setCurrent(new Alert("No tweet selected", "You can't reply or retweet if you don't select a tweet, silly!", null, AlertType.ERROR));
			}
		} else if(label.equals("Reset")) {
			TweetStorage.reset();
			UserStorage.reset();
			
			Account[] accs = AccountStorage.getInstance().getAccounts();
			for(int i = 0; i < accs.length; i++) {
				accs[i].setLastTweet("1");
				AccountStorage.getInstance().saveAccount(accs[i]);
			}
			
			TweetStorage.getInstance().addTweetListener(this);
			
			refreshList();
			StatusKeeper.setStatus("Data reset");
		} else
			System.out.println("Unknown command: " + label);
	}
	
	private void publishTweet(String message, String inReplyTo) {
		Account[] userInfo = AccountStorage.getInstance().getAccounts();
		for(int i = 0; i < userInfo.length; i++)
			if(userInfo[i].getUsername().equals(activeUsername))
				new Thread(new PublishTweetRunnable(userInfo[i], message, inReplyTo)).start();
	}

	private void clearForms() {
		updateStatus.clear();
		accountCreation.clear();
	}

	public void statusChanged(String status, int type) {
		Alert a;
		switch(type) {
		case StatusKeeper.SERIOUS_ERROR:
			a = new Alert("Error", status, null, AlertType.ERROR);
			a.setCommandListener(this);
			display.setCurrent(a, display.getCurrent());
			break;
		case StatusKeeper.UPDATE:
			a = new Alert("Update available", status, null, AlertType.INFO);
			a.setCommandListener(this);
			display.setCurrent(a, display.getCurrent());
			break;
		default:
			break;
		}
	}
}
