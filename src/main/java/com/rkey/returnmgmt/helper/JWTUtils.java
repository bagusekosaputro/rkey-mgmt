package com.rkey.returnmgmt.helper;

import com.rkey.returnmgmt.model.Order;
import com.rkey.returnmgmt.view.request.PendingReturnRequest;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Component
public class JWTUtils implements Serializable {
    private static final long serialVersionUID = -2550185165626007488L;
    public static final long JWT_TOKEN_VALIDITY = 24 * 60 * 60;

    @Value("$jwt.secret")
    private String secret;

    public String generateToken(PendingReturnRequest body) {
        return Jwts.builder().
                setSubject(body.getEmailAddress()).
                claim("orderId", body.getOrderId()).
                setIssuedAt(new Date(System.currentTimeMillis())).
                setExpiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY * 1000)).
                signWith(SignatureAlgorithm.HS512, secret).
                compact();
    }

    public String getEmailAddressFromToken(String token) {
        return getClaim(token, Claims::getSubject);
    }

    public String getOrderIdFromToken(String token) {
        return getClaim(token, Claims::getAudience);
    }

    public <T> T getClaim(String token, Function<Claims, T> resolver) {
        final Claims claims = getAllClaims(token);
        return resolver.apply(claims);
    }

    private Claims getAllClaims(String token) {
        return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
    }
}
