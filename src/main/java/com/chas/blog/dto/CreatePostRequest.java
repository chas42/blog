package com.chas.blog.dto;

/**
 * CreatePostRequest
 */
public record CreatePostRequest(
    Long authorId,
    TranslationRequest en,
    TranslationRequest pt
    
) {

}
