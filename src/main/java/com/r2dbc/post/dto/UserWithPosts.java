package com.r2dbc.post.dto;

import com.r2dbc.post.domain.Post;
import com.r2dbc.user.domain.User;

import java.util.List;

public record UserWithPosts(
        User user,
        List<Post> posts
) {}