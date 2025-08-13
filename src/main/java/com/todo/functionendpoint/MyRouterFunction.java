package com.todo.functionendpoint;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;

@Configuration
public class MyRouterFunction {
    @Bean
    public RouterFunction<ServerResponse> route(MyFunctionHandler handler) {
        return RouterFunctions
                .route(GET("/hello"), handler::hello)
                .andRoute(GET("/goodbye"), handler::goodbye);
    }
}