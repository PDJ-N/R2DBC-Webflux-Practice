package com.todo.post.domain;

import com.todo.post.dto.request.PostCreateRequest;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Setter
@Getter
@Table("posts")
@NoArgsConstructor
public class Post {
    @Id
    private Long id;
    private String title;
    private String content;
    private Long userId; // 외래 키

    @Builder
    public Post(String title, String content, Long userId) {
        this.title = title;
        this.content = content;
        this.userId = userId;
    }


    public static Post toEntity(Long userId, PostCreateRequest request){
        return Post.builder()
                .userId(userId)
                .title(request.title())
                .content(request.content())
                .build();
    }
}