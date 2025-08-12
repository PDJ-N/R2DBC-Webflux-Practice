package com.todo.externalapi.gemini.service;

import com.todo.exception.CustomException;
import com.todo.exception.dto.ErrorMessage;
import com.todo.externalapi.gemini.dto.ChatRequest;
import com.todo.externalapi.gemini.dto.ChatResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
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
                .map(response ->
                        response.getCandidates().stream()
                                .findFirst()
                                .map(candidate -> candidate.getContent().getParts().stream()
                                        .findFirst()
                                        .map(ChatResponse.Parts::getText)
                                        .orElse("응답이 비어있습니다.")
                                )
                                .orElse("후보 응답이 없습니다.")
                )
                .doOnError(error -> log.error("제미나이에 요청을 보내는데 오류가 발생했습니다.\n ---------- ERROR MESSAGE FROM GEMINI -----------\n {}", error))
                .onErrorMap(error -> new CustomException(ErrorMessage.GEMINI_INTERNAL_SERVER_ERROR, error.getMessage()));
    }
}