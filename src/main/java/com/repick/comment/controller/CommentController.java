package com.repick.comment.controller;

import com.repick.comment.dto.CommentLikeResponse;
import com.repick.comment.dto.CommentRequest;
import com.repick.comment.dto.CommentResponse;
import com.repick.comment.jwt.TokenProvider;
import com.repick.comment.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/v1/posts/{postId}/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;
    private final TokenProvider tokenProvider;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CommentResponse createComment(@PathVariable Long postId,
                                         @RequestBody @Valid CommentRequest request,
                                         @RequestHeader String Authorization) {
        Long userId = tokenProvider.getUserIdFromToken(Authorization);
        String userNickname = tokenProvider.getNickNameFromToken(Authorization);

        return commentService.createComment(userId, userNickname, postId, request);
    }

    @GetMapping
    public List<CommentResponse> getCommentsByPost(@PathVariable Long postId) {
        return commentService.getCommentsByPostId(postId);
    }

    @GetMapping("/my-comments")
    public List<CommentResponse> getMyComments(@PathVariable Long postId,
                                               @RequestHeader String Authorization) {
        Long userId = tokenProvider.getUserIdFromToken(Authorization);
        return commentService.getMyComments(userId);
    }

    @PutMapping("/{commentId}")
    public CommentResponse updateComment(@PathVariable Long postId,
                                         @PathVariable Long commentId,
                                         @RequestBody String content,
                                         @RequestHeader String Authorization) {
        Long userId = tokenProvider.getUserIdFromToken(Authorization);
        return commentService.updateComment(userId, postId, commentId, content);
    }

    @DeleteMapping("/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> deleteComment(@PathVariable Long postId,
                                              @PathVariable Long commentId,
                                              @RequestHeader String Authorization) {
        Long userId = tokenProvider.getUserIdFromToken(Authorization);
        commentService.deleteComment(userId, postId, commentId);
        return ResponseEntity.noContent().build();
    }

    // 댓글 좋아요
    @PostMapping("/{id}/like")
    public ResponseEntity<CommentLikeResponse> likeComment (@PathVariable Long id,
                                                        @RequestHeader String Authorization) {
        Long userId = tokenProvider.getUserIdFromToken(Authorization);
        String userNickname = tokenProvider.getNickNameFromToken(Authorization);

        CommentLikeResponse response = commentService.toggleLike(id, userId, userNickname);
        return ResponseEntity.ok(response);
    }

}
