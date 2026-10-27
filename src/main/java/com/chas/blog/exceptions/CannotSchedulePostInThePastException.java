package com.chas.blog.exceptions;

public class CannotSchedulePostInThePastException extends RuntimeException {
    public CannotSchedulePostInThePastException() {
        super("Cannot schedule a post in the past");
    }

}
