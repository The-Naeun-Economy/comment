package com.repick.comment.service;

import com.repick.comment.client.PostClient;
import com.repick.comment.client.UserClient;
import com.repick.comment.domain.Comment;
import com.repick.comment.dto.CommentRequest;
import com.repick.comment.dto.CommentResponse;
import com.repick.comment.dto.UserResponse;
import com.repick.comment.mapper.CommentMapper;
import com.repick.comment.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final UserClient userClient;
    private final PostClient postClient;

    @Override
    public CommentResponse createComment(Long userId, Long postId, CommentRequest request) {
        UserResponse userResponse = userClient.getUserById(userId); // 결과를 변수에 저장
        if (userResponse == null) {
            throw new IllegalArgumentException("Invalid user ID");
        }

        // 게시글 검증
        if (!postClient.checkPostExists(postId)) {
            throw new IllegalArgumentException("Invalid post ID");
        }

        // 댓글 생성
        Comment comment = Comment.builder()
                .postId(postId)
                .userId(userId)
                .userNickname(userResponse.getNickname()) // 저장된 변수 사용
                .content(request.getContent())
                .isDeleted(false)
                .build();

        Comment savedComment = commentRepository.save(comment);

        return CommentMapper.toResponse(savedComment);
    }

    @Override
    public List<CommentResponse> getCommentsByPostId(Long postId) {
        return commentRepository.findByPostId(postId).stream()
                .map(CommentMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<CommentResponse> getUserComments(Long userId) {
        return commentRepository.findByUserId(userId).stream()
                .map(CommentMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public CommentResponse updateComment(Long userId, Long postId, Long commentId, String content) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("Comment not found"));

        // 유저 검증
        if (!comment.getUserId().equals(userId)) {
            throw new SecurityException("You can only update your own comments");
        }

        // 게시글 검증
        if (!comment.getPostId().equals(postId)) {
            throw new IllegalArgumentException("Post ID mismatch");
        }

        // 댓글 업데이트
        comment.updateContent(content);
        Comment updatedComment = commentRepository.save(comment);

        return CommentMapper.toResponse(updatedComment);
    }

    @Override
    public void deleteComment(Long userId, Long postId, Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("Comment not found"));

        // 유저 검증
        if (!comment.getUserId().equals(userId)) {
            throw new SecurityException("You can only delete your own comments");
        }

        // 게시글 검증
        if (!comment.getPostId().equals(postId)) {
            throw new IllegalArgumentException("Post ID mismatch");
        }

        // 댓글 삭제 (Soft Delete)
        comment.softDelete();
        commentRepository.save(comment);
    }
}
