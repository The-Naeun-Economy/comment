package com.repick.comment.service;

import com.repick.comment.dto.UpdateCommentUserNickName;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaServiceImpl implements KafkaService {
    @KafkaListener(id = "comment-nickname", topics = "updateusernickname")
    public void consume(UpdateCommentUserNickName updateCommentUserNickName) {
        System.out.println("Consumer updateusernickname : " + updateCommentUserNickName);
        //실제 동작 로직
    }
}
