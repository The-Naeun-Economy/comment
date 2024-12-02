package com.repick.comment.event;

import com.repick.comment.domain.Comment;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CommentCacheUpdate {

    private Long postId;
    private Comment comment;
    private Long userId;
    private String userNickname;
}
