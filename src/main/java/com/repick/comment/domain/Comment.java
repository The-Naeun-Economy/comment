package com.repick.comment.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Comment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Long id;

    @NotNull
    @Column(name = "post_id")
    private Long postId;

    @Column(name = "user_id")
    @NotNull
    private Long userId;

    @Column(name = "user_nickname")
    @NotNull
    private String userNickname;

    @Column(name = "comment_content", columnDefinition = "TEXT")
    private String content;

    @Column(name = "like_count")
    private Long likesCount = 0L;

    @Column(name = "view_count")
    private Long viewCount = 0L;

    @Column(name = "deleted")
    @NotNull
    private boolean isDeleted = false;

    public void softDelete() {
        this.isDeleted = true;
    }

    public void updateContent(String content) {
        this.content = content;
    }

    public void incrementPostLikesCount() {
        if (this.likesCount == null) {
            this.likesCount = 1L;
        } else {
            this.likesCount += 1;
        }
    }

    public void decrementLikeCount() {
        if (this.likesCount > 0) {
            this.likesCount -= 1;
        }
    }
}
