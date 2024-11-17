package com.repick.comment.controller;

import com.repick.comment.dto.CommentRequest;
import com.repick.comment.dto.CommentResponse;
import com.repick.comment.service.CommentService;
import lombok.RequiredArgsConstructor;
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
        return commentService.updateComment(userId, postId, commentId, content);
    }

    @DeleteMapping("/{commentId}")
    public void deleteComment(@RequestHeader("USER-ID") Long userId,
                              @PathVariable Long postId,
                              @PathVariable Long commentId) {
        commentService.deleteComment(userId, postId, commentId);
    }
}
