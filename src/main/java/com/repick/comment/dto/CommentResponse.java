package com.repick.comment.dto;

import com.repick.comment.domain.Comment;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentResponse {
    private Long id;
    private Long postId;
    private Long userId;
    private String userNickname;
    private String content;
    private Long likeCount;
    private Boolean isDeleted;
    private String createdAt;
    private String updatedAt;

    public CommentResponse(Long id, @NotNull Long postId, @NotNull Long userId, @NotNull String userNickname,
                           String content, Long likesCount, @NotNull boolean isDeleted, LocalDateTime createdAt) {
        this.id = id;
        this.postId = postId;
        this.userId = userId;
        this.userNickname = userNickname;
        this.content = content;
        this.likeCount = likesCount;
        this.isDeleted = isDeleted;
        this.createdAt = createdAt.toString();
    }


    public static CommentResponse from(Comment comment) {
        return new CommentResponse(
                comment.getId(),
                comment.getPostId(),
                comment.getUserId(),
                comment.getUserNickname(),
                comment.getContent(),
                comment.getLikesCount(),
                comment.isDeleted(),
                comment.getCreatedAt()
        );
    }

}
