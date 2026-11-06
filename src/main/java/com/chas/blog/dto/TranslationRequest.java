package com.chas.blog.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * TranslationRequest
 */
public record TranslationRequest(
        @NotBlank @Size(max = 255) String title,
        @Size(max = 500) String summary,
        @NotNull String content) {
}
