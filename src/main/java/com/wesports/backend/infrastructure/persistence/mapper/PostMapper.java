package com.wesports.backend.infrastructure.persistence.mapper;

import com.wesports.backend.domain.model.Post;
import com.wesports.backend.infrastructure.persistence.entity.PostEntity;
import org.springframework.stereotype.Component;

@Component
public class PostMapper {

    public PostEntity toEntity(Post post) {
        if (post == null) {
            return null;
        }

        return new PostEntity(
            post.getId(),
            post.getCode(),
            post.getLabel()
        );
    }

    public Post toDomain(PostEntity entity) {
        if (entity == null) {
            return null;
        }

        return new Post(
            entity.getId(),
            entity.getCode(),
            entity.getLabel()
        );
    }
}
