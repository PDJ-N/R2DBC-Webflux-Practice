package com.todo.post.dto.response;

import com.todo.post.domain.Post;
import com.todo.user.domain.User;

import java.util.List;

public record UserWithPostsResponse(
        User user,
        List<Post> posts
) {}