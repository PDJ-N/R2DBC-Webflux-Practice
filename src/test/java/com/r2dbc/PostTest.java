package com.r2dbc;

import com.r2dbc.post.domain.Post;
import com.r2dbc.post.dto.UserWithPosts;
import com.r2dbc.post.repository.PostRepository;
import com.r2dbc.post.service.PostService;
import com.r2dbc.user.domain.User;
import com.r2dbc.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

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

        User user = new User(null, "홍길동", "example@naver.com");
        Post post1 = new Post(1L, "첫 글", userId);
        Post post2 = new Post(2L, "두 번째 글", userId);

        when(userRepository.findById(userId)).thenReturn(Mono.just(user));
        when(postRepository.findByUserId(userId)).thenReturn(Flux.just(post1, post2));

        // When
        Mono<UserWithPosts> result = postService.getUserWithPosts(userId);

        // Then
        StepVerifier.create(result)
                .expectNextMatches(userWithPosts ->
                        userWithPosts.user() != null &&
                                "홍길동".equals(userWithPosts.user().getName()) &&
                                userWithPosts.posts().size() == 2
                )
                .verifyComplete();

        verify(userRepository, times(1)).findById(userId);
        verify(postRepository, times(1)).findByUserId(userId);
    }
}