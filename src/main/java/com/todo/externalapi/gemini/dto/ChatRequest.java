package com.todo.externalapi.gemini.dto;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * 제미나이에게 요청을 보내기 위한 DTO
 * */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatRequest {
    private List<Content> contents;
    private GenerationConfig generationConfig;

    @Getter
    @Setter
    public static class Content {
        private Parts parts;
    }

    @Getter
    @Setter
    public static class Parts {
        private String text;

    }

    @Getter
    @Setter
    public static class GenerationConfig {
        private int candidate_count;
        private int max_output_tokens;
        private double temperature;

    }

    /**
     * 문자열을 받아서 ChatRequest 객체를 생성한다.
     * */
    public ChatRequest(String prompt) {
        this.contents = new ArrayList<>();
        Content content = new Content();
        Parts parts = new Parts();

        parts.setText(prompt);
        content.setParts(parts);

        this.contents.add(content);
        this.generationConfig = new GenerationConfig();
        this.generationConfig.setCandidate_count(1);
        this.generationConfig.setMax_output_tokens(1000);
        this.generationConfig.setTemperature(0.7);
    }
}