package org.fixnum.twitsy.data;

import java.util.Vector;

import javax.microedition.rms.RecordEnumeration;
import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;

import org.fixnum.twitsy.data.entities.Account;

public class AccountStorage {
	public static AccountStorage instance;
	
	public static AccountStorage getInstance() {
		try {
			if(instance == null)
				instance = new AccountStorage();
			return instance;
		} catch(RecordStoreException e) {
			return null;
		}
	}
	
	private RecordStore accountStore;
	
	private AccountStorage() throws RecordStoreException {
		accountStore = RecordStore.openRecordStore("TwitsyAccounts",true);
	}
	
	public Account[] getAccounts() {
		try {
			Vector accounts = new Vector();
			RecordEnumeration enum =  accountStore.enumerateRecords(null, null, false);
			while(enum.hasNextElement()) {
				int id = enum.nextRecordId();
				Account acc = new Account(accountStore.getRecord(id));
				acc.setStoreId(id);
				accounts.addElement(acc);
			}
			Account[] arr = new Account[accounts.size()];
			accounts.copyInto(arr);
			return arr;
		} catch (RecordStoreException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public void saveAccount(Account acc) {
		try {
			byte[] data = acc.toBytes();
			if(acc.getStoreId() == -1)
				accountStore.addRecord(data, 0, data.length);
			else
				accountStore.setRecord(acc.getStoreId(), data, 0, data.length);
		} catch (RecordStoreException e) {
			e.printStackTrace();
		}
	}
	
	public void deleteAccount(String username) {
		Account[] a = getAccounts();
		for(int i = 0; i < a.length; i++)
			if(a[i].getUsername().equals(username))
				deleteAccount(a[i].getStoreId());
	}
	
	public void deleteAccount(int storeId) {
		try {
			accountStore.deleteRecord(storeId);
		} catch (RecordStoreException e) {
			e.printStackTrace();
		}
	}
}
