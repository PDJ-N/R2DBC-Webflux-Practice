package com.r2dbc.post.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("posts")
public class Post {
    @Id
    private Long id;
    private String title;
    private Long userId; // 외래 키

    // 생성자, getter/setter
    public Post() {}

    public Post(Long id, String title, Long userId) {
        this.id = id;
        this.title = title;
        this.userId = userId;
    }
}