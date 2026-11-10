package com.chas.blog.exceptions;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.chas.blog.exceptions.post.CannotPublishWithoutTranslationException;
import com.chas.blog.exceptions.post.CannotSchedulePostInThePastException;
import com.chas.blog.exceptions.post.CannotSchedulePublishedPostException;
import com.chas.blog.exceptions.post.InvalidTitleException;
import com.chas.blog.exceptions.post.PostNotFoundException;
import com.chas.blog.exceptions.post.SlugAlreadyExistsException;

@RestControllerAdvice 
public class ApiExceptionHandler {

    @ExceptionHandler(PostNotFoundException.class)
	ProblemDetail handlePostNotFound(PostNotFoundException ex) {
		ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
		problem.setTitle("Post not found");
		return problem;
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	ProblemDetail handleValidationException(MethodArgumentNotValidException ex) {
		ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
		problem.setTitle("Validation failed");
		problem.setProperty("errors", ex.getBindingResult().getFieldErrors().stream()
			.map(e -> Map.of("field", e.getField(), "message", e.getDefaultMessage()))
			.toList()
		);
		return problem;
	}

	@ExceptionHandler(CannotPublishWithoutTranslationException.class)
	ProblemDetail handleCannotPublishWithoutTranslation(CannotPublishWithoutTranslationException ex) {
		ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.UNPROCESSABLE_CONTENT, ex.getMessage());
		problem.setTitle("Cannot publish post without translation");
		return problem;	
	}

	@ExceptionHandler(CannotSchedulePublishedPostException.class)
	ProblemDetail handleCannotPublishedPost(CannotSchedulePublishedPostException ex) {
		ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
		problem.setTitle("Cannot publish post");
		return problem;
	}

	@ExceptionHandler(CannotSchedulePostInThePastException.class)
	ProblemDetail handleCannotSchedulePostInThePast(CannotSchedulePostInThePastException ex) {
		ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.UNPROCESSABLE_CONTENT, ex.getMessage());
		problem.setTitle("Cannot schedule post in the past");
		return problem;
	}

	@ExceptionHandler(SlugAlreadyExistsException.class)
	ProblemDetail handleSlugAlreadyExists(SlugAlreadyExistsException ex) {
		ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
		problem.setTitle("Slug already exists");
		return problem;
	}

	@ExceptionHandler(InvalidTitleException.class)
	ProblemDetail handleInvalidTitle(InvalidTitleException ex) {
		ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
		problem.setTitle("Invalid title");
		return problem;
	}


}
