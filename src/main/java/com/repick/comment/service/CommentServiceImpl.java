package com.repick.comment.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    @Override
    public List<CommentResponse> getAllcommentsByPostId(Long postId) {
        return List.of();
    }

    @Override
    public CommentResponse createComment(Long userId, CommentRequest request) {
        return null;
    }

    @Override
    public CommentResponse updateComment(Long userId, Long commentId, String content) {
        return null;
    }

    @Override
    public void deleteComment(Long userId, Long commentId) {

    }
}
