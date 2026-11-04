package com.chas.blog.model.post;

import com.fasterxml.jackson.annotation.JsonValue;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor 
@Getter
public enum Language {
	EN("en"), PT("pt");

	private final String code;

    public static Language fromCode(String code) {
		for (Language language : values()) {
			if (language.code.equals(code)) {
				return language;
			}
		}
		throw new IllegalArgumentException("Unknown language code: " + code);
	}

	@JsonValue
	public String getCode() {
		return code;
	}

}