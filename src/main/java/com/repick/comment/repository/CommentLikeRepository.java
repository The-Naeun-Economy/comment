package com.repick.comment.repository;

import com.repick.comment.domain.Comment;
import com.repick.comment.domain.CommentLike;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentLikeRepository extends JpaRepository<CommentLike, Long> {

    Optional<CommentLike> findByUserIdAndPostIdAndCommentId(Long userId, Long postId, Comment comment);
}
