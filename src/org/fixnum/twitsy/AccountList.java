package org.fixnum.twitsy;

import javax.microedition.lcdui.Choice;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.List;

import org.fixnum.twitsy.data.AccountStorage;
import org.fixnum.twitsy.data.entities.Account;

public class AccountList extends List {
	
	public AccountList() {
		super("Account List", Choice.IMPLICIT);
		Command select = new Command("Select", "Select account", Command.OK, 10);
		addCommand(new Command("Cancel", "Cancel", Command.BACK, 100));
		addCommand(select);
		addCommand(new Command("Add account", "Add another account", Command.OK, 10));
		addCommand(new Command("Remove", "Remove account", Command.OK, 10));
		this.setSelectCommand(select);
		updateList();
	}
	
	public void updateList() {
		this.deleteAll();
		Account[] accs = AccountStorage.getInstance().getAccounts();
		for(int i = 0; i < accs.length; i++)
			this.append(accs[i].getUsername(), null);
	}
	
	public String getSelectedUser() {
		return this.getString(getSelectedIndex());
	}
}
