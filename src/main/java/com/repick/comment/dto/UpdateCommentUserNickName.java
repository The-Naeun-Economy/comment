package com.repick.comment.dto;

import lombok.Data;

@Data
public class UpdateCommentUserNickName {
    private Long userId;
    private String nickName;
}
