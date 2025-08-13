package com.todo;

import com.todo.functionendpoint.MyFunctionHandler;
import com.todo.functionendpoint.MyRouterFunction;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.server.WebHandler;

@SpringBootTest
public class FunctionHandlerAndRouterFunctionTest {
    // MyHandler와 MyRouter 인스턴스를 생성
    private final MyFunctionHandler myHandler = new MyFunctionHandler();
    private final MyRouterFunction myRouter = new MyRouterFunction();

    // WebTestClient를 설정. RouterFunction을 HttpHandler로 변환하여 연결
    private final WebTestClient client = WebTestClient.bindToWebHandler(
            (WebHandler) RouterFunctions.toHttpHandler(myRouter.route(myHandler))
    ).build();

    @Test
    void testHello() {
        client.get().uri("/hello")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .isEqualTo("Hello");
    }

    @Test
    void testGoodbyeWithName() {
        client.get().uri("/goodbye?name=홍길동")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .isEqualTo("Goodbye, 홍길동");
    }

    @Test
    void testGoodbyeWithoutName() {
        client.get().uri("/goodbye")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .isEqualTo("Goodbye, guest");
    }
}