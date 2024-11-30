package com.repick.comment.event;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class CommentCacheDelete {
    private Long postId;
}
