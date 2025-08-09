package com.r2dbc.user.dto.request;

public record UserLoginRequest(String username, String password) {
}