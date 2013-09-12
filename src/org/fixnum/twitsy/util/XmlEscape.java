package org.fixnum.twitsy.util;

public class XmlEscape {
	public static final String[][] ESCAPE_TABLE = {
			{"€",   "&euro;"},
			{"\"",  "&#34;",   "&quot;"},
			{"&",   "&#38;",   "&amp;"},
			{"<",   "&#60;",   "&lt;"},
			{">",   "&#62;",   "&gt;"},
			{" ",   "&#160;",  "&nbsp;"},
			{"¡",   "&#161;",  "&iexcl;"},
			{"¢",   "&#162;",  "&cent;"},
			{"£",   "&#163;",  "&pound;"},
			{"¤",   "&#164;",  "&curren;"},
			{"¥",   "&#165;",  "&yen;"},
			{"¦",   "&#166;",  "&brvbar;"},
			{"§",   "&#167;",  "&sect;"},
			{"¨",   "&#168;",  "&uml;"},
			{"©",   "&#169;",  "&copy;"},
			{"ª",   "&#170;",  "&ordf;"},
			{"¬",   "&#172;",  "&not;"},
			{""+(char)0xc2ad,   "&#173;",  "&shy;"},
			{"®",   "&#174;",  "&reg;"},
			{"¯",   "&#175;",  "&macr;"},
			{"°",   "&#176;",  "&deg;"},
			{"±",   "&#177;",  "&plusmn;"},
			{"²",   "&#178;",  "&sup2;"},
			{"³",   "&#179;",  "&sup3;"},
			{"´",   "&#180;",  "&acute;"},
			{"µ",   "&#181;",  "&micro;"},
			{"¶",   "&#182;",  "&para;"},
			{"·",   "&#183;",  "&middot;"},
			{"¸",   "&#184;",  "&cedil;"},
			{"¹",   "&#185;",  "&sup1;"},
			{"º",   "&#186;",  "&ordm;"},
			{"»",   "&#187;",  "&raquo;"},
			{"¼",   "&#188;",  "&frac14;"},
			{"½",   "&#189;",  "&frac12;"},
			{"¾",   "&#190;",  "&frac34;"},
			{"¿",   "&#191;",  "&iquest;"},
			{"À",   "&#192;",  "&Agrave;"},
			{"Á",   "&#193;",  "&Aacute;"},
			{"Â",   "&#194;",  "&Acirc;"},
			{"Ã",   "&#195;",  "&Atilde;"},
			{"Ä",   "&#196;",  "&Auml;"},
			{"Å",   "&#197;",  "&Aring;"},
			{"Æ",   "&#198;",  "&AElig;"},
			{"Ç",   "&#199;",  "&Ccedil;"},
			{"È",   "&#200;",  "&Egrave;"},
			{"É",   "&#201;",  "&Eacute;"},
			{"Ê",   "&#202;",  "&Ecirc;"},
			{"Ë",   "&#203;",  "&Euml;"},
			{"Ì",   "&#204;",  "&Igrave;"},
			{"Í",   "&#205;",  "&Iacute;"},
			{"Î",   "&#206;",  "&Icirc;"},
			{"Ï",   "&#207;",  "&Iuml;"},
			{"Ð",   "&#208;",  "&ETH;"},
			{"Ñ",   "&#209;",  "&Ntilde;"},
			{"Ò",   "&#210;",  "&Ograve;"},
			{"Ó",   "&#211;",  "&Oacute;"},
			{"Ô",   "&#212;",  "&Ocirc;"},
			{"Õ",   "&#213;",  "&Otilde;"},
			{"Ö",   "&#214;",  "&Ouml;"},
			{"×",   "&#215;",  "&times;"},
			{"Ø",   "&#216;",  "&Oslash;"},
			{"Ù",   "&#217;",  "&Ugrave;"},
			{"Ú",   "&#218;",  "&Uacute;"},
			{"Û",   "&#219;",  "&Ucirc;"},
			{"Ü",   "&#220;",  "&Uuml;"},
			{"Ý",   "&#221;",  "&Yacute;"},
			{"Þ",   "&#222;",  "&THORN;"},
			{"ß",   "&#223;",  "&szlig;"},
			{"à",   "&#224;",  "&agrave;"},
			{"á",   "&#225;",  "&aacute;"},
			{"â",   "&#226;",  "&acirc;"},
			{"ã",   "&#227;",  "&atilde;"},
			{"ä",   "&#228;",  "&auml;"},
			{"å",   "&#229;",  "&aring;"},
			{"æ",   "&#230;",  "&aelig;"},
			{"ç",   "&#231;",  "&ccedil;"},
			{"è",   "&#232;",  "&egrave;"},
			{"é",   "&#233;",  "&eacute;"},
			{"ê",   "&#234;",  "&ecirc;"},
			{"ë",   "&#235;",  "&euml;"},
			{"ì",   "&#236;",  "&igrave;"},
			{"í",   "&#237;",  "&iacute;"},
			{"î",   "&#238;",  "&icirc;"},
			{"ï",   "&#239;",  "&iuml;"},
			{"ð",   "&#240;",  "&eth;"},
			{"ñ",   "&#241;",  "&ntilde;"},
			{"ò",   "&#242;",  "&ograve;"},
			{"ó",   "&#243;",  "&oacute;"},
			{"ô",   "&#244;",  "&ocirc;"},
			{"õ",   "&#245;",  "&otilde;"},
			{"ö",   "&#246;",  "&ouml;"},
			{"÷",   "&#247;",  "&divide;"},
			{"ø",   "&#248;",  "&oslash;"},
			{"ù",   "&#249;",  "&ugrave;"},
			{"ú",   "&#250;",  "&uacute;"},
			{"û",   "&#251;",  "&ucirc;"},
			{"ü",   "&#252;",  "&uuml;"},
			{"ý",   "&#253;",  "&yacute;"},
			{"þ",   "&#254;",  "&thorn;"},
		};
	
	public static String unescape(String message) {
		StringBuffer result = new StringBuffer();
		int currentPos = 0, lastMark = 0;
		while(currentPos < message.length()) {
			if(message.charAt(currentPos) != '&')
				currentPos++;
			else {
				if(message.substring(currentPos, currentPos+2).equals("&#")) {
					int semicolon = message.indexOf(';', currentPos);
					if(semicolon != -1) {
						String code = message.substring(currentPos + 2, semicolon);
						if(code.startsWith("x"))
							code = "0" + code;
						//System.out.println("Converting " + code + " to " + (char)Integer.parseInt(code));
						result.append(message.substring(lastMark, currentPos));
						result.append((char)Integer.parseInt(code));
						currentPos = semicolon + 1;
						lastMark = currentPos;
					} else
						currentPos++;
				} else {
					int newMark = lookupInTable(result, message, currentPos, lastMark);
					if(newMark == lastMark)
						currentPos++;
					else {
						currentPos = newMark;
						lastMark = newMark;
					}
				}
			}
		}
		result.append(message.substring(lastMark));
		return result.toString();
	}
	
	private static int lookupInTable(StringBuffer result, String message, int currentPos, int lastMark) {
		boolean found = false;
		for(int entity = 0; entity < ESCAPE_TABLE.length; entity++) {
			for(int alias = 1; alias < ESCAPE_TABLE[entity].length; alias++) {
				int len = ESCAPE_TABLE[entity][alias].length();
				if(message.length() >= len+currentPos &&
						message.substring(currentPos, currentPos+len).equals(ESCAPE_TABLE[entity][alias])) {
					// Found entity, copy everything before & to result
					result.append(message.substring(lastMark, currentPos));
					// Copy entity to result
					result.append(ESCAPE_TABLE[entity][0]);
					lastMark = currentPos + len;
					currentPos += len;
					found = true;
					break;
				}
			}
			if(found)
				break;
		}
		return lastMark;
	}
}