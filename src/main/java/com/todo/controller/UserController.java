package com.todo.controller;

import com.todo.user.dto.request.UserCreateRequest;
import com.todo.user.dto.request.UserUpdateRequest;
import com.todo.user.service.UserService;
import com.todo.user.domain.User;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @Operation(summary = "사용자 조회")
    @GetMapping("/{userId}")
    public Mono<User> read(@PathVariable Long userId) {
        return userService.read(userId);
    }

    @Operation(summary = "사용자 전부 조회")
    @GetMapping("/all")
    public Flux<User> readAll() {
        return userService.readAll();
    }

    @Operation(summary = "사용자 생성")
    @PostMapping
    public Mono<User> create(@RequestBody UserCreateRequest user) {
        return userService.create(user);
    }

    @Operation(summary = "이메일로 사용자 조회")
    @GetMapping("/email/{email}")
    public Mono<User> readByEmail(@PathVariable String email) {
        return userService.getUserByEmail(email);
    }

    @Operation(summary = "R2DBCTemplate를 사용하여 사용자 수정")
    @PatchMapping("/template/{userId}")
    public Mono<Long> updateUserByTemplate(@PathVariable Long userId, @RequestBody UserUpdateRequest request) {
        return userService.updateUsingTemplate(userId, request);
    }

    @Operation(summary = "Repository를 사용하여 사용자 수정")
    @PatchMapping("/repository/{userId}")
    public Mono<User> updateUserByRepository(@PathVariable Long userId, @RequestBody UserUpdateRequest request) {
        return userService.updateUsingRepository(userId, request);
    }

    @Operation(summary = "Repository를 사용하여 사용자 삭제")
    @DeleteMapping("/repository/{userId}")
    public Mono<Void> deleteByRepository(@PathVariable Long userId) {
        return userService.deleteUsingRepository(userId);
    }

    @Operation(summary = "R2DBCTemplate를 사용하여 사용자 삭제")
    @DeleteMapping("/template/{userId}")
    public Mono<Long> deleteByTemplate(@PathVariable Long userId) {
        return userService.deleteUsingTemplate(userId);
    }
}