package com.chas.blog.dto;

import java.time.Instant;

/**
 * PostSummary
 */
public record PostSummary(
    String slug,
    String title,
    String summary,
    Instant publishedAt
) {

}
