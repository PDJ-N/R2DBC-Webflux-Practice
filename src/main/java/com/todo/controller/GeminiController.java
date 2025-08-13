package com.todo.controller;

import com.todo.externalapi.gemini.dto.FreeChatRequest;
import com.todo.externalapi.gemini.service.GeminiService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/gemini")
public class GeminiController {

    private final GeminiService geminiService;

    /**
     * 제미나이에게 기본적인 테스트 요청을 보내 제대로 연결이 되었는지 확인하기 위한 메소드.
     * 보통 "저는 구글에게 개발한 프롬포트~~~"같은 소개문구가 온다.
     * */
    @GetMapping("/test")
    public Mono<String> gemini() {
        return geminiService.getContents("안녕! 너는 누구야?");
    }

    /**
     * 사용자가 문자열로 제미나이 프롬포트에게 요청할 수 있도록 해주는 REST API.
     * 자유롭게 제미나이에게 질문할 수 있다.
     * */
    @GetMapping("/free-chat")
    public Mono<String> free(@RequestBody FreeChatRequest request) {
        return geminiService.getContents(request.message());
    }
}