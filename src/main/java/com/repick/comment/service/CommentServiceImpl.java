package com.repick.comment.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.repick.comment.domain.Comment;
import com.repick.comment.domain.CommentEvent;
import com.repick.comment.domain.CommentLike;
import com.repick.comment.dto.CommentLikeResponse;
import com.repick.comment.dto.CommentRequest;
import com.repick.comment.dto.CommentResponse;
import com.repick.comment.dto.GetMyLikedCommentResponse;
import com.repick.comment.mapper.CommentMapper;
import com.repick.comment.repository.CommentCacheRepository;
import com.repick.comment.repository.CommentLikeRepository;
import com.repick.comment.repository.CommentRepository;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final KafkaTemplate<String, byte[]> kafkaTemplate;
    private final ObjectMapper objectMapper;

    private final CommentRepository commentRepository;
    private final CommentLikeRepository commentLikeRepository;
    private final CommentCacheRepository commentCacheRepository;

    @Override
    @Transactional
    public CommentResponse createComment(Long userId, String userNickname, Long postId, CommentRequest request) {

        if (postId == null || postId <= 0) {
            throw new IllegalArgumentException("Invalid post ID");
        }

        // 댓글 생성
        Comment comment = Comment.builder()
                .postId(postId)
                .userId(userId)
                .userNickname(userNickname)
                .content(request.getContent())
                .isDeleted(false)
                .build();
        Comment savedComment = commentRepository.save(comment);

        // Kafka 이벤트 생성 및 전송
        CommentEvent event = new CommentEvent(
                savedComment.getPostId(),
                savedComment.getId(),
                "CREATE"
        );

        try {
            byte[] message = objectMapper.writeValueAsBytes(event); // JSON 직렬화
            kafkaTemplate.send("comment-topic", message);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Kafka 메시지 직렬화 실패", e);
        }

        Long likeCount = 0L;

        return CommentMapper.toResponse(savedComment, likeCount);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommentResponse> getCommentsByPostId(Long postId) {
        List<Comment> comments = commentRepository.findByPostIdAndIsDeletedFalse(postId);

        // 댓글 ID 리스트 생성
        List<Long> commentIds = comments.stream()
                .map(Comment::getId)
                .toList();

        // 좋아요 수를 캐시에서 일괄 조회
        Map<Long, Long> likeCounts = commentIds.stream()
                .collect(Collectors.toMap(
                        commentId -> commentId,
                        commentCacheRepository::getLikeCount // 캐시에서 좋아요 수 가져오기
                ));

        // 댓글과 좋아요 수를 함께 매핑
        return comments.stream()
                .map(comment -> {
                    Long likeCount = likeCounts.getOrDefault(comment.getId(), 0L); // 캐시에서 가져온 좋아요 수 활용
                    return CommentMapper.toResponse(comment, likeCount);
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommentResponse> getMyComments(Long userId) {
        // 댓글 조회
        List<Comment> comments = commentRepository.findByUserIdAndIsDeletedFalse(userId);

        // 좋아요 수 조회 및 매핑
        return comments.stream()
                .map(comment -> {
                    Long likeCount = commentCacheRepository.getLikeCount(comment.getId()); // 캐시에서 좋아요 수 조회
                    return CommentMapper.toResponse(comment, likeCount); // 좋아요 수와 함께 매핑
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CommentResponse updateComment(Long userId, Long postId, Long commentId, String content) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("Comment not found"));

        validateUserAuthorization(comment, userId);

        if (!comment.getPostId().equals(postId)) {
            throw new IllegalArgumentException("Post ID mismatch");
        }

        comment.updateContent(content);

        // 변경된 댓글 저장
        Comment updatedComment = commentRepository.save(comment);

        // 좋아요 수 조회
        Long likeCount = commentCacheRepository.getLikeCount(updatedComment.getId());

        // 업데이트된 댓글과 좋아요 수 반환
        return CommentMapper.toResponse(updatedComment, likeCount);
    }


    @Override
    @Transactional
    public void deleteComment(Long userId, Long postId, Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("Comment not found"));

        // Kafka 이벤트 생성 및 전송
        CommentEvent event = new CommentEvent(
                comment.getPostId(),
                commentId,
                "DELETE"
        );

        try {
            byte[] message = objectMapper.writeValueAsBytes(event); // JSON 직렬화
            kafkaTemplate.send("comment-topic", message);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Kafka 메시지 직렬화 실패", e);
        }

        validateUserAuthorization(comment, userId);

        if (!comment.getPostId().equals(postId)) {
            throw new IllegalArgumentException("Post ID mismatch");
        }

        // 이미 삭제된 상태인지 확인
        if (comment.isDeleted()) {
            throw new IllegalStateException("Comment is already deleted");
        }

        // 댓글 삭제 (Soft Delete)
        comment.softDelete();
        commentRepository.save(comment);
    }

    @Override
    @Transactional
    public CommentLikeResponse toggleLike(Long id, Long userId, String userNickname) {

        // 댓글 조회
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Comment not found"));

        if (comment.getUserId().equals(userId)) {
            throw new IllegalArgumentException("You cannot like your own post.");
        }

        // 좋아요 여부 확인
        Optional<CommentLike> existingLike = commentLikeRepository.findByUserIdAndCommentId(userId, comment);

        boolean isLiked;

        if (existingLike.isEmpty()) {
            // 좋아요 추가
            CommentLike newLike = CommentLike.builder()
                    .userId(userId)
                    .userNickname(userNickname)
                    .commentId(comment)
                    .build();
            commentLikeRepository.save(newLike);

            commentCacheRepository.incrementLikeCount(id);
            isLiked = true;
        } else {
            // 좋아요 취소
            CommentLike like = existingLike.get();
            commentLikeRepository.delete(like);

            commentCacheRepository.decrementLikeCount(id);
            isLiked = false;
        }

        // 캐시에서 좋아요 수 가져오기
        Long updatedLikeCount = commentCacheRepository.getLikeCount(id);

        // 데이터베이스의 좋아요 수와 동기화
        comment.syncLikesCount(updatedLikeCount);
        commentRepository.save(comment);

        return new CommentLikeResponse(isLiked, updatedLikeCount);
    }

    @Override
    public List<GetMyLikedCommentResponse> getMyLikedComments(Long userId) {

        List<CommentLike> likedComments = commentLikeRepository.findByUserId(userId);

        return likedComments.stream()
                .map(like -> new GetMyLikedCommentResponse(
                        like.getCommentId().getId(),
                        like.getCommentId().getContent(),
                        like.getCommentId().getLikesCount()
                ))
                .collect(Collectors.toList());
    }


    // 사용자 검증 로직
    private void validateUserAuthorization(Comment comment, Long userId) {
        if (!comment.getUserId().equals(userId)) {
            throw new IllegalArgumentException("You are not authorized to perform this action on this comment");
        }
    }
}
