package com.chas.blog.config;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.chas.blog.exceptions.PostNotFoundException;

@RestControllerAdvice 
public class ApiExceptionHandler {

    @ExceptionHandler(PostNotFoundException.class)
	ProblemDetail handlePostNotFound(PostNotFoundException ex) {
		ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
		problem.setTitle("Post not found");
		return problem;
	}

}
