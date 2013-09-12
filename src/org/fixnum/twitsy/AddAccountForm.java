package org.fixnum.twitsy;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.TextField;

public class AddAccountForm extends Form {
	TextField userName, password;
	
	public AddAccountForm() {
		super("Add an account");
		userName = new TextField("Screenname:", "", 15, TextField.ANY);
		password = new TextField("Password:", "", 30, TextField.PASSWORD);
		append(userName);
		append(password);
		addCommand(new Command("Save", "Add account", Command.OK, 2));
		addCommand(new Command("Cancel", "Cancel", Command.BACK, 1));
	}
	
	public String getUsername() {
		return userName.getString();
	}
	
	public String getPassword() {
		return password.getString();
	}
	
	public void clear() {
		userName.setString("");
		password.setString("");
	}
}
