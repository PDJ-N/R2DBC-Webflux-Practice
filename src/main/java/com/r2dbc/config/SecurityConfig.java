package com.r2dbc.config;

import com.r2dbc.jwt.JwtAuthenticationFilter;
import com.r2dbc.jwt.JwtTokenProvider;
import com.r2dbc.user.service.ReactiveUserDetailsServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtTokenProvider tokenProvider;
    private final ReactiveUserDetailsServiceImpl reactiveUserDetailsService;

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers(
                        "/v3/api-docs/**",
                        "/swagger-ui/**",
                        "/swagger-ui.html",
                        "/webjars/swagger-ui/**"
                        ).permitAll()
                        .pathMatchers("/api/auth/login", "/users/**", "/posts/**").permitAll()
                        .anyExchange().authenticated()
                )
                .addFilterAt(new JwtAuthenticationFilter(tokenProvider), SecurityWebFiltersOrder.AUTHENTICATION)
                .build();
    }

    // (예시) 사용자 정보가 메모리에 있는 경우의 ReactiveAuthenticationManager 빈
    // ReactiveAuthenticationManager 설정
    @Bean
    public ReactiveAuthenticationManager authenticationManager() {
        UserDetailsRepositoryReactiveAuthenticationManager authenticationManager =
                new UserDetailsRepositoryReactiveAuthenticationManager(reactiveUserDetailsService);
        authenticationManager.setPasswordEncoder(passwordEncoder());
        return authenticationManager;
    }

    // BCryptPasswordEncoder 사용
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}