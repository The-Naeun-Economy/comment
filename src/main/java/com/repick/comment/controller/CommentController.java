package com.repick.comment.controller;

import com.repick.comment.dto.CommentLikeResponse;
import com.repick.comment.dto.CommentRequest;
import com.repick.comment.dto.CommentResponse;
import com.repick.comment.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin("*")
@RestController
@RequestMapping("/posts/{postId}/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CommentResponse createComment(@PathVariable Long postId,
                                         @RequestBody @Valid CommentRequest request) {
        Long userId = getAuthenticatedUserId();
        String userNickname = getAuthenticatedUserNickname();

        return commentService.createComment(userId, userNickname, postId, request);
    }

    @GetMapping
    public List<CommentResponse> getCommentsByPost(@PathVariable Long postId) {
        return commentService.getCommentsByPostId(postId);
    }

    @GetMapping("/my-comments")
    public List<CommentResponse> getMyComments(@PathVariable Long postId) {
        Long userId = getAuthenticatedUserId();
        return commentService.getMyComments(userId);
    }

    @PutMapping("/{commentId}")
    public CommentResponse updateComment(@PathVariable Long postId,
                                         @PathVariable Long commentId,
                                         @RequestBody String content) {
        Long userId = getAuthenticatedUserId();
        return commentService.updateComment(userId, postId, commentId, content);
    }

    @DeleteMapping("/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> deleteComment(@PathVariable Long postId,
                                              @PathVariable Long commentId) {
        Long userId = getAuthenticatedUserId();
        commentService.deleteComment(userId, postId, commentId);
        return ResponseEntity.noContent().build();
    }

    // 게시글 좋아요
    @PostMapping("/{id}/like")
    public ResponseEntity<CommentLikeResponse> likePost(@PathVariable Long id) {
        Long userId = getAuthenticatedUserId();
        String userNickname = getAuthenticatedUserNickname();

        CommentLikeResponse response = commentService.toggleLike(id, userId, userNickname);
        return ResponseEntity.ok(response);
    }

    private Long getAuthenticatedUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalArgumentException("User is not authenticated");
        }
        return (Long) authentication.getPrincipal();
    }

    private String getAuthenticatedUserNickname() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalArgumentException("User is not authenticated");
        }
        return (String) authentication.getCredentials();
    }
}
