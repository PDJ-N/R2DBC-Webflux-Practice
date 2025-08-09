package com.todo.controller;

import com.todo.exception.CustomException;
import com.todo.exception.dto.ErrorMessage;
import com.todo.jwt.JwtTokenProvider;
import com.todo.user.dto.request.UserLoginRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
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
                    .map(jwt -> Map.of("token", jwt, "tokenType", "Bearer"))
                    .onErrorResume(
                            BadCredentialsException.class,
                            ex -> Mono.error(new CustomException(ErrorMessage.UNAUTHORIZED, "올바르지 않은 로그인입니다"))
                    );
        });
    }

    @GetMapping("/me")
    public Mono<Map<String, Object>> getCurrentUserRole(@AuthenticationPrincipal Mono<UserDetails> userDetailsMono) {
        return userDetailsMono
                .switchIfEmpty(Mono.error(new CustomException(ErrorMessage.UNAUTHORIZED, "올바르지 않은 인증입니다.")))
                .map(userDetails -> userDetails.getAuthorities().stream()
                        .map(org.springframework.security.core.GrantedAuthority::getAuthority)
                        .toList())
                .map(roles -> Map.of("roles", roles));
    }

    @GetMapping("/me2")
    public Mono<String> getCurrentUserRole() {
        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .map(authentication -> "Current user's roles: " + authentication.getAuthorities());
    }
}