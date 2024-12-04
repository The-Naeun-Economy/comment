package com.repick.comment.repository;

import com.repick.comment.dto.CommentResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CommentCacheRepository implements CacheRepository {

    public static final String CACHE_KEY = "commentsInPost-cache";
    private final String cacheKey = CACHE_KEY;

    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public String getCacheKey() {
        return cacheKey;
    }

    @Override
    public List<CommentResponse> getCacheDate(String cacheKey) {
        return (List<CommentResponse>) redisTemplate.opsForValue().get(cacheKey);
    }

    @Override
    public void saveCacheData(String cacheKey, Object data) {
        redisTemplate.opsForValue().set(cacheKey, data);
    }

    @Override
    public void deleteCacheData(String cacheKey) {
        redisTemplate.delete(cacheKey);
    }

    // 좋아요 캐시 키 생성
    public String generateLikeCacheKey(Long commentId) {
        return String.format("%s::%d::likes", CACHE_KEY, commentId);
    }

    // 좋아요 수 가져오기
    public Long getLikeCount(Long commentId) {
        String cacheKey = generateLikeCacheKey(commentId);
        Object cachedValue = redisTemplate.opsForValue().get(cacheKey);
        return cachedValue != null ? (Long) cachedValue : 0L;
    }

    // 좋아요 수 증가
    public void incrementLikeCount(Long commentId) {
        String cacheKey = generateLikeCacheKey(commentId);
        redisTemplate.opsForValue().increment(cacheKey);
    }

    // 좋아요 수 감소
    public void decrementLikeCount(Long commentId) {
        String cacheKey = generateLikeCacheKey(commentId);
        redisTemplate.opsForValue().decrement(cacheKey);
    }
}
