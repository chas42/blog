package com.chas.blog.dto;

/**
 * TranslationRequest
 */
public record TranslationRequest(
    String title,
    String summary,
    String content
) {

}
