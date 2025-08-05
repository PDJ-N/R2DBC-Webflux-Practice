package com.r2dbc;

import com.r2dbc.domain.User;
import com.r2dbc.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureWebTestClient
public class UserCrudTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setup() {
        // 테스트 전 DB 초기화
        userRepository.deleteAll().block();
    }

    @Test
    void testCreateAndGetUser() {
        User newUser = new User(null, "홍길동", "hong@example.com");

        // 1. 사용자 등록
        webTestClient.post()
                .uri("/users")
                .body(Mono.just(newUser), User.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody(User.class)
                .value(user -> {
                    assertThat(user.getId()).isNotNull();
                    assertThat(user.getName()).isEqualTo("홍길동");
                    assertThat(user.getEmail()).isEqualTo("hong@example.com");
                    System.out.println(user);
                });

        // 2. 사용자 전체 조회
        webTestClient.get()
                .uri("/users")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(User.class)
                .hasSize(1)
                .value(System.out::println);

        // 3. 이메일로 사용자 조회
        webTestClient.get()
                .uri("/users/hong@example.com")
                .exchange()
                .expectStatus().isOk()
                .expectBody(User.class)
                .value(user -> {
                    assertThat(user.getEmail()).isEqualTo("hong@example.com");
                    System.out.println(user);
                });
    }
}
