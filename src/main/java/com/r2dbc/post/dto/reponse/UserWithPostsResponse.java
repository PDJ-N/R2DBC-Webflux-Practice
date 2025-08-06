package com.r2dbc.post.dto.reponse;

import com.r2dbc.post.domain.Post;
import com.r2dbc.user.domain.User;

import java.util.List;

public record UserWithPostsResponse(
        User user,
        List<Post> posts
) {}