package com.chas.blog.exceptions;

public class CannotPublishWithoutTranslationException extends IllegalArgumentException {
    public CannotPublishWithoutTranslationException(String language) {
        super("Cannot publish a post without translations in " + language);
    }
}
