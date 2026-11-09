package com.chas.blog.util;

import java.text.Normalizer;
import java.util.Locale;

import com.chas.blog.exceptions.post.InvalidTitleException;

public class SlugGenerator {

	private SlugGenerator() {
	}

	public static String fromTitle(String title) {

		var normalized = Normalizer.normalize(title, Normalizer.Form.NFD)
				.replaceAll("\\p{M}", "")
				.toLowerCase(Locale.ROOT)
				.replaceAll("[^a-z0-9]+", "-")
				.replaceAll("^-+", "")
				.replaceAll("-+$", "");

		if (normalized.isEmpty()) {
			throw new InvalidTitleException("The Title \"" + title + "\" cannot be converted to a valid slug.");
		}

		return normalized;
	}

}
