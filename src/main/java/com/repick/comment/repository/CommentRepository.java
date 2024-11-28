package com.repick.comment.repository;

import com.repick.comment.domain.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findByPostId(Long postId);

    List<Comment> findByPostIdAndIsDeletedFalse(Long postId);

    List<Comment> findByUserIdAndIsDeletedFalse(Long userId);
}
