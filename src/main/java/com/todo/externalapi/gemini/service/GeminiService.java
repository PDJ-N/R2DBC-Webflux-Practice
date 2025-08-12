package com.todo.externalapi.gemini.service;

import com.todo.externalapi.gemini.dto.ChatRequest;
import com.todo.externalapi.gemini.dto.ChatResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class GeminiService {

    private final WebClient geminiWebClient;
    private final String apiUrl;
    private final String geminiApiKey;

    public GeminiService(@Qualifier("geminiWebClient") WebClient geminiWebClient, @Value("${gemini.api.url}") String apiUrl, @Value("${gemini.api.key}") String geminiApiKey) {
        this.geminiWebClient = geminiWebClient;
        this.apiUrl = apiUrl;
        this.geminiApiKey = geminiApiKey;
    }

    public Mono<String> getContents(String prompt) {

        // ChatRequest 객체 생성
        ChatRequest request = new ChatRequest(prompt);

        // WebClient를 사용한 비동기 요청
        return geminiWebClient.post()
                .uri(apiUrl + "?key=" + geminiApiKey)
                .body(Mono.just(request), ChatRequest.class)
                .retrieve()
                .bodyToMono(ChatResponse.class)
                .map(response -> response.getCandidates().get(0).getContent().getParts().get(0).getText());
    }
}