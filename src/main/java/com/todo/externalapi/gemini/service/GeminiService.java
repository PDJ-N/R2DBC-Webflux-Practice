package com.todo.externalapi.gemini.service;

import com.todo.exception.CustomException;
import com.todo.exception.dto.ErrorMessage;
import com.todo.externalapi.gemini.dto.ChatRequest;
import com.todo.externalapi.gemini.dto.ChatResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class GeminiService {

    private final WebClient geminiWebClient;

    public GeminiService(@Qualifier("geminiWebClient") WebClient geminiWebClient) {
        this.geminiWebClient = geminiWebClient;
    }

    public Mono<String> getContents(String message) {

        // ChatRequest 객체 생성
        ChatRequest request = new ChatRequest(message);

        // WebClient를 사용한 비동기 요청
        return geminiWebClient.post()
                .body(Mono.just(request), ChatRequest.class)
                .retrieve()
                .onStatus(HttpStatusCode::is5xxServerError, GeminiErrorHandler::handle5xxError)
                .onStatus(HttpStatusCode::is4xxClientError, GeminiErrorHandler::handle4xxError)
                .bodyToMono(ChatResponse.class)
                .flatMap(GeminiResponseValidator::validateResponse)
                .doOnError(error -> log.error("제미나이에 요청을 보내는데 오류가 발생했습니다. {}", error.getMessage()))
                .onErrorMap(error -> {
                    if (error instanceof CustomException) return error;
                    return new CustomException(ErrorMessage.GEMINI_INTERNAL_SERVER_ERROR, error.getMessage());
                });
    }
}