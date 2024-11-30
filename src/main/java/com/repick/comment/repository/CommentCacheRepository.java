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
}
