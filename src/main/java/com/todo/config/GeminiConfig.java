package com.todo.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * 제미나이에 요청을 보내는 WebClient를 만들기 위한 설정 클래스
 * */
@Configuration
public class GeminiConfig {

    /**
     * @implSpec 여기서 baseUrl을 지정하면 서비스 클래스에서 url을 지정해주지 않아도 된다.
     * @implSpec 여기에 defaultHeader로 헤더에 키를 넣어줬는데, 이렇게 하면 URL에 키를 param으로 넣어서 전송하지 않아도 되서 안전하다.
     * @implNote 만약 키를 URL에 포함한다고 가정했을 때, URL을 로깅해야 한다면 로깅에 키가 남게 된다. 이는 보안 문제로 이어질 수 있다.
     * */
    @Bean
    public WebClient geminiWebClient(
            WebClient.Builder webClientBuilder,
            @Value("${gemini.api.key}") String geminiApiKey,
            @Value("${gemini.api.url}") String apiUrl
    ) {
        return webClientBuilder
                .baseUrl(apiUrl)
                .defaultHeader("x-goog-api-key", geminiApiKey)
                .build();
    }
}