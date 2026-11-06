package com.chas.blog.exceptions;

/**
 * SlugAlreadyExistsException
 */
public class SlugAlreadyExistsException extends RuntimeException {

    public SlugAlreadyExistsException() {
        super("Slug already exists");
    }

}
