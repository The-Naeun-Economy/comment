package com.repick.comment.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class TokenProvider {
    private static final Logger logger = LoggerFactory.getLogger(TokenProvider.class);

    @Value("${jwt.secret-key}")
    private String jwtSecretKey;

    // 토큰 유효성 검증
    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(jwtSecretKey).parseClaimsJws(token);
            return true;
        } catch (SignatureException e) {
            logger.error("Invalid JWT signature", e);
        } catch (ExpiredJwtException e) {
            logger.warn("Expired JWT token", e);
        } catch (MalformedJwtException e) {
            logger.error("Malformed JWT token", e);
        } catch (Exception e) {
            logger.error("Invalid JWT token", e);
        }
        return false;
    }

    // 토큰에서 userId 추출
    public Long getUserIdFromToken(String bearerToken) {
        String token = extractJwtFromRequest(bearerToken);
        Claims claims = parseClaims(token);
        return claims.get("userId", Long.class);
    }

    // 토큰에서 nickName 추출
    public String getNickNameFromToken(String bearerToken) {
        String token = extractJwtFromRequest(bearerToken);
        Claims claims = parseClaims(token);
        return claims.get("nickName", String.class);
    }

    // 클레임 추출 메서드
    private Claims parseClaims(String token) {
        try {
            return Jwts.parser().setSigningKey(jwtSecretKey).parseClaimsJws(token).getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }

    private String extractJwtFromRequest(String bearerToken) {
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}