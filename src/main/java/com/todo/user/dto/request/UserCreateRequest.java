package com.todo.user.dto.request;

public record UserCreateRequest(String username, String password, String name, String email) {
    public UserCreateRequest(String username, String password, String name, String email) {
        this.username = username;
        this.password = password;
        this.name = name;
        this.email = email;
    }
}