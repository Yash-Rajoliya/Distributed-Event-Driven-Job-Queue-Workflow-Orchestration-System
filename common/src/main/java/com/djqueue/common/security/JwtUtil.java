package com.djqueue.common.security;

import io.jsonwebtoken.*;
import java.util.Date;

public class JwtUtil {

    private static final String SECRET = "secret";

    public static String generate(String user) {
        return Jwts.builder()
                .setSubject(user)
                .setExpiration(new Date(System.currentTimeMillis() + 86400000))
                .signWith(SignatureAlgorithm.HS256, SECRET)
                .compact();
    }

    public static String validate(String token) {
        return Jwts.parser()
                .setSigningKey(SECRET)
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }
}