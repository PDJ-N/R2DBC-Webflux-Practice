package com.todo.controller;

import com.todo.externalapi.gemini.dto.FreeChatRequest;
import com.todo.externalapi.gemini.service.GeminiService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/gemini")
public class GeminiController {

    private final GeminiService geminiService;

    @GetMapping("/test")
    public Mono<?> gemini() {
        try {
            return geminiService.getContents("안녕! 너는 누구야?");
        } catch (HttpClientErrorException e) {
            return Mono.error(e);
        }
    }

    @GetMapping("/free-chat")
    public Mono<?> free(@RequestBody FreeChatRequest request){
        try {
            return geminiService.getContents(request.message());
        }catch (HttpClientErrorException e){
            return Mono.error(e);
        }
    }
}