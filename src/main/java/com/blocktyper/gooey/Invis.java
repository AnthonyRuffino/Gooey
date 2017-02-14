package com.blocktyper.gooey;

import org.bukkit.ChatColor;

public class Invis {
	
	public static String prependKey(String text, String key) {
		if (text == null){
			text = "";
		}
		text = encode(key) + text;
		return text;
	}
	
	public static boolean textContainsKey(String text, String key) {
		if (text == null || text.isEmpty()){
			return false;
		}
		text = decode(text);
		return text.contains(key);
	}

	public static String encode(String s) {
		String hidden = "";
		for (char c : s.toCharArray()){
			hidden += ChatColor.COLOR_CHAR + "" + c;
		}
		return hidden;
	}

	public static String decode(String s) {
		if (s != null && !s.isEmpty()) {
			s = s.replace(ChatColor.COLOR_CHAR + "", "");
		}
		return s;
	}
}
