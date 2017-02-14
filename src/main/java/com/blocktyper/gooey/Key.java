package com.blocktyper.gooey;


public class Key {
	String val;

	Key __(String subKey) {
		val = val + "." + subKey;
		return this;
	}

	String end(String subKey) {
		return val + "." + subKey;
	}
}
