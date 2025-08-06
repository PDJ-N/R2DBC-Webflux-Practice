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
    private String name;
    private String email;

    @Builder
    private User(Long id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }

    public static User toEntity(UserCreateRequest request){
        return User.builder()
                .name(request.name())
                .email(request.email())
                .build();
    }
}