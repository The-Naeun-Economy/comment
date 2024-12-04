package com.repick.comment.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GetMyLikedCommentResponse {

    private Long id;
    private String content;
    private Long likeCount;
}
