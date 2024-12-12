package com.repick.comment.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentEvent {
    private Long postId;
    private Long commentId;
    private String action;  // "CREATE", "DELETE"
}