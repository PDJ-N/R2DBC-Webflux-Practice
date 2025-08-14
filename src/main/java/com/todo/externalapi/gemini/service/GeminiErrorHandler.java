package com.todo.externalapi.gemini.service;

import com.todo.exception.CustomException;
import com.todo.exception.dto.ErrorMessage;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ClientResponse;
import reactor.core.publisher.Mono;

/**
 * 제미나이에게서 문제가 발생했다는 응답 코드를 받았을 때 이를 처리하기 위한 메소드를 가진 클래스
 *
 * @author duskafka
 * @see GeminiService
 * */
@Component
public class GeminiErrorHandler {
    /**
     * 제미나이에 요청을 보내고 5xx 응답이 왔을 때 대응하기 위한 핸들러 메소드.
     *
     * @param response 응답 코드를 가지고 있는 DTO
     * @return Mono.error 객체를 반환해 GlobalExceptionHandler에서 처리할 수 있도록 만든다.
     * */
    protected static Mono<? extends Throwable> handle5xxError(ClientResponse response) {
        return response.bodyToMono(String.class)
                .defaultIfEmpty("")
                .flatMap(body -> Mono.error(new CustomException(
                        ErrorMessage.GEMINI_INTERNAL_SERVER_ERROR,
                        "Gemini API 호출 실패: HTTP " + response.statusCode() + " body=" + body))
                );
    }

    /**
     * 제미나이에 요청을 보내고 4xx 응답이 왔을 때 대응하기 위한 핸들러 메소드
     *
     * @param response 응답 코드를 가지고 있는 DTO
     * @return Mono.error 객체를 반환해 GlobalExceptionHandler에서 처리할 수 있도록 만든다.
     * */
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