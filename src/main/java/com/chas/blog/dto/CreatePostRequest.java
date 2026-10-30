package com.chas.blog.dto;

import com.chas.blog.model.post.Language;

/**
 * CreatePostRequest
 */
public record CreatePostRequest(
    Long authorId,
    TranslationRequest en,
    TranslationRequest pt
    
) {

}
