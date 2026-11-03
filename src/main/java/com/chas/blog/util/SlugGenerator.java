package com.chas.blog.util;

import java.text.Normalizer;
import java.util.Locale;

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
			throw new IllegalArgumentException("Title cannot be converted to a valid slug: " + title);
		}

		return normalized;
	}

}
