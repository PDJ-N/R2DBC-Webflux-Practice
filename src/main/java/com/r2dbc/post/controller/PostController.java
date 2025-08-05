package com.r2dbc.post.controller;

import com.r2dbc.post.dto.UserWithPosts;
import com.r2dbc.post.service.PostService;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/posts")
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping("/{id}")
    public Mono<UserWithPosts> getUserWithPosts(@PathVariable Long id) {
        return postService.getUserWithPosts(id);
    }
}