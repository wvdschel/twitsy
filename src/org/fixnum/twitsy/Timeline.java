package org.fixnum.twitsy;

import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;

import org.fixnum.twitsy.data.entities.Tweet;

public class Timeline extends Canvas implements StatusListener {
	private static final int 	bg = 0xeeeeee, fg = 0x000000, selectionBg = 0x94E6E8, selectionFg = fg,
								overlayFg = fg, tabBarBg = 0xdddddd;
	private static final Font 	overlayFont = Font.getFont(Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_SMALL),
								tabFont = Font.getFont(Font.FACE_SYSTEM, Font.STYLE_BOLD, Font.SIZE_LARGE);
	private int overlayBg = 0xdddddd; // This one can change over time, so not static final
	
	private Vector allEntries = new Vector(), replies = new Vector();
	private String statusMessage = "Welcome to Twitsy!";
	private int currentlySelected = 0, // Select active entry
				currentTopPixel = 0, // Where the top of the active entry is drawn
				animationTarget = 0; // Should become the top pixel after animation
	private Timer animTimer = new Timer();
	private TimerTask scroller = new SmoothScroller();
	//private Command tweet;
	private boolean showRepliesOnly = false;
	private int totalHeight = 0; // Total pixel height of all visible tweets
	
	public Timeline() {
		super();
		animTimer.schedule(scroller, 70, 70);
		setTitle("Twitsy Timeline");
		StatusKeeper.addListener(this);
		addCommand(new Command("Tweet", "New tweet", Command.ITEM, 1));
		addCommand(new Command("Reply", "Reply", Command.OK, 2));
		addCommand(new Command("Retweet", "Retweet", Command.OK, 3));
		addCommand(new Command("Accounts", "Account list", Command.OK, 3));
		addCommand(new Command("Refresh", "Refresh", Command.OK, 3));
		addCommand(new Command("Reset", "Reset local data", Command.OK, 3));
		addCommand(new Command("Exit", "Exit", Command.EXIT, 4));
	}
	
	public void statusChanged(String statMsg, int type) {
		switch(type) {
		case StatusKeeper.ERROR:
			overlayBg = 0xdd5555;
			break;
		default:
			overlayBg = 0xdddddd;
		}
		
		int firstNewLine = statMsg.indexOf('\n');
		if(firstNewLine > 0)
			statusMessage = statMsg.substring(0, firstNewLine);
		else
			statusMessage = statMsg;
		
		int overlayPos = getHeight() - overlayFont.getHeight() - 5;
		repaint(0,overlayPos,getWidth(),getHeight());
	}
	
	private void calculateHeight() {
		totalHeight = 0;
		for(int i = 0; i < filteredTweets().size(); i ++)
			totalHeight += ((TweetView)filteredTweets().elementAt(i)).getHeight(getWidth());
	}
	
	public void addEntry(TweetView entry) {
		int positionAll = insert(entry, allEntries);
		if(entry.getTweet().isReply()) {
			int position = insert(entry, replies);
			if(showRepliesOnly)
				navigateTo(position, true);
		}
		if(!showRepliesOnly)
			navigateTo(positionAll, true);
		calculateHeight();
		repaint();
	}
	
	private int insert(TweetView entry, Vector list) {
		int i;
		long twId = Long.parseLong(entry.getTweet().getId()), lastId = 0;
		for(i = 0; i < list.size(); i++) {
			lastId = Long.parseLong(((TweetView)list.elementAt(i)).getTweet().getId());
			if(lastId >= twId)
				break;
		}
		if(lastId != twId)
			list.insertElementAt(entry, i);
		
		return i;
	}
	
	public void clear() {
		allEntries.removeAllElements();
		replies.removeAllElements();
		currentlySelected = 0;
		repaint();
	}

	protected void paint(Graphics g) {
		g.setColor(0xffffff);
		g.fillRect(0,0, getWidth(), getHeight());
		
		/*int currentPos = tabFont.getHeight() + 4;

		for(int i = currentlySelected; i >= 0; i--) {
			Image img;
			if(i == currentlySelected)
				img = ((TweetView)filteredTweets().elementAt(i)).getImage(getWidth(), selectionBg, selectionFg);
			else
				img = ((TweetView)filteredTweets().elementAt(i)).getImage(getWidth(), bg, fg);
			g.drawImage(img, 0, currentPos, 0);
			currentPos += img.getHeight() + 1;
			g.drawLine(0, currentPos-1, getWidth(), currentPos-1);
			if(currentPos >= getHeight())
				break;
		}*/
		
		Vector entries = filteredTweets();
		
		if(currentlySelected < entries.size()) {
			// Draw up from the active item
			int selectedEntryHeight = ((TweetView)entries.elementAt(currentlySelected)).getHeight(getWidth());
			int currentPos = currentTopPixel + selectedEntryHeight;
			for(int i = currentlySelected; i < entries.size(); i++) {
				int height = ((TweetView)entries.elementAt(i)).getHeight(getWidth());
				currentPos -=  height;
				if(currentPos < getHeight()) {
					if(currentlySelected == i)
						g.drawImage(((TweetView)entries.elementAt(i)).getImage(getWidth(), selectionBg, selectionFg), 0, currentPos, 0);
					else
						g.drawImage(((TweetView)entries.elementAt(i)).getImage(getWidth(), bg, fg), 0, currentPos, 0);
				}
				if(currentPos + height < 0)
					break;
			}
			// Draw down from the active item
			currentPos = currentTopPixel+selectedEntryHeight;
			for(int i = currentlySelected - 1; i >= 0 && currentPos < getHeight(); i--) {
				g.drawImage(((TweetView)entries.elementAt(i)).getImage(getWidth(), bg, fg), 0, currentPos, 0);
				currentPos += ((TweetView)entries.elementAt(i)).getHeight(getWidth());
			}
		}

		drawTabBar(g);
		drawOverlay(g);
	}
	
	private void drawTabBar(Graphics g) {
		if(showRepliesOnly) {
			g.setColor(tabBarBg);
			g.fillRect(0, 0, getWidth()/2, tabFont.getHeight() + 4);
			g.setColor(overlayFg);
			g.drawString("Timeline", getWidth()/4, 2, Graphics.HCENTER | Graphics.TOP);
		} else {
			g.setColor(selectionBg);
			g.fillRect(0, 0, getWidth()/2, tabFont.getHeight() + 4);
			g.setColor(selectionFg);
			g.drawString("Timeline", getWidth()/4, 2, Graphics.HCENTER | Graphics.TOP);
		}
		if(!showRepliesOnly) {
			g.setColor(tabBarBg);
			g.fillRect(getWidth()/2, 0, getWidth(), tabFont.getHeight() + 4);
			g.setColor(overlayFg);
			g.drawString("Replies", 3*(getWidth()/4), 2, Graphics.HCENTER | Graphics.TOP);
		} else {
			g.setColor(selectionBg);
			g.fillRect(getWidth()/2, 0, getWidth(), tabFont.getHeight() + 4);
			g.setColor(selectionFg);
			g.drawString("Replies", 3*(getWidth()/4), 2, Graphics.HCENTER | Graphics.TOP);
		}
	}
	
	private void drawOverlay(Graphics g) {
		g.setColor(overlayBg);
		int overlayPos = getHeight() - overlayFont.getHeight() - 5;
		g.fillRect(0, overlayPos, getWidth(), overlayPos);
		g.setColor(overlayFg);
		g.drawLine(0, overlayPos, getWidth(), overlayPos);
		g.drawString(statusMessage, 1, overlayPos + 1, 0);
	}
	
	protected void keyRepeated(int keyCode) {
		keyPressed(keyCode);
	}
	
	private Vector filteredTweets() {
		if(showRepliesOnly)
			return replies;
		else
			return allEntries;
	}
	
	protected void scroll(int pixels) {
		currentTopPixel += pixels;
		// Size of the top tab bar
		if(currentlySelected == filteredTweets().size()-1)
			currentTopPixel = Math.min(tabFont.getHeight() + 4, currentTopPixel);
		// Size of the bottom bar
		if(currentlySelected == 0) {
			currentTopPixel = Math.max(- overlayFont.getHeight() + 5, currentTopPixel);
		}
		// Determine what tweet has the focus
		int height = 0, i = currentlySelected;
		if(currentTopPixel < 0) {
			for(; i >= 0; i--) {
				height -= ((TweetView)filteredTweets().elementAt(i)).getHeight(getWidth());
				if(height < currentTopPixel - getHeight() / 2)
					break;
			}
			if(currentlySelected != i) {
				currentTopPixel -= height + ((TweetView)filteredTweets().elementAt(i)).getHeight(getWidth());
				currentlySelected = i;
			}
		} else {
			if(currentTopPixel > getHeight() / 2) {
				currentlySelected++;
				currentTopPixel -= ((TweetView)filteredTweets().elementAt(currentlySelected)).getHeight(getWidth());
			}
		}
		
		animationTarget = currentTopPixel;
		repaint();
	}
	
	private int minHeight() {
		return TweetView.usernameFont.getHeight() * 10;
	}
	
	protected void keyPressed (int keyCode)
	{
		int oldSelection = currentlySelected;
		switch(keyCode) {
		case -1: // Up
			if(getHeight() > minHeight())
				navigateTo(Math.min(filteredTweets().size()-1, currentlySelected+1), false);
			else
				scroll(25);
			break;
		case -2: // Down
			if(getHeight() > minHeight())
				navigateTo(Math.max(0, currentlySelected-1), false);
			else
				scroll(-25);
			break;
		case -4: // Right
			if(!showRepliesOnly) {
				showRepliesOnly = true;
				navigateTo(filteredTweets().size() - 1, true);
				calculateHeight();
				repaint();
			}
			break;
		case -3: // Left;
			if(showRepliesOnly) {
				showRepliesOnly = false;
				navigateTo(filteredTweets().size() - 1, true);
				calculateHeight();
				repaint();
			}
			break;
		default: 
			System.out.println("Unknown keycode: " + keyCode);
		}
		if(oldSelection != currentlySelected)
			repaint();
	}
	
	private void navigateTo(int entry, boolean instant) {
		if(entry < 0)
			entry = 0;
		synchronized(animTimer) {
			if(instant) {
				currentlySelected = entry;
				//tryToCenter(entry);
				currentTopPixel = tabFont.getHeight() + 4;
				animationTarget = currentTopPixel;
				repaint();
			} else {
				tryToCenter(entry);
			}
		}
	}
	
	private void tryToCenter(int entry) {
		if(entry == -1)
			return;
		if(currentlySelected == -1)
			currentlySelected = 0;
		
		Vector entries = filteredTweets();
		if(entries.size() == 0)
			return;
		
		int inBetween = 0, step = currentlySelected > entry ? -1 : 1;
		for(int i = currentlySelected; i != entry; i += step)
			inBetween += ((TweetView)entries.elementAt(i)).getHeight(getWidth());
		
		//System.out.println("Moving " + (step == 1 ? "up " : "down ") +
		//		(step * (entry - currentlySelected)) + " entries, "
		//		+ inBetween + " pixels.");
		
		int myHeight = ((TweetView)entries.elementAt(entry)).getHeight(getWidth());
		int attempt = (getHeight() - myHeight)/2;
		
		/*
		// Try and fill the top
		int filled = 0;
		for(int i = entry+1; i < entries.size() && filled < attempt; i++)
			filled += ((TweetView)entries.elementAt(i)).getHeight(getWidth());
		
		if(attempt <= filled) {
			// See if we can fill the bottom up.
			filled = 0;
			int goal = getHeight() - attempt - myHeight; 
			for(int i = entry-1; i >= 0 && filled < goal; i--)
				filled += ((TweetView)entries.elementAt(i)).getHeight(getWidth());
			if(filled < goal) {
				if(filled != 0)
					attempt = getHeight() - filled - myHeight;
				else
					attempt = getHeight() - myHeight - overlayFont.getHeight() - 3;
			} 
		} else { // Top couldn't be filled
			if(filled == 0)
				attempt = tabFont.getHeight() + 4;
			else
				attempt = filled;
		}*/
		
		animationTarget = attempt;
		//currentTopPixel = attempt + -1 * step * inBetween;
		currentTopPixel -= step * inBetween;
		
		currentlySelected = entry;
	}
	
	private class SmoothScroller extends TimerTask {
		public void run() {
			synchronized (animTimer) {
				int speed = Math.max(Math.abs(currentTopPixel - animationTarget) / 5, 10);
				
				if(currentTopPixel > animationTarget) {
					currentTopPixel = Math.max(currentTopPixel-speed, animationTarget);
					repaint();
				} else if(currentTopPixel < animationTarget) {
					currentTopPixel = Math.min(currentTopPixel+speed, animationTarget);
					repaint();
				}
				
			}
		}
	}
	
	public Tweet getSelectedTweet() {
		if(currentlySelected >= 0 && currentlySelected < filteredTweets().size()) {
			return ((TweetView)filteredTweets().elementAt(currentlySelected)).getTweet();
		} else
			return null;
	}
	
	/* Touch screen code */
	private boolean dragged;
	private int prevY;
	
	public void pointerPressed(int x, int y) {
		dragged = false;
		prevY = y;
	}
	
	public void pointerReleased(int x, int y) {
		if(!dragged) {
			if(y <= tabFont.getHeight() + 4) {
				// Select tab by simulating keypress
				keyPressed(x < getHeight() / 2 ? -3 : -4);
			} else {
				int tweetIndex = currentlySelected, goalPixel = y - currentTopPixel,
					currentPixel = 0;
				if(goalPixel < 0)
					tweetIndex++;
				
				Vector entries = filteredTweets();
				
				while(tweetIndex >= 0 && tweetIndex < entries.size()) {
					TweetView tw = (TweetView)entries.elementAt(tweetIndex);
					int height = tw.getHeight(getWidth());
					
					if(goalPixel < 0)
						currentPixel -= height;
						
					if(goalPixel >= currentPixel
							&& goalPixel < currentPixel + height) {
						navigateTo(tweetIndex, false);
						break;
					}
						
					if(goalPixel < 0)
						tweetIndex++;
					else {
						currentPixel += height;
						tweetIndex--;
					}
				}
			}
		}
	}
	
	public void pointerDragged(int x, int y) {
		synchronized(animTimer) {
			currentTopPixel += (y - prevY);
			animationTarget = currentTopPixel;
			repaint();
		}
		dragged = true;
		prevY = y;
	}
	
}
