package com.chas.blog.service;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtValidationException;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

import com.chas.blog.security.JwtProperties;
import com.chas.blog.security.TokenService;
import com.nimbusds.jose.jwk.source.ImmutableSecret;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class TokenServiceTest {

    private static final Instant NOW = Instant.parse("2026-11-10T18:53:00Z");

    private static final String SECRET = "c2VjcmV0MTIzNDU2Nzg5MDEyMzQ1Njc4OTAxMjM0NTY3ODkwMTI=";

    SecretKey key = new SecretKeySpec(java.util.Base64.getDecoder().decode(SECRET), "HmacSHA256");
    NimbusJwtDecoder decoder = NimbusJwtDecoder.withSecretKey(key).macAlgorithm(MacAlgorithm.HS256).build();

    TokenService service(Instant now) {
        return new TokenService(
                new NimbusJwtEncoder(new ImmutableSecret<>(key)),
                new JwtProperties(SECRET, Duration.ofHours(1)),
                Clock.fixed(now, java.time.ZoneOffset.UTC));
    }

    Authentication admin = UsernamePasswordAuthenticationToken.authenticated("admin", "password",
            List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));

    @Test
    void generatesTokenWithSubkectRolesAndExpiration() {
        Jwt jwt = decoder.decode(service(Instant.now()).generate(admin));

        assertThat(jwt.getSubject()).isEqualTo("admin");
        assertThat(jwt.getClaimAsStringList("roles")).containsExactly("ADMIN");
        assertThat(jwt.getExpiresAt()).isEqualTo(jwt.getIssuedAt().plus(1, ChronoUnit.HOURS));
    }

    @Test
    void expiredTokenIsRejected() {
        String token = service(NOW.minus(2, ChronoUnit.HOURS)).generate(admin);

        assertThatThrownBy(() -> decoder.decode(token))
                .isInstanceOf(JwtValidationException.class)
                .hasMessageContaining("expired");
    }

}
