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

    
}


