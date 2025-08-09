package com.r2dbc.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JwtTokenProvider {

    @Value("${jwt.secret-key}")
    private String secretKey;

    @Value("${jwt.expiration-time}")
    private long expirationTime;

    private SecretKey secret;

    @PostConstruct
    public void init() {
        // 시크릿 키를 Base64로 인코딩한 후 HMAC SHA 키로 변환
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.secret = Keys.hmacShaKeyFor(keyBytes);
    }

    // JWT 토큰 생성
    public String generateToken(Authentication authentication) {
        // 사용자 이름(Principal)을 토큰의 subject로 설정
        String subject = authentication.getName();
        Date now = new Date();
        Date expiration = new Date(now.getTime() + expirationTime);

        List<String> roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        return Jwts.builder()
                .setSubject(subject)        // 토큰 주체
                .setIssuedAt(now)           // 발행 시간
                .claim("roles", roles)   // roles 클레임에 권한 정보 추가
                .setExpiration(expiration)  // 만료 시간
                .signWith(secret, SignatureAlgorithm.HS256) // 서명 (시크릿 키)
                .compact();
    }

    // 토큰에서 사용자 이름 추출
    public String getUsernameFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secret)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    // 권한 가져오기
    public List<String> getRolesFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(secret)
                .build()
                .parseClaimsJws(token)
                .getBody();

        // claims에서 "roles"라는 키의 값을 List<String>으로 추출
        return (List<String>) claims.get("roles", List.class);
    }

    // 토큰 유효성 검증
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(secret).build().parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            // 토큰 유효성 검증 실패 시 예외 처리
            return false;
        }
    }
}