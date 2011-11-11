package com.joinplato.android.news;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

public class NewsElement implements Serializable {

	private static final long serialVersionUID = 1L;

	private final CharSequence title;

	public NewsElement(String title) {
		this.title = title;
	}

	public static List<NewsElement> mockNews() {
		return Arrays.asList(new NewsElement("Latest IPSOS poll"), //
				new NewsElement("N. Sarkozy took a shower"), //
				new NewsElement("Someone stole my pants"), //
				new NewsElement("Where is Bryan?"), //
				new NewsElement("I like turtles"), //
				new NewsElement("I got a big monkey"), //
				new NewsElement("Relax, don't do it"), //
				new NewsElement("I can haz cheezburger?"), //
				new NewsElement("In vino veritas"), //
				new NewsElement("Drink to get strong muscles"), //
				new NewsElement("Last, but not least"));
	}

	public CharSequence getTitle() {
		return title;
	}

}
