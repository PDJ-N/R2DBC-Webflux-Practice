package com.r2dbc.post.repository;

import com.r2dbc.post.domain.Post;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface PostRepository extends ReactiveCrudRepository<Post, Long> {
    Flux<Post> findByUserId(Long userId);
    Flux<Post> findAllByUserId(Long userId);
    Mono<Void> deleteByUserId(Long userId);
}