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

/**
 * 제미나이 프롬포트에 요청을 보내기 위한 서비스 클래스.
 *
 * @see GeminiErrorHandler          제미나이에게 에러 응답이 왔을 때 처리해주는 핸들러
 * @see GeminiResponseValidator     제미나이의 응답이 올바른지 검증해주는 클래스
 * @see ChatRequest                 제미나이에 요청을 보내기 위한 DTO
 * @see ChatResponse                제미나이에 요청을 보내고 받아온 DTO
 *
 * @author duskafka
 * */
@Slf4j
@Service
public class GeminiService {

    private final WebClient geminiWebClient;

    public GeminiService(@Qualifier("geminiWebClient") WebClient geminiWebClient) {
        this.geminiWebClient = geminiWebClient;
    }

    /**
     * 제미나이에게 요청을 보내기 위한 요청 메소드.
     *
     * @implSpec {@code Mono<String>}을 사용하여 논블로킹으로 요청을 보낼 수 있도록 하였다.
     * @implNote {@code ChatRequest} 클래스의 모양(필드 구성 등)은 구글의 변경으로 인해 변경해야 할 수도 있음.
     *
     * @param message 사용자가 프롬포트에 보내달라고 요청한 문자열
     *
     * @return 제미나이에게서 받아온 답변
     * */
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