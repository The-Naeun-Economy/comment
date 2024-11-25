package com.repick.comment.service;

import com.repick.comment.domain.Comment;
import com.repick.comment.dto.CommentRequest;
import com.repick.comment.dto.CommentResponse;
import com.repick.comment.mapper.CommentMapper;
import com.repick.comment.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;

    @Transactional
    @Override
    public CommentResponse createComment(Long userId, String userNickname, Long postId, CommentRequest request) {

        if (postId == null || postId <= 0) {
            throw new IllegalArgumentException("Invalid post ID");
        }

        // 댓글 생성
        Comment comment = Comment.builder()
                .postId(postId)
                .userId(userId)
                .userNickname(userNickname)
                .content(request.getContent())
                .isDeleted(false)
                .build();
        Comment savedComment = commentRepository.save(comment);

        return CommentMapper.toResponse(savedComment);
    }

    @Transactional(readOnly = true)
    @Override
    public List<CommentResponse> getCommentsByPostId(Long postId) {
        return commentRepository.findByPostIdAndIsDeletedFalse(postId).stream()
                .map(CommentMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public List<CommentResponse> getMyComments(Long userId) {
        return commentRepository.findByUserIdAndIsDeletedFalse(userId).stream()
                .map(CommentMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public CommentResponse updateComment(Long userId, Long postId, Long commentId, String content) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("Comment not found"));

        validateUserAuthorization(comment, userId);

        if (!comment.getPostId().equals(postId)) {
            throw new IllegalArgumentException("Post ID mismatch");
        }

        /// 댓글 업데이트
        comment.updateContent(content);

        // 변경된 댓글 저장
        Comment updatedComment = commentRepository.save(comment);

        return CommentMapper.toResponse(updatedComment);
    }

    @Transactional
    @Override
    public void deleteComment(Long userId, Long postId, Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("Comment not found"));

        validateUserAuthorization(comment, userId);

        if (!comment.getPostId().equals(postId)) {
            throw new IllegalArgumentException("Post ID mismatch");
        }

        // 이미 삭제된 상태인지 확인
        if (comment.isDeleted()) {
            throw new IllegalStateException("Comment is already deleted");
        }

        // 댓글 삭제 (Soft Delete)
        comment.softDelete();
        commentRepository.save(comment);
    }

    // 사용자 검증 로직
    private void validateUserAuthorization(Comment comment, Long userId) {
        if (!comment.getUserId().equals(userId)) {
            throw new IllegalArgumentException("You are not authorized to perform this action on this comment");
        }
    }
}
