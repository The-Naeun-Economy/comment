package com.repick.comment.service;

import com.repick.comment.domain.Comment;
import com.repick.comment.domain.CommentLike;
import com.repick.comment.dto.CommentLikeResponse;
import com.repick.comment.dto.CommentRequest;
import com.repick.comment.dto.CommentResponse;
import com.repick.comment.mapper.CommentMapper;
import com.repick.comment.repository.CommentCacheRepository;
import com.repick.comment.repository.CommentLikeRepository;
import com.repick.comment.repository.CommentRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final CommentLikeRepository commentLikeRepository;
    private final CommentCacheRepository commentCacheRepository;

    @Override
    @Transactional
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

    @Override
    @Transactional(readOnly = true)
    public List<CommentResponse> getCommentsByPostId(Long postId) {
        return commentRepository.findByPostIdAndIsDeletedFalse(postId).stream()
                .map(CommentMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommentResponse> getMyComments(Long userId) {
        return commentRepository.findByUserIdAndIsDeletedFalse(userId).stream()
                .map(CommentMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
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

    @Override
    @Transactional
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

    @Override
    @Transactional
    public CommentLikeResponse toggleLike(Long id, Long userId, String userNickname) {

        // 댓글 조회
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Comment not found"));

        if (comment.getUserId().equals(userId)) {
            throw new IllegalArgumentException("You cannot like your own post.");
        }

        // 좋아요 여부 확인
        Optional<CommentLike> existingLike = commentLikeRepository.findByUserIdAndCommentId(userId, comment);

        boolean isLiked;

        if (existingLike.isEmpty()) {
            // 좋아요 추가
            CommentLike newLike = CommentLike.builder()
                    .userId(userId)
                    .userNickname(userNickname)
                    .commentId(comment)
                    .build();
            commentLikeRepository.save(newLike);

            comment.incrementPostLikesCount();
            isLiked = true;
        } else {
            // 좋아요 취소
            CommentLike like = existingLike.get();
            commentLikeRepository.delete(like);

            comment.decrementLikeCount();
            isLiked = false;
        }

        // 캐시에 좋아요 수 저장
        String cacheKey = commentCacheRepository.getCacheKey() + "::" + id;
        commentCacheRepository.saveCacheData(cacheKey, comment.getLikesCount());

        // 변경된 좋아요 수 저장
        commentRepository.save(comment);

        return new CommentLikeResponse(isLiked, comment.getLikesCount());
    }

    // 사용자 검증 로직
    private void validateUserAuthorization(Comment comment, Long userId) {
        if (!comment.getUserId().equals(userId)) {
            throw new IllegalArgumentException("You are not authorized to perform this action on this comment");
        }
    }
}
