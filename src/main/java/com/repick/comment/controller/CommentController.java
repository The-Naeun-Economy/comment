package com.repick.comment.controller;

import com.repick.comment.dto.CommentRequest;
import com.repick.comment.dto.CommentResponse;
import com.repick.comment.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/posts/{postId}/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    public CommentResponse createComment(@RequestHeader("USER-ID") Long userId,
                                         @PathVariable Long postId,
                                         @RequestBody CommentRequest request) {
        if (userId == null) {
            userId = 1L; // 하드코딩된 기본값
        }
        return commentService.createComment(userId, postId, request);
    }

    @GetMapping
    public List<CommentResponse> getCommentsByPost(@PathVariable Long postId) {
        return commentService.getCommentsByPostId(postId);
    }

    @GetMapping("/users/{userId}")
    public List<CommentResponse> getUserComments(@PathVariable Long userId) {
        return commentService.getUserComments(userId);
    }

    @PutMapping("/{commentId}")
    public CommentResponse updateComment(@RequestHeader("USER-ID") Long userId,
                                         @PathVariable Long postId,
                                         @PathVariable Long commentId,
                                         @RequestBody String content) {
        if (userId == null) {
            userId = 1L; // 하드코딩된 기본값
        }
        return commentService.updateComment(userId, postId, commentId, content);
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(@RequestHeader("USER-ID") Long userId,
                              @PathVariable Long postId,
                              @PathVariable Long commentId) {
        if (userId == null) {
            userId = 1L; // 하드코딩된 기본값
        }
        commentService.deleteComment(userId, postId, commentId);
        return ResponseEntity.noContent().build();
    }
}
