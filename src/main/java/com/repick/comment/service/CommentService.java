package com.repick.comment.service;

import com.repick.comment.dto.CommentRequest;
import com.repick.comment.dto.CommentResponse;

import java.util.List;

public interface CommentService {
    CommentResponse createComment(Long userId, Long postId, CommentRequest request);

    List<CommentResponse> getCommentsByPostId(Long postId);

    List<CommentResponse> getUserComments(Long userId);

    CommentResponse updateComment(Long userId, Long postId, Long commentId, String content);

    void deleteComment(Long userId, Long postId, Long commentId);
}
