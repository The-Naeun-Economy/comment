package com.repick.comment.mapper;

import com.repick.comment.domain.Comment;
import com.repick.comment.dto.CommentResponse;

public class CommentMapper {

    public static CommentResponse toResponse(Comment comment) {
        return CommentResponse.builder()
                .id(comment.getId())
                .postId(comment.getPostId())
                .userId(comment.getUserId())
                .userNickname(comment.getUserNickname())
                .content(comment.getContent())
                .isDeleted(comment.isDeleted())
                .createdAt(comment.getCreatedAt() != null ? comment.getCreatedAt().toString() : null)
                .updatedAt(comment.getUpdatedAt() != null ? comment.getUpdatedAt().toString() : null)
                .build();
    }
}
