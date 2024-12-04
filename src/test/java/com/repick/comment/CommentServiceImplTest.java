package com.repick.comment;

import com.repick.comment.client.PostClient;
import com.repick.comment.client.UserClient;
import com.repick.comment.domain.Comment;
import com.repick.comment.dto.CommentRequest;
import com.repick.comment.dto.CommentResponse;
import com.repick.comment.repository.CommentRepository;
import com.repick.comment.service.CommentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CommentServiceImplTest {

    @InjectMocks
    private CommentServiceImpl commentService;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private UserClient userClient;

    @Mock
    private PostClient postClient;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createComment_ShouldSaveAndReturnCommentResponse() {
        // Given
        Long userId = 10L;
        Long postId = 20L;
        CommentRequest request = new CommentRequest("New Comment");
        Comment mockComment = Comment.builder()
                .id(1L)
                .postId(postId)
                .userId(userId)
                .userNickname("TestUser")
                .content(request.getContent())
                .isDeleted(false)
                .build();

        // UserResponse로 Mock 설정
        UserResponse mockUserResponse = new UserResponse(userId, "TestUser");

        when(userClient.getUserById(userId)).thenReturn(mockUserResponse);
        when(postClient.checkPostExists(postId)).thenReturn(true);
        when(commentRepository.save(any(Comment.class))).thenReturn(mockComment);

        // When
        CommentResponse response = commentService.createComment(userId, postId, request);

        // Then
        assertNotNull(response);
        assertEquals("New Comment", response.getContent());
        assertEquals("TestUser", response.getUserNickname());
        verify(userClient, times(1)).getUserById(userId);
        verify(postClient, times(1)).checkPostExists(postId);
        verify(commentRepository, times(1)).save(any(Comment.class));
    }

    @Test
    void getCommentsByPostId_ShouldReturnComments() {
        // Given
        Long postId = 20L;
        List<Comment> mockComments = List.of(
                Comment.builder().id(1L).postId(postId).userId(10L).userNickname("User1").content("Comment1").isDeleted(false).build(),
                Comment.builder().id(2L).postId(postId).userId(20L).userNickname("User2").content("Comment2").isDeleted(false).build()
        );

        when(commentRepository.findByPostId(postId)).thenReturn(mockComments);

        // When
        List<CommentResponse> responses = commentService.getCommentsByPostId(postId);

        // Then
        assertEquals(2, responses.size());
        assertEquals("Comment1", responses.get(0).getContent());
        assertEquals("Comment2", responses.get(1).getContent());
        verify(commentRepository, times(1)).findByPostId(postId);
    }

    @Test
    void updateComment_ShouldUpdateAndReturnCommentResponse() {
        // Given
        Long userId = 10L;
        Long postId = 20L;
        Long commentId = 1L;
        String updatedContent = "Updated Comment";
        Comment mockComment = Comment.builder()
                .id(commentId)
                .postId(postId)
                .userId(userId)
                .userNickname("User1")
                .content("Old Comment")
                .isDeleted(false)
                .build();

        when(commentRepository.findById(commentId)).thenReturn(Optional.of(mockComment));
        when(commentRepository.save(any(Comment.class))).thenReturn(mockComment);

        // When
        CommentResponse response = commentService.updateComment(userId, postId, commentId, updatedContent);

        // Then
        assertNotNull(response);
        assertEquals("Updated Comment", response.getContent());
        verify(commentRepository, times(1)).findById(commentId);
        verify(commentRepository, times(1)).save(any(Comment.class));
    }

    @Test
    void deleteComment_ShouldSoftDeleteComment() {
        // Given
        Long userId = 10L;
        Long postId = 20L;
        Long commentId = 1L;
        Comment mockComment = Comment.builder()
                .id(commentId)
                .postId(postId)
                .userId(userId)
                .userNickname("User1")
                .content("Comment to Delete")
                .isDeleted(false)
                .build();

        when(commentRepository.findById(commentId)).thenReturn(Optional.of(mockComment));

        // When
        commentService.deleteComment(userId, postId, commentId);

        // Then
        assertTrue(mockComment.isDeleted());
        verify(commentRepository, times(1)).findById(commentId);
        verify(commentRepository, times(1)).save(mockComment);
    }
}
