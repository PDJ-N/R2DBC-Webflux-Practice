package com.todo.post.service;

import com.todo.post.domain.Post;
import com.todo.post.dto.response.UserWithPostsResponse;
import com.todo.post.dto.request.PostCreateRequest;
import com.todo.post.dto.request.PostUpdateRequest;
import com.todo.post.repository.PostRepository;
import com.todo.user.domain.User;
import com.todo.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
import org.springframework.data.relational.core.query.Update;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {

    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final R2dbcEntityTemplate template;

    public Mono<Post> create(Long userId, PostCreateRequest postCreateRequest) {
        return postRepository.save(Post.toEntity(userId, postCreateRequest));
    }

    public Flux<Post> readAll() {
        return postRepository.findAll();
    }

    public Mono<Post> readByPostId(Long postId) {
        return postRepository.findById(postId);
    }

    public Mono<UserWithPostsResponse> readByUserId(Long userId) {
        Mono<User> userMono = userRepository.findById(userId);
        Mono<List<Post>> postsMono = postRepository.findByUserId(userId).collectList();

        return Mono.zip(userMono, postsMono)
                .map(tuple -> new UserWithPostsResponse(tuple.getT1(), tuple.getT2()));
    }

    public Flux<Post> readAll(Long userId) {
        return postRepository.findAllByUserId(userId);
    }

    public Mono<Long> updateByTemplate(Long postId, PostUpdateRequest request) {
        Update update = Update
                .update("title", request.title() == null ? "제목이 없습니다" : request.title())
                .set("content", request.content() == null ? "내용이 없습니다" : request.content());
        return template.update(
                Query.query(Criteria.where("id").is(String.valueOf(postId))),
                update,
                Post.class
        );
    }

    public Mono<Post> updateByRepository(Long postId, PostUpdateRequest request) {
        return postRepository.findById(postId).flatMap(post -> {
            post.setTitle(request.title() == null ? "제목이 없습니다" : request.title());
            post.setContent(request.content() == null ? "내용이 없습니다" : request.content());
            return postRepository.save(post);
        });
    }

    public Mono<Void> deleteByPostId(Long postId) {
        return postRepository.deleteById(postId);
    }

    public Mono<Void> deleteByUserId(Long userId) {
        return postRepository.deleteByUserId(userId);
    }
}