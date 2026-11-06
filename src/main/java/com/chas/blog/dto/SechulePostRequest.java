package com.chas.blog.dto;

import java.time.Instant;

import jakarta.validation.constraints.NotNull;

public record SechulePostRequest(@NotNull Instant publishAt) {

}
