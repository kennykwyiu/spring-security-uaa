package com.kenny.uaa.util;

import com.kenny.uaa.config.AppProperties;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.MalformedKeyException;
import io.jsonwebtoken.security.SignatureException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class JwtUtil {
    // Key for signing Access Token
    public static final Key signKey = Jwts.SIG.HS512.key().build();

    // Key for signing Refresh Token
    public static final Key refreshKey = Jwts.SIG.HS512.key().build();

    private final AppProperties appProperties;

    public String createAccessToken(UserDetails userDetails) {
        return createJWTToken(userDetails,
                appProperties.getJwt().getAccessTokenExpireTime(),
                signKey);
    }

    public String createJWTToken(UserDetails userDetails,
                                 long timeToExpire,
                                 Key key) {
        long now = System.currentTimeMillis();
        return Jwts
                .builder()
                .id("kenny")
                .subject(userDetails.getUsername())
                .claim("authorities",
                        userDetails.getAuthorities().stream()
                                .map(grantedAuthority -> grantedAuthority.getAuthority())
                                .collect(Collectors.toList()))
                .expiration(new Date(now + timeToExpire))
                .issuedAt(new Date(now))
                .signWith(key)
                .compact();
    }

    public String createRefreshToken(UserDetails userDetails) {
        return createJWTToken(userDetails,
                appProperties.getJwt().getRefreshTokenExpireTime(),
                refreshKey);
    }

    public String createAccessTokenWithRefreshToken(String token) {
        return parseClaims(token, refreshKey)
                .map(claims -> Jwts
                        .builder()
                        .claims(claims)
                        .expiration(new Date(System.currentTimeMillis() + appProperties.getJwt().getAccessTokenExpireTime()))
                        .issuedAt(new Date())
                        .signWith(signKey)
                        .compact()
                ).orElseThrow(() -> new AccessDeniedException("Access denied"));

    }

    public Optional<Claims> parseClaims(String jwtToken, Key key) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith((SecretKey) key)
                    .build()
                    .parseSignedClaims(jwtToken)
                    .getPayload();
            return Optional.of(claims);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public boolean validateAccessToken(String jwtToken) {
        return validateToken(jwtToken, signKey, true);
    }
    public boolean validateAccessTokenWithoutExpiration(String jwtToken) {
        return validateToken(jwtToken, signKey, false);
    }

    public boolean validateRefreshToken(String jwtToken) {
        return validateToken(jwtToken, refreshKey, true);
    }

    private boolean validateToken(String jwtToken, Key key, boolean isExpiredInvalid) {
        try {
            Jwts.parser()
                    .verifyWith((SecretKey) key)
                    .build()
                    .parseSignedClaims(jwtToken)
                    .getPayload();
            return true;
        } catch (ExpiredJwtException | SignatureException | MalformedKeyException | UnsupportedJwtException |
                 IllegalArgumentException e) {
            if (e instanceof ExpiredJwtException) {
                return !isExpiredInvalid;
            }
            return false;
        }
    }
}


