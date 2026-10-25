package com.chas.blog.exceptions;

/**
 * PostNotFoundException
 */
public class PostNotFoundException extends RuntimeException {
    public PostNotFoundException(Long postId) {
        super("Post not found: " + postId);
    }
    public PostNotFoundException(String slug) {
        super("Post not found: " + slug);
    }

}
