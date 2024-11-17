package com.repick.comment;

import com.repick.comment.controller.CommentController;
import com.repick.comment.dto.CommentRequest;
import com.repick.comment.dto.CommentResponse;
import com.repick.comment.service.CommentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CommentControllerTest {

    @InjectMocks
    private CommentController commentController;

    @Mock
    private CommentService commentService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createComment_ShouldReturnCreatedComment() {
        // Given
        Long userId = 10L;
        Long postId = 20L;
        CommentRequest request = new CommentRequest("New Comment");
        CommentResponse response = CommentResponse.builder()
                .id(1L)
                .postId(postId)
                .userId(userId)
                .content("New Comment")
                .isDeleted(false)
                .build();

        when(commentService.createComment(userId, postId, request)).thenReturn(response);

        // When
        CommentResponse result = commentController.createComment(userId, postId, request);

        // Then
        assertNotNull(result);
        assertEquals("New Comment", result.getContent());
        assertEquals(1L, result.getId());
        verify(commentService, times(1)).createComment(userId, postId, request);
    }

    @Test
    void getCommentsByPost_ShouldReturnCommentList() {
        // Given
        Long postId = 20L;
        List<CommentResponse> responses = List.of(
                CommentResponse.builder().id(1L).postId(postId).userId(10L).content("Comment1").isDeleted(false).build(),
                CommentResponse.builder().id(2L).postId(postId).userId(20L).content("Comment2").isDeleted(false).build()
        );

        when(commentService.getCommentsByPostId(postId)).thenReturn(responses);

        // When
        List<CommentResponse> result = commentController.getCommentsByPost(postId);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Comment1", result.getFirst().getContent());
        verify(commentService, times(1)).getCommentsByPostId(postId);
    }

    @Test
    void updateComment_ShouldReturnUpdatedComment() {
        // Given
        Long userId = 10L;
        Long postId = 20L;
        Long commentId = 1L;
        String updatedContent = "Updated Comment";
        CommentResponse response = CommentResponse.builder()
                .id(commentId)
                .postId(postId)
                .userId(userId)
                .content(updatedContent)
                .isDeleted(false)
                .build();

        when(commentService.updateComment(userId, postId, commentId, updatedContent)).thenReturn(response);

        // When
        CommentResponse result = commentController.updateComment(userId, postId, commentId, updatedContent);

        // Then
        assertNotNull(result);
        assertEquals("Updated Comment", result.getContent());
        verify(commentService, times(1)).updateComment(userId, postId, commentId, updatedContent);
    }

    @Test
    void deleteComment_ShouldCallServiceDelete() {
        // Given
        Long userId = 10L;
        Long postId = 20L;
        Long commentId = 1L;

        doNothing().when(commentService).deleteComment(userId, postId, commentId);

        // When
        commentController.deleteComment(userId, postId, commentId);

        // Then
        verify(commentService, times(1)).deleteComment(userId, postId, commentId);
    }
}
