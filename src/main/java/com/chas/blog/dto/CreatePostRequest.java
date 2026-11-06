package com.chas.blog.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

/**
 * CreatePostRequest
 */
public record CreatePostRequest(
        @NotNull @Valid TranslationRequest en,
        @NotNull @Valid TranslationRequest pt) {
}
