package com.repick.comment.repository;

import com.repick.comment.domain.Comment;
import com.repick.comment.domain.CommentLike;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentLikeRepository extends JpaRepository<CommentLike, Long> {

    // 특정 댓글에 대해 좋아요 눌렀는지
    Optional<CommentLike> findByUserIdAndCommentId(Long userId, Comment comment);

    // 사용자가 좋아요 누른 모든 거
    List<CommentLike> findByUserId(Long userId);

}
