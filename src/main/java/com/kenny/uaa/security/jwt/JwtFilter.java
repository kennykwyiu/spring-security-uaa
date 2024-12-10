package com.kenny.uaa.security.jwt;

import com.kenny.uaa.config.AppProperties;
import com.kenny.uaa.util.CollectionUtil;
import com.kenny.uaa.util.JwtUtil;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.MalformedKeyException;
import io.jsonwebtoken.security.SignatureException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.crypto.SecretKey;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
public class JwtFilter extends OncePerRequestFilter {

    private final AppProperties appProperties;
    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest,
                                    HttpServletResponse httpServletResponse,
                                    FilterChain filterChain) throws ServletException, IOException {
        if (checkJwtToken(httpServletRequest)) {
            validateToken(httpServletRequest)
                    .filter(claims -> claims.get("authorities") != null)
                    .ifPresentOrElse(
                            claims -> {
                                setupSpringAuthentication(claims);
                            },
                            () -> {
                                SecurityContextHolder.clearContext();
                            }
                    );
        }
        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }

    private void setupSpringAuthentication(Claims claims) {
        List<?> rawList = CollectionUtil.convertObjectToList(claims.get("authorities"));
        List<SimpleGrantedAuthority> authorities = rawList.stream()
                .map(obj -> String.valueOf(obj))
                .map(role -> new SimpleGrantedAuthority(role))
                .collect(Collectors.toList());
        UsernamePasswordAuthenticationToken authentication
                = new UsernamePasswordAuthenticationToken(claims.getSubject(),
                                                        null,
                                                        authorities);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private Optional<Claims> validateToken(HttpServletRequest httpServletRequest) {
        String jwtToken = httpServletRequest.getHeader(appProperties.getJwt().getHeader()).replace(appProperties.getJwt().getPrefix(), "");
        try {
            return Optional.of(
                    Jwts.parser()
                            .verifyWith((SecretKey) JwtUtil.signKey)
                            .build()
                            .parseSignedClaims(jwtToken)
                            .getPayload()
            );
        } catch (ExpiredJwtException | SignatureException | MalformedKeyException | UnsupportedJwtException | IllegalArgumentException e) {
            return Optional.empty();
        }
    }

    private boolean checkJwtToken(HttpServletRequest httpServletRequest) {
        String authenticationHeader = httpServletRequest.getHeader(appProperties.getJwt().getHeader());
        return authenticationHeader != null
                && authenticationHeader.startsWith(appProperties.getJwt().getPrefix());
    }
}
