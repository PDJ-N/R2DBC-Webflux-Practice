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
                .onStatus(HttpStatusCode::is5xxServerError, resp ->
                        resp.bodyToMono(String.class)
                                .defaultIfEmpty("")
                                .flatMap(body -> Mono.error(new CustomException(
                                        ErrorMessage.GEMINI_INTERNAL_SERVER_ERROR,
                                        "Gemini API 호출 실패: HTTP " + resp.statusCode() + " body=" + body))
                                )
                )
                .onStatus(HttpStatusCode::is4xxClientError, resp ->
                        resp.bodyToMono(String.class)
                                .defaultIfEmpty("")
                                .flatMap(body -> Mono.error(new CustomException(
                                        ErrorMessage.GEMINI_BAD_REQUEST,
                                        "Gemini API 호출 실패: HTTP " + resp.statusCode() + " body=" + body))
                                )
                )
                .bodyToMono(ChatResponse.class)
                .flatMap(this::validateResponse)
                .doOnError(error -> log.error("제미나이에 요청을 보내는데 오류가 발생했습니다.\n ---------- ERROR MESSAGE FROM GEMINI -----------\n {}", error))
                .onErrorMap(error -> new CustomException(ErrorMessage.GEMINI_INTERNAL_SERVER_ERROR, error.getMessage()));
    }

    private Mono<String> validateResponse(ChatResponse response) {
        if (isCandidateEmpty(response)) {
            return Mono.error(new CustomException(
                    ErrorMessage.GEMINI_INTERNAL_SERVER_ERROR,
                    "Gemini 응답에 candidates가 없습니다.")
            );
        }
        var candidate = response.getCandidates().get(0);
        if (isContentEmpty(candidate)) {
            return Mono.error(new CustomException(
                    ErrorMessage.GEMINI_INTERNAL_SERVER_ERROR,
                    "Gemini 응답에 parts가 없습니다.")
            );
        }
        return Mono.just(candidate.getContent().getParts().get(0).getText());
    }

    /**
     * 응답으로 받아온 ChatResponse의 Candidate가 비어있는지 확인하는 메소드
     */
    private boolean isCandidateEmpty(ChatResponse response) {
        return response == null || response.getCandidates() == null || response.getCandidates().isEmpty();
    }

    /**
     * 응답으로 받아온 Candidate의 Content가 비어있는지 확인하는 메소드.
     */
    private boolean isContentEmpty(ChatResponse.Candidate candidate) {
        return candidate.getContent() == null || candidate.getContent().getParts() == null || candidate.getContent().getParts().isEmpty();
    }
}