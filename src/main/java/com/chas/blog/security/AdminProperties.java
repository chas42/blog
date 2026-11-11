package com.chas.blog.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("blog.admin")
public record AdminProperties(String username, String password) {

}
