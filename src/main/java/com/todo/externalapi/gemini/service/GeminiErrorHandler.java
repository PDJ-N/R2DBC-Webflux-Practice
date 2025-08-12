package com.todo.externalapi.gemini.service;

import com.todo.exception.CustomException;
import com.todo.exception.dto.ErrorMessage;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ClientResponse;
import reactor.core.publisher.Mono;

@Component
public class GeminiErrorHandler {
    protected static Mono<? extends Throwable> handle5xxError(ClientResponse response) {
        return response.bodyToMono(String.class)
                .defaultIfEmpty("")
                .flatMap(body -> Mono.error(new CustomException(
                        ErrorMessage.GEMINI_INTERNAL_SERVER_ERROR,
                        "Gemini API 호출 실패: HTTP " + response.statusCode() + " body=" + body))
                );
    }

    protected static Mono<? extends Throwable> handle4xxError(ClientResponse response) {
        HttpStatusCode httpStatusCode = response.statusCode();

        if (HttpStatusCode.valueOf(429) == httpStatusCode) {
            return response.bodyToMono(String.class)
                    .flatMap(body -> Mono.error(new CustomException(
                            ErrorMessage.GEMINI_TOO_MANY_REQUESTS,
                            "Gemini에 너무 많은 요청이 들어가 제한되었습니다. 잠시 후 시도해주세요."
                    )));
        }

        if (HttpStatusCode.valueOf(401) == httpStatusCode) {
            return response.bodyToMono(String.class)
                    .flatMap(body -> Mono.error(new CustomException(
                            ErrorMessage.GEMINI_UNAUTHORIZED,
                            "유효한 인증이 없는 요청입니다."
                    )));
        }

        return response.bodyToMono(String.class)
                .defaultIfEmpty("")
                .flatMap(body -> Mono.error(new CustomException(
                        ErrorMessage.GEMINI_BAD_REQUEST,
                        "Gemini API 호출 실패: HTTP " + response.statusCode() + " body=" + body))
                );
    }
}