package com.r2dbc.controller;

import com.r2dbc.jwt.JwtTokenProvider;
import com.r2dbc.user.dto.request.UserLoginRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final ReactiveAuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;

    @PostMapping("/login")
    public Mono<Map<String, String>> login(@RequestBody Mono<UserLoginRequest> userLoginDto) {
        return userLoginDto.flatMap(dto -> {
            UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(dto.username(), dto.password());
            
            return authenticationManager.authenticate(authenticationToken)
                .map(tokenProvider::generateToken)
                .map(jwt -> {
                    Map<String, String> response = new HashMap<>();
                    response.put("token", jwt);
                    return response;
                });
        });
    }

    @GetMapping("/me")
    public Mono<String> getCurrentUserRole(@AuthenticationPrincipal Mono<UserDetails> userDetailsMono) {
        return userDetailsMono
                .map(userDetails -> "Current user's roles: " + userDetails.getAuthorities());
    }

    @GetMapping("/me2")
    public Mono<String> getCurrentUserRole() {
        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .map(authentication -> "Current user's roles: " + authentication.getAuthorities());
    }
}