package com.chas.blog.dto;

import java.time.Instant;
import java.util.Map;

import com.chas.blog.model.post.Language;

/**
 * PostView
 */
public record PostView(
    String slug,
    String title,
    String summary,
    String content,
    Language language,
    Instant publishedAt,
    Map<Language, String> translations
) {

}
