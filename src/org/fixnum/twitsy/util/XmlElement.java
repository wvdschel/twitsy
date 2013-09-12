package org.fixnum.twitsy.util;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

public class XmlElement {
	private String type;
	private Hashtable attributes = new Hashtable();
	private Vector children = new Vector();
	private String body = "";
	
	public XmlElement(String type) {
		this.type = type;
	}
	
	public void addAttribute(String key, String value) {
		attributes.put(key, value);
	}
	
	public void getAttribute(String key) {
		attributes.get(key);
	}
	
	public void addChild(XmlElement child) {
		children.addElement(child);
	}
	
	public XmlElement[] getChildren() {
		XmlElement[] childrenArr = new XmlElement[children.size()];
		children.copyInto(childrenArr);
		return childrenArr;
	}
	
	public void setBody(String body) {
		this.body = body.trim();
	}
	
	public String getBody() {
		return body;
	}
	
	public String getType() {
		return type;
	}
	
	public XmlElement getChild(String tagType) {
		for(int i = 0; i < children.size(); i++)
			if(((XmlElement)children.elementAt(i)).getType().equals(tagType))
				return (XmlElement)children.elementAt(i);
		return null;
	}
	
	public XmlElement[] getChildren(String tagType) {
		Vector matches = new Vector();
		for(int i = 0; i < children.size(); i++)
			if(((XmlElement)children.elementAt(i)).getType().equals(tagType))
				matches.addElement(children.elementAt(i));
		XmlElement[] childrenArr = new XmlElement[matches.size()];
		matches.copyInto(childrenArr);
		return childrenArr;
	}
	
	public String toString() { return toString(""); }
	
	public String toString(String indent) {
		StringBuffer buff = new StringBuffer();
		buff.append(indent + "<" + type);
		Enumeration keys = attributes.keys();
		while(keys.hasMoreElements()) {
			String key = (String)keys.nextElement();
			buff.append(" "+key+"\""+(String)attributes.get(key)+"\"");
		}
		buff.append(">");
		buff.append(body);
		for(int i = 0; i < children.size(); i++)
			buff.append("\n" + ((XmlElement)children.elementAt(i)).toString(indent+"  "));
		buff.append("</" + type + ">");
		return buff.toString();
	}
}
