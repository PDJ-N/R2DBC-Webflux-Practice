package com.r2dbc.user.dto;

public record UserCreateRequest(String name, String email) {

    public UserCreateRequest(String name, String email) {
        this.name = name;
        this.email = email;
    }
}