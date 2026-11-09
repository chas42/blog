package com.chas.blog.exceptions.post;

public class CannotPublishWithoutTranslationException extends IllegalArgumentException {
    public CannotPublishWithoutTranslationException(String language) {
        super("Cannot publish a post without translations in " + language);
    }
}
