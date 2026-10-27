package com.chas.blog.exceptions;

/**
 * CannotSchedulePublishedPostException
 */
public class CannotSchedulePublishedPostException extends RuntimeException {
    public CannotSchedulePublishedPostException() {
        super("Cannot schedule a post that is already published");
    }

}
