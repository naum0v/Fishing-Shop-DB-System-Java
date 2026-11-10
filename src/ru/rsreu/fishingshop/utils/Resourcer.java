package ru.rsreu.fishingshop.utils;

import java.util.ResourceBundle;

public class Resourcer {
	private static final Resourcer instance = new Resourcer();
	private ResourceBundle bundle;

	private Resourcer() {
		bundle = ResourceBundle.getBundle("text");
	}

	public static Resourcer getInstance() {
		return instance;
	}

	public String getString(String key) {
		return bundle.getString(key);
	}
}