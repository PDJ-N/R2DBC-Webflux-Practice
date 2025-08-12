package com.todo.config;

import com.todo.post.domain.Post;
import com.todo.post.dto.request.PostCreateRequest;
import com.todo.post.repository.PostRepository;
import com.todo.user.domain.User;
import com.todo.user.dto.request.UserCreateRequest;
import com.todo.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import reactor.core.publisher.Mono;

@Configuration
@RequiredArgsConstructor
public class DatabaseInitConfig {
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    /**
     * 테스트용 데이터를 만드는 ApplicationRunner. 1L로 둔 이유는 자동으로 생성되는 PK는 처음에는 무조건 1이기 때문이다.
     */
    @Bean
    @Order(2)
    public ApplicationRunner databaseInit() {
        return args -> {
            userRepository.save(User.toEntity(new UserCreateRequest("test", "test", "홍길동", "hong@example.com"))).subscribe();
            postRepository.save(Post.toEntity(1L, new PostCreateRequest("제목1", "내용1"))).subscribe();
            postRepository.save(Post.toEntity(1L, new PostCreateRequest("제목2", "내용2"))).subscribe();
        };
    }
}