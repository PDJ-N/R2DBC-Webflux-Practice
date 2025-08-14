package com.todo.functionendpoint;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
public class MyFunctionHandler {
    public Mono<ServerResponse> hello(ServerRequest request) {
        return ServerResponse.ok()
                .bodyValue("Hello");
    }

    public Mono<ServerResponse> goodbye(ServerRequest request) {
        String name = request.queryParam("name").orElse("guest");
        return ServerResponse.ok()
                .bodyValue("Goodbye, " + name);
    }
}