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

    @Column(name = "user_id")
    @NotNull
    private Long userId;

    @Column(name = "user_nickname")
    @NotNull
    private String userNickname;

    @Column(name = "comment_content")
    private String content;

    @Column(name = "like_count")
    private Long likesCount = 0L;

    @Column(name = "view_count")
    private Long viewCount = 0L;

    @Column(name = "deleted")
    @NotNull
    private boolean deleted = false;
}
