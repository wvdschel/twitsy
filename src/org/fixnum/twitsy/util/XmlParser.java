package org.fixnum.twitsy.util;

import java.io.IOException;
import java.io.Reader;

public class XmlParser {
	private XmlElement root;
	private Reader xmlDocument;
	
	public XmlParser(String text) throws Exception {
		xmlDocument = new StringReader(text);
		root = parseNextElement();
	}
	
	public XmlElement getRoot() {
		return root;
	}
	
	private XmlElement parseNextElement() throws Exception {
		String tagType;
		int nextChar;
		XmlElement result;
		
		while(true) {
			skipWhiteSpace();
			
			// Check for open tag
			if((nextChar = xmlDocument.read()) == -1)
				return null;
			if((char)nextChar != '<')
				throw new Exception("Error parsing XML, expecting a '<', got '" + (char)nextChar + "'");
			// Read tag type
			skipWhiteSpace();
			tagType = readWord();
			skipWhiteSpace();
			result = new XmlElement(tagType);
			
			// System.out.println("Found " + tagType + " tag.");
			
			// Read attributes
			String[] attribute; 
			while(true) {
				skipWhiteSpace();
				attribute = readAttribute();
				if(attribute == null)
					break;
				else if(!attribute[0].equals("?")) {
					result.addAttribute(attribute[0], attribute[1]);
				}
			}
			
			// Check for kind of closing tag
			skipWhiteSpace();
			
			switch(peek()) {
			case (int)'/':
				// read close tag and quit
				xmlDocument.read();
				xmlDocument.read();
				break;
			case (int)'>':
				// read close tag and process tag body
				xmlDocument.read();
				if(tagType.charAt(0) != '?')
					readBody(result);
				break;
			default:
				throw new Exception("Unexpected character at end of tag: " + peek());
			}
			
			// Skip <? ?> tags
			if(tagType.charAt(0) != '?')
				break;
		}
		
		return result;
	}
	
	private String[] readAttribute() throws IOException {
		String[] res = new String[2];
		res[0] = readWord();
		skipWhiteSpace();
		if(res[0].length() == 0) // No attribute name found
			return null;
		if((char)peek() == '=') {
			xmlDocument.read(); // Consume '='
			skipWhiteSpace();
			res[1] = readValue();
		} else
			res[1] = "";
		return res;
	}
	
	private String readWord() throws IOException {
		StringBuffer currentItem = new StringBuffer();
		while(true) {
			int lastChar = peek();
			if(lastChar == -1 || isWhiteSpace((char)lastChar) ||
					(char)lastChar == '<' || (char)lastChar == '>' ||
					(char)lastChar == '/' || (char)lastChar == '=')
				break;
			xmlDocument.read();
			currentItem.append((char)lastChar);
		}
		return currentItem.toString();
	}
	
	private String readValue() throws IOException {
		StringBuffer currentItem = new StringBuffer();
		char readUntil, lastChar;
		switch(peek()) {
		case (int)'\'':
		case (int)'"':
			readUntil = (char)xmlDocument.read();
			break;
		default: return readWord();
		}
		while(peek() != -1) {
			lastChar = (char)xmlDocument.read();
			if(lastChar == readUntil)
				break;
			currentItem.append(lastChar);
		}
		return currentItem.toString();
	}
	
	private void readBody(XmlElement node) throws Exception {
		StringBuffer body = new StringBuffer();
		int nextChar;
		while(true) {
			//if(body.length() % 2000 == 0)
			//	System.out.println("Read " + body.length() + " characters into body of " + node.getType() + ".");
			nextChar = peek();
			if(nextChar == -1)
				throw new Exception("Unexpected end-of-file while parsing body of " + node.getType());
			else if((char)nextChar=='<') {
				if(peek(2).equals("</")) {
					xmlDocument.read(); xmlDocument.read(); // skip '</'
					skipWhiteSpace();
					String closeTag = readWord();
					skipWhiteSpace();
					xmlDocument.read();
					if(closeTag.equals(node.getType()))
						break;
					else
						throw new Exception("Unexpected close tag for " + closeTag + " inside " + node.getType());	
				} else
					node.addChild(parseNextElement());
			} else {
				body.append((char)xmlDocument.read());
			}
		}
		node.setBody(XmlEscape.unescape(body.toString()));
	}
	
	private void skipWhiteSpace() throws IOException {
		int nextChar;
		while((nextChar = peek()) != -1) {
			if(isWhiteSpace((char)nextChar))
				xmlDocument.read();
			else
				break;
		}
	}
	
	private String peek(int length) throws IOException {
		xmlDocument.mark(length);
		char[] buff = new char[length];
		xmlDocument.read(buff, 0, length);
		xmlDocument.reset();
		return new String(buff);
	}
	
	private int peek() throws IOException {
		xmlDocument.mark(1);
		int nextChar = xmlDocument.read();
		xmlDocument.reset();
		return nextChar;
	}
	
	private boolean isWhiteSpace(char ch) {
		return ch == ' ' || ch == '\n' || ch == '\r' || ch == '\t';
	}
}
