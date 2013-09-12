package org.fixnum.twitsy;

import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

import org.fixnum.twitsy.data.entities.Tweet;

public class TweetView {
	private Image renderedBuffer;
	private int background, foreground;
	private Tweet tweet;
	public static final Font usernameFont = Font.getFont(Font.FACE_SYSTEM, Font.STYLE_BOLD | Font.STYLE_UNDERLINED, Font.SIZE_MEDIUM),
						messageFont  = Font.getFont(Font.FACE_PROPORTIONAL, Font.STYLE_PLAIN, Font.SIZE_MEDIUM);
	
	public TweetView(Tweet tweet) {
		this.tweet = tweet;
	}
	
	public Image getImage(int width, int bgColor, int fgColor) {
		if(renderedBuffer == null || renderedBuffer.getWidth() != width || 
				bgColor != background || fgColor != foreground) {
			// TODO: fix longer-than-line words
			int[] splits = splitPositions(tweet.getMessage());
			renderedBuffer = Image.createImage(width, getHeight(splits, width));
			drawImage(splits, width, bgColor, fgColor);
			background = bgColor; foreground = fgColor;
		}
		return renderedBuffer;
	}
	
	public Image getTempImage(int width, int bgColor, int fgColor) {
		Image buImage = renderedBuffer;
		int buFg = foreground, buBg = background;
		try {
			return getImage(width, bgColor, fgColor);
		} finally {
			// restore image at the end
			renderedBuffer = buImage;
			foreground = buFg;
			background = buBg;
		}
	}
	
	public int getHeight(int width) {
		if(renderedBuffer == null || renderedBuffer.getWidth() != width)
			return getHeight(splitPositions(tweet.getMessage()), width);
		else
			return renderedBuffer.getHeight();
	}
	
	private int getHeight(int[] splitPositions, int width) {
		int textHeight = 1, currentPos = 51, prevIndex = 0;
		// Draw sender string
		textHeight += usernameFont.getHeight();
		// Draw message word for word, wrapping to a new line when needed
		for(int i = 0; i < splitPositions.length; i++) {
			String messagePart = tweet.getMessage().substring(prevIndex, splitPositions[i]);
			int strWidth = messageFont.stringWidth(messagePart);
			if(strWidth + currentPos > width) {
				textHeight += messageFont.getHeight();
				currentPos = textHeight > 50 ? 1 : 51;
				currentPos += strWidth;
				prevIndex = splitPositions[i];
			} else {
				currentPos += strWidth;
				prevIndex = splitPositions[i];
			}
		}
		String messagePart = tweet.getMessage().substring(prevIndex);
		if(messageFont.stringWidth(messagePart) + currentPos > width)
			textHeight += messageFont.getHeight();
		textHeight += messageFont.getHeight() +1 ;
		return Math.max(50, textHeight);
	}
	
	private void drawImage(int[] splitPositions, int width, int bgColor, int fgColor) {
		Graphics g = renderedBuffer.getGraphics();
		int textHeight = 1, currentPos = 51, prevIndex = 0;
		// Draw background and set color
		g.setColor(bgColor);
		g.fillRect(0,0,renderedBuffer.getWidth(),renderedBuffer.getHeight());
		g.setColor(fgColor);
		// Draw sender string
		g.setFont(usernameFont);
		g.drawString(tweet.getUser().getUsername(), currentPos, textHeight, 0);
		textHeight += usernameFont.getHeight();
		// Draw message word for word, wrapping to a new line when needed
		g.setFont(messageFont);
		for(int i = 0; i < splitPositions.length; i++) {
			String messagePart = tweet.getMessage().substring(prevIndex, splitPositions[i]);
			int strWidth = messageFont.stringWidth(messagePart);
			if(strWidth + currentPos > width) {
				textHeight += messageFont.getHeight();
				currentPos = textHeight > 50 ? 1 : 51;
				g.drawString(messagePart, currentPos, textHeight, 0);
				currentPos += strWidth;
				prevIndex = splitPositions[i];
			} else {
				g.drawString(messagePart, currentPos, textHeight, 0);
				currentPos += strWidth;
				prevIndex = splitPositions[i];
			}
		}
		String messagePart = tweet.getMessage().substring(prevIndex);
		if(messageFont.stringWidth(messagePart) + currentPos > width) {
			g.drawString(messagePart, textHeight > 50 ? 1 : 51, textHeight + messageFont.getHeight(), 0);
		} else
			g.drawString(messagePart, currentPos, textHeight, 0);
		// Draw avatar
		g.drawImage(tweet.getUser().getAvatar(), 1, 1, 0);
		//g.drawLine(0, renderedBuffer.getHeight()-1, renderedBuffer.getWidth(), renderedBuffer.getHeight()-1);
	}
	
	private int[] splitPositions(String message) {
		int[] positions = new int[15];
		int splitCount = 0;
		
		for(int pos = 0; pos < message.length(); pos++) {
			if(isWhiteSpace(message.charAt(pos))) {
				if(positions.length == splitCount) {
					int[] nPos = new int[splitCount+2];
					for(int i = 0; i < splitCount; i++)
						nPos[i] = positions[i];
					positions = nPos;
				}
				positions[splitCount++] = pos;
			}
		}
		
		if(splitCount != positions.length) {
			int[] nPos = new int[splitCount];
			for(int i = 0; i < splitCount; i++)
				nPos[i] = positions[i];
			positions = nPos;
		}
		return positions;
	}
	
	private boolean isWhiteSpace(char ch) {
		return ch == ' ' || ch == '\n' || ch == '\r' || ch == '\t';
	}
	
	public Tweet getTweet() {
		return tweet;
	}
}
