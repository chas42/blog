package com.chas.blog.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SchedulePostRequest(
    @NotNull @NotBlank String when) {

}
