package com.todo.externalapi.gemini.service;

import com.todo.exception.CustomException;
import com.todo.exception.dto.ErrorMessage;
import com.todo.externalapi.gemini.dto.ChatResponse;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class GeminiResponseValidator {
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
     */
    private static boolean isCandidateEmpty(ChatResponse response) {
        return response == null || response.getCandidates() == null || response.getCandidates().isEmpty();
    }

    /**
     * 응답으로 받아온 Candidate의 Content가 비어있는지 확인하는 메소드.
     */
    private static boolean isContentEmpty(ChatResponse.Candidate candidate) {
        return candidate.getContent() == null || candidate.getContent().getParts() == null || candidate.getContent().getParts().isEmpty();
    }
}