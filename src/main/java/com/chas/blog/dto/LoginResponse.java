package com.chas.blog.dto;

import java.time.Duration;

public record LoginResponse(String token, String tokenType, long expiresInSeconds) {

    public static LoginResponse bearer(String token, Duration expiresIn) {
        return new LoginResponse(token, "Bearer", expiresIn.toSeconds());
    }

}
