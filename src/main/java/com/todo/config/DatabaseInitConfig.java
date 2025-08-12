package com.todo.config;

import com.todo.post.domain.Post;
import com.todo.post.dto.request.PostCreateRequest;
import com.todo.post.repository.PostRepository;
import com.todo.user.domain.User;
import com.todo.user.dto.request.UserCreateRequest;
import com.todo.user.repository.UserRepository;
import io.r2dbc.spi.ConnectionFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.r2dbc.connection.R2dbcTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import reactor.core.publisher.Flux;

@Slf4j
@Configuration
@RequiredArgsConstructor
@EnableTransactionManagement  // @Transactional 애노테이션이 원자성과 일관성을 유지하게 만들기 위한 애노테이션
public class DatabaseInitConfig {
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    @Bean
    @Order(2)
    public ApplicationRunner databaseInit() {
        return args -> {
            userRepository.save(User.toEntity(new UserCreateRequest("test", "test", "홍길동", "hong@example.com")))
                    .flatMap(saved ->
                            postRepository.saveAll(
                                    Flux.just(
                                            Post.toEntity(saved.getId(), new PostCreateRequest("제목1", "내용1")),
                                            Post.toEntity(saved.getId(), new PostCreateRequest("제목2", "내용2"))
                                    )
                            ).then()
                    )
                    .doOnSuccess(v -> log.info("개발용 시드 데이터 삽입 완료"))
                    .doOnError(e -> log.error("개발용 시드 데이터 삽입 실패", e))
                    .block(); // 개발 초기화 목적이므로 완료까지 대기
        };
    }

    /**
     * {@code @Transactional} 애노테이션이 원자성과 일관성을 보장하게 하기 위해서 생성하는 빈
     * */
    @Bean
    public R2dbcTransactionManager transactionManager(ConnectionFactory connectionFactory) {
        return new R2dbcTransactionManager(connectionFactory);
    }
}