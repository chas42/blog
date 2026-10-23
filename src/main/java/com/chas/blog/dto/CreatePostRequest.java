package com.chas.blog.dto;

import com.chas.blog.model.post.Language;

/**
 * CreatePostRequest
 */
public record CreatePostRequest(
    String title,
    String summary,
    String content,
    Long authorId,
    Language language
) {

}
