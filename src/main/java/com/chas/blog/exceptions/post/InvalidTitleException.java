package com.chas.blog.exceptions.post;

public class InvalidTitleException extends RuntimeException {

    public InvalidTitleException(String message) {
        super(message);
    }

}
