package com.todo.controller;

import com.todo.post.domain.Post;
import com.todo.post.dto.response.UserWithPostsResponse;
import com.todo.post.dto.request.PostCreateRequest;
import com.todo.post.dto.request.PostUpdateRequest;
import com.todo.post.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;

    @PostMapping
    public Mono<Post> create(@RequestBody PostCreateRequest request, @RequestParam Long userId) {
        return postService.create(userId, request);
    }

    @GetMapping("/users")
    public Mono<UserWithPostsResponse> readPostsByUser(@RequestParam Long userId) {
        return postService.readByUserId(userId);
    }

    @GetMapping
    public Flux<Post> readAll() {
        return postService.readAll();
    }

    @GetMapping("/{postId}")
    public Mono<Post> readPost(@PathVariable Long postId) {
        return postService.readByPostId(postId);
    }

    @PatchMapping("/repository/{postId}")
    public Mono<Post> updateByRepository(@PathVariable Long postId, @RequestBody PostUpdateRequest request) {
        return postService.updateByRepository(postId, request);
    }

    @PatchMapping("/template/{postId}")
    public Mono<Long> updateByTemplate(@PathVariable Long postId, @RequestBody PostUpdateRequest request) {
        return postService.updateByTemplate(postId, request);
    }

    @DeleteMapping("/user")
    public Mono<Void> deleteByUserId(@RequestParam Long userId) {
        return postService.deleteByUserId(userId);
    }
}