package com.todo.jwt;

import com.todo.exception.CustomException;
import com.todo.exception.dto.ErrorMessage;
import io.jsonwebtoken.*;
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

@Component
public class JwtTokenProvider {

    @Value("${jwt.secret-key}")
    private String secretKey;

    @Value("${jwt.expiration-time}")
    private long expirationTime;

    private SecretKey secret;
    private JwtParser parser;

    @PostConstruct
    public void init() {
        // 시크릿 키를 Base64로 인코딩한 후 HMAC SHA 키로 변환
        final byte[] keyBytes;
        try {
            keyBytes = Decoders.BASE64.decode(secretKey);
        } catch (IllegalArgumentException e) {
            throw new CustomException(ErrorMessage.INTERNAL_SERVER_ERROR, "jwt.secret-key는 Base64 인코딩된 문자열이어야 합니다.");
        }
        if (keyBytes.length < 32) { // 256bit
            throw new CustomException(ErrorMessage.INTERNAL_SERVER_ERROR, "jwt.secret-key는 HS256에 적합한 최소 256비트 이상이어야 합니다.");
        }
        this.secret = Keys.hmacShaKeyFor(keyBytes);
        this.parser = Jwts.parserBuilder()
                .setSigningKey(secret)
                .setAllowedClockSkewSeconds(30) // 시계 오차 허용
                .build();
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
        return parser
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    // 권한 가져오기
    public List<String> getRolesFromToken(String token) {
        Claims claims = parser.parseClaimsJws(token).getBody();

        List<?> roles = claims.get("roles", List.class);
        return roles == null ? List.of() : roles.stream().map(String::valueOf).toList();
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