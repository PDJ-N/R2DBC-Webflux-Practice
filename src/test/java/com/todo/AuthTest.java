package com.todo;

import com.todo.user.domain.User;
import com.todo.user.dto.request.UserCreateRequest;
import com.todo.user.dto.request.UserLoginRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.Map;

@SpringBootTest
@AutoConfigureWebTestClient // WebTestClient 빈 자동 구성
public class AuthTest {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void testAuthenticationWithJwtToken() {
        // 1. 로그인 요청
        String username = "user";
        String password = "password";

        UserLoginRequest loginRequest = new UserLoginRequest(username, password);

        webTestClient.post().uri("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(new UserCreateRequest(username, password, "테스트", "example@example.com")), UserCreateRequest.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody(User.class);

        webTestClient.post().uri("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(loginRequest), UserLoginRequest.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Map.class)
                .consumeWith(response -> {
                    // 2. 로그인 응답에서 JWT 토큰 추출
                    String jwtToken = (String) response.getResponseBody().get("token");

                    // 3. 발급받은 JWT 토큰을 사용하여 인증된 엔드포인트 호출
                    webTestClient.get().uri("/api/auth/me")
                            .header("Authorization", "Bearer " + jwtToken)
                            .exchange()
                            .expectStatus().isOk()
                            .expectBody(String.class)
                            .consumeWith(authResponse -> {
                                String body = authResponse.getResponseBody();
                                System.out.println("Response Body: " + body);
                                assert body != null;
                                assert body.contains("ROLE_USER");
                            });

                    webTestClient.get().uri("/api/auth/me2")
                            .header("Authorization", "Bearer " + jwtToken)
                            .exchange()
                            .expectStatus().isOk()
                            .expectBody(String.class)
                            .consumeWith(authResponse -> {
                                String body = authResponse.getResponseBody();
                                System.out.println("Response Body (me2): " + body);
                                assert body != null;
                                assert body.contains("ROLE_USER");
                            });
                });
    }
}