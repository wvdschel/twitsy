package org.fixnum.twitsy;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.TextBox;

public class UpdateStatus extends TextBox {
	private String inReplyTo = null;
	
	public UpdateStatus() {
		super("What are you doing?","", 140, 0);
		addCommand(new Command("Update", "Update your status", Command.OK, 1));
		addCommand(new Command("Cancel", "Back to your timeline", Command.CANCEL, 2));
	}
	
	public void clear() {
		setString("");
		inReplyTo = null;
	}

	public String getInReplyTo() {
		return inReplyTo;
	}

	public void setInReplyTo(String inReplyTo) {
		this.inReplyTo = inReplyTo;
	}
}
