package com.r2dbc.user.domain;

import com.r2dbc.user.dto.request.UserCreateRequest;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@Setter
@Table("users")
@NoArgsConstructor
@ToString
public class User {
    @Id
    private Long id;
    private String username;
    private String password;
    private String name;
    private String email;
    private String roles = "USER";

    @Builder
    public User(String username, String password, String name, String email) {
        this.username = username;
        this.password = password;
        this.name = name;
        this.email = email;
    }

    public static User toEntity(UserCreateRequest request){
        return User.builder()
                .name(request.name())
                .email(request.email())
                .password(request.password())
                .username(request.username())
                .build();
    }
}