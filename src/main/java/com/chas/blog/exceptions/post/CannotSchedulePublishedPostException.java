package com.chas.blog.exceptions.post;

/**
 * CannotSchedulePublishedPostException
 */
public class CannotSchedulePublishedPostException extends RuntimeException {
    public CannotSchedulePublishedPostException() {
        super("Cannot schedule a post that is already published");
    }

}
