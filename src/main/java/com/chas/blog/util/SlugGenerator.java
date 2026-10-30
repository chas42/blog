package com.chas.blog.util;

import java.text.Normalizer;
import java.util.Locale;

public class SlugGenerator {

	private SlugGenerator() {}

    public static String fromTitle(String title) {
		// 1. Normalizer.normalize(title, Normalizer.Form.NFD)
		//    → separa "á" em "a" + acento
		// 2. replaceAll("\\p{M}", "")        → remove os acentos
		// 3. toLowerCase(Locale.ROOT)
		// 4. replaceAll("[^a-z0-9]+", "-")    → tudo que não é letra/número vira hífen
		// 5. remover hífens do começo e do fim

        return Normalizer.normalize(title, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("^-+", "")
				.replaceAll("-+$", "");

	}

}
