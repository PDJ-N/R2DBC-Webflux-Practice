package com.todo.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class GeminiConfig {

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