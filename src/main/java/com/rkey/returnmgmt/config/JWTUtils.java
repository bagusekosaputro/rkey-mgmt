package com.rkey.returnmgmt.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rkey.returnmgmt.view.request.PendingReturnRequest;
import io.jsonwebtoken.*;
import io.jsonwebtoken.lang.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JWTUtils implements Serializable {
    private static final Logger log = LoggerFactory.getLogger(JWTUtils.class);
    private static final long serialVersionUID = -2550185165626007488L;
    public static final long JWT_TOKEN_VALIDITY = 24 * 60 * 60;

    @Value("${jwt.secret}")
    private String secret;

    public String generateToken(PendingReturnRequest body) {
        Map<String, String> order = new HashMap<>();
        order.put("orderId", body.getOrderId());
        order.put("emailAddress", body.getEmailAddress());
        return Jwts.builder().
                setSubject(body.getEmailAddress()).
                claim("body", order).
                setIssuedAt(new Date(System.currentTimeMillis())).
                setExpiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY * 1000)).
                signWith(SignatureAlgorithm.HS512, secret.getBytes()).
                compact();
    }

    public boolean validateToken(String token, String orderId, String emailAddress) {
        try {
            Jwts.parser().setSigningKey(secret.getBytes()).parseClaimsJws(token);
            Claims claims = getAllClaims(token);
            if(!claims.getSubject().equals(emailAddress)) {
                return false;
            }
            log.info("Validate token subject: {}", getAllClaims(token));
            return true;
        } catch (SignatureException e) {
            log.error("Invalid JWT Signature: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            log.error("Invalid JWT Token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            log.error("JWT Token has expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.error("JWT Token  is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("JWT claims string is empty: {}", e.getMessage());
        }
        return false;
    }

    public String parseJWT(String token) {
        if (token.startsWith("Bearer ")){
            return token.substring(7);
        }
        return null;
    }

    public <T> T getClaim(String token, Function<Claims, T> resolver) {
        final Claims claims = getAllClaims(token);
        return resolver.apply(claims);
    }

    private Claims getAllClaims(String token) {
        ObjectMapper mapper = new ObjectMapper();
        return Jwts
                .parserBuilder()
//                .deserializeJsonWith(new JacksonDeserializer(Maps.of("body", PendingReturnRequest.class).build()))
                .setSigningKey(secret.getBytes())
                .build()
                .parseClaimsJws(token)
                .getBody();

    }
}
