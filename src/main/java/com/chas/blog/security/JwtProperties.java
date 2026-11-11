package com.chas.blog.security;

import java.time.Duration;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("blog.jwt")
public record JwtProperties(String secret, Duration expiration) {

}
