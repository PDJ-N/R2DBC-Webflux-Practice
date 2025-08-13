package com.todo.externalapi.gemini.service;

import com.todo.exception.CustomException;
import com.todo.exception.dto.ErrorMessage;
import com.todo.externalapi.gemini.dto.ChatResponse;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * 제미나이에게서 받은 응답을 검증하는 클래스.
 *
 * @see ChatResponse            검증해야 하는 객체
 * @see ChatResponse.Candidate  검증해야 하는 객체, 안에 제대로된 값이 들어있는지 확인해야한다. 내부의 Content 필드도 확인함.
 * @see GeminiService           검증이 이루어져야 하는 서비스
 *
 * @author duskafka
 * */
@Component
public class GeminiResponseValidator {

    /**
     * 제미나이에게 받아온 응답 DTO가 제대로된 값을 가지고 있는지 검증하기 위한 메소드.
     *
     * @param response 검증해야 할 응답 DTO
     * @return 만약 받아온 객체가 유효하다면 {@code Mono<String>}형태로 만들어 응답해준다.
     *         만약 유효하지 않다면 Mono.error 객체를 반환해 GlobalExceptionHandler에서 처리하도록 한다.
     * */
    protected static Mono<String> validateResponse(ChatResponse response) {
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
     *
     * @param response 검증해야 할 DTO
     * @return 유효하지 않다면 false를 반환한다
     */
    private static boolean isCandidateEmpty(ChatResponse response) {
        return response == null || response.getCandidates() == null || response.getCandidates().isEmpty();
    }

    /**
     * 응답으로 받아온 Candidate의 Content가 비어있는지 확인하는 메소드.
     *
     * @param candidate 검증해야 할 DTO 내부의 클래스
     * @return 유효하지 않다면 false를 반환한다.
     */
    private static boolean isContentEmpty(ChatResponse.Candidate candidate) {
        return candidate.getContent() == null || candidate.getContent().getParts() == null || candidate.getContent().getParts().isEmpty();
    }
}