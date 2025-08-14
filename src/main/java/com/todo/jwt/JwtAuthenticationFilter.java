package com.todo.jwt;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter implements WebFilter {

    private final JwtTokenProvider tokenProvider;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String token = resolveToken(exchange.getRequest());
        if (token != null && tokenProvider.validateToken(token)) {
            // 토큰이 유효하면 인증 정보 생성
            String username = tokenProvider.getUsernameFromToken(token);
            List<GrantedAuthority> authorities = getAuthorities(token);
            Authentication authentication = generateAuthentication(username, authorities);

            log.debug("JWT authentication for user: {}, authorities: {}", username, authorities);

            // SecurityContext에 인증 정보 저장
            return chain.filter(exchange).contextWrite(
                    ReactiveSecurityContextHolder.withAuthentication(authentication)
            );
        }
        return chain.filter(exchange);
    }

    /**
     * HTTP 요청 헤더에서 JWT 토큰 추출해주는 메소드.
     * */
    private String resolveToken(ServerHttpRequest request) {
        String bearerToken = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (StringUtils.hasText(bearerToken) && StringUtils.startsWithIgnoreCase(bearerToken, "Bearer ")) {
            return bearerToken.substring(7).trim();
        }
        return null;
    }

    /**
     * JWT 토큰의 roles 항목에서 권한들을 가져오는 메소드.
     *
     * @param token 권한을 가져올 JWT 토큰
     * */
    private List<GrantedAuthority> getAuthorities(String token) {
        List<String> roles = tokenProvider.getRolesFromToken(token);
        if (roles == null) roles = List.of();

        return roles.stream()
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    /**
     * Authentication을 만드는 메소드.
     *
     * @param username      JWT 토큰에 저장된 subject
     * @param authorities   JWT 토큰에 저장된 roles
     * */
    private Authentication generateAuthentication(String username, List<GrantedAuthority> authorities) {
        UserDetails userDetails = new User(
                username,   // username
                "",         // password (JWT 인증에서는 필요 없으므로 빈 문자열)
                authorities // authorities
        );

        return new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                authorities
        );
    }
}