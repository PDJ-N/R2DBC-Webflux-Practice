package com.r2dbc.user.dto.request;

public record UserCreateRequest(String name, String email) {

    public UserCreateRequest(String name, String email) {
        this.name = name;
        this.email = email;
    }
}