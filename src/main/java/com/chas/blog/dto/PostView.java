package com.chas.blog.dto;

import com.chas.blog.model.post.Language;

/**
 * PostView
 */
public record PostView(
    String title,
    String summary,
    String content,
    String slug,
    Language language
) {

}
