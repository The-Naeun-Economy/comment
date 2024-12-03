package com.repick.comment.service;

import com.repick.comment.dto.CommentLikeResponse;
import com.repick.comment.dto.CommentRequest;
import com.repick.comment.dto.CommentResponse;

import java.util.List;

public interface CommentService {
    CommentResponse createComment(Long userId, String userNickname, Long postId, CommentRequest request);

    List<CommentResponse> getCommentsByPostId(Long postId);

    List<CommentResponse> getMyComments(Long userId);

    CommentResponse updateComment(Long userId, Long postId, Long commentId, String content);

    void deleteComment(Long userId, Long postId, Long commentId);

    CommentLikeResponse toggleLike(Long id, Long userId, String userNickname);

    List<CommentLikeResponse> getMyLikedComments(Long userId);
}
