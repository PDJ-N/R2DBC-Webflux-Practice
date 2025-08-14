package com.todo.externalapi.gemini.dto;

/**
 * 사용자가 입력하 message를 받아오기 위한 DTO
 * */
public record FreeChatRequest(String message) {
}