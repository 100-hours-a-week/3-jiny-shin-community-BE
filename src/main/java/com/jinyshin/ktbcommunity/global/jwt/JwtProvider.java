package com.jinyshin.ktbcommunity.global.jwt;

import static com.jinyshin.ktbcommunity.global.constants.JwtConstants.ACCESS_TOKEN_EXPIRATION;
import static com.jinyshin.ktbcommunity.global.constants.JwtConstants.REFRESH_TOKEN_EXPIRATION;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtProvider {

  private final Key key;

  public JwtProvider(@Value("${jwt.secret}") String secret) {
    this.key = Keys.hmacShaKeyFor(
        Base64.getEncoder().encode(secret.getBytes(StandardCharsets.UTF_8))
    );
  }

  public String createAccessToken(Long userId, String role) {
    return Jwts.builder()
        .subject(String.valueOf(userId))
        .claim("role", role)
        .issuedAt(new Date())
        .expiration(Date.from(Instant.now().plusSeconds(ACCESS_TOKEN_EXPIRATION)))
        .signWith(key)
        .compact();
  }

  public String createRefreshToken(Long userId) {
    return Jwts.builder()
        .subject(String.valueOf(userId))
        .issuedAt(new Date())
        .expiration(Date.from(Instant.now().plusSeconds(REFRESH_TOKEN_EXPIRATION)))
        .signWith(key)
        .compact();
  }

  public Jws<Claims> parse(String jwt) {
    return Jwts.parser()
        .verifyWith((javax.crypto.SecretKey) key)
        .build()
        .parseSignedClaims(jwt);
  }
}