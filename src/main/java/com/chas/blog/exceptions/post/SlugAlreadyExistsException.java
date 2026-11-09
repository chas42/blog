package com.chas.blog.exceptions.post;

/**
 * SlugAlreadyExistsException
 */
public class SlugAlreadyExistsException extends RuntimeException {

    public SlugAlreadyExistsException() {
        super("Slug already exists");
    }

}
