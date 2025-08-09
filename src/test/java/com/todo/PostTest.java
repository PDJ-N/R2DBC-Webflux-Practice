package com.todo;

import com.todo.post.domain.Post;
import com.todo.post.dto.reponse.UserWithPostsResponse;
import com.todo.post.dto.request.PostCreateRequest;
import com.todo.post.repository.PostRepository;
import com.todo.post.service.PostService;
import com.todo.user.domain.User;
import com.todo.user.dto.request.UserCreateRequest;
import com.todo.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.UUID;

import static org.mockito.Mockito.*;

public class PostTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private PostRepository postRepository;

    @InjectMocks
    private PostService postService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this); // @Mock 어노테이션 초기화
    }

    @Test
    void getUserWithPosts_shouldReturnUserWithTheirPosts() {
        // Given
        Long userId = 1L;
        String testName = UUID.randomUUID().toString();

        User user = User.toEntity(new UserCreateRequest("username", "password", testName, UUID.randomUUID().toString()));
        Post post1 = Post.toEntity(1L, new PostCreateRequest("첫 번째 글", "내용 1"));
        Post post2 = Post.toEntity(2L, new PostCreateRequest("두 번째 글", "내용 2"));

        when(userRepository.findById(userId)).thenReturn(Mono.just(user));
        when(postRepository.findByUserId(userId)).thenReturn(Flux.just(post1, post2));

        // When
        Mono<UserWithPostsResponse> result = postService.readByUserId(userId);

        // Then
        StepVerifier.create(result)
                .expectNextMatches(userWithPostsResponse ->
                        userWithPostsResponse.user() != null &&
                                testName.equals(userWithPostsResponse.user().getName()) &&
                                userWithPostsResponse.posts().size() == 2
                )
                .verifyComplete();

        verify(userRepository, times(1)).findById(userId);
        verify(postRepository, times(1)).findByUserId(userId);
    }
}