package com.repick.comment.service;

import java.util.List;

public interface CommentService {
    List<CommentResponse> getAllcommentsByPostId(Long postId);

    CommentResponse createComment(Long userId, CommentRequest request);

    CommentResponse updateComment(Long userId, Long commentId, String content);

    void deleteComment(Long userId, Long commentId);
}
