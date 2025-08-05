package com.r2dbc.post.service;

import com.r2dbc.post.domain.Post;
import com.r2dbc.post.dto.UserWithPosts;
import com.r2dbc.post.repository.PostRepository;
import com.r2dbc.user.domain.User;
import com.r2dbc.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class PostService {

    private final UserRepository userRepository;
    private final PostRepository postRepository;

    public PostService(UserRepository userRepository, PostRepository postRepository) {
        this.userRepository = userRepository;
        this.postRepository = postRepository;
    }

    public Mono<UserWithPosts> getUserWithPosts(Long userId) {
        Mono<User> userMono = userRepository.findById(userId);
        Mono<List<Post>> postsMono = postRepository.findByUserId(userId).collectList();

        return Mono.zip(userMono, postsMono)
                .map(tuple -> new UserWithPosts(tuple.getT1(), tuple.getT2()));
    }
}