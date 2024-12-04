package com.repick.comment;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import com.repick.comment.dto.CommentResponse;
import com.repick.comment.repository.CommentCacheRepository;
import java.util.Collections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.redis.core.RedisTemplate;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import org.springframework.data.redis.core.ValueOperations;

// CommentCacheRepository 동작 테스트, Redis 캐싱 로직이 잘 작동하는지 확인
class CommentCacheRepositoryTest {

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private ValueOperations<String, Object> valueOperations;

    @InjectMocks
    private CommentCacheRepository commentCacheRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    }

    @Test
    void testSaveCacheData() {
        // given
        String cacheKey = "commentsInPost-cache::123";
        List<CommentResponse> commentResponses = Arrays.asList(
                CommentResponse.builder()
                        .id(1L)
                        .postId(123L)
                        .userId(1L)
                        .userNickname("user1")
                        .content("Test Comment")
                        .likeCount(5L)
                        .isDeleted(false)
                        .createdAt(LocalDateTime.now().toString())
                        .updatedAt(LocalDateTime.now().toString())
                        .build(),
                CommentResponse.builder()
                        .id(2L)
                        .postId(123L)
                        .userId(2L)
                        .userNickname("user2")
                        .content("Another Test Comment")
                        .likeCount(3L)
                        .isDeleted(false)
                        .createdAt(LocalDateTime.now().toString())
                        .updatedAt(LocalDateTime.now().toString())
                        .build()
        );

        // when
        commentCacheRepository.saveCacheData(cacheKey, commentResponses);

        // then
        verify(valueOperations, times(1)).set(cacheKey, commentResponses);
    }

    @Test
    void testGetCacheData() {
        // given
        String cacheKey = "commentsInPost-cache::123";
        List<CommentResponse> expectedData = Collections.singletonList(
                CommentResponse.builder()
                        .id(1L)
                        .postId(123L)
                        .userId(1L)
                        .userNickname("user1")
                        .content("Test Comment")
                        .likeCount(5L)
                        .isDeleted(false)
                        .createdAt(LocalDateTime.now().toString())
                        .updatedAt(LocalDateTime.now().toString())
                        .build()
        );

        // RedisTemplate Mock 설정
        when(redisTemplate.opsForValue().get(cacheKey)).thenReturn(expectedData);

        // when
        List<CommentResponse> result = commentCacheRepository.getCacheDate(cacheKey);

        // then
        assertThat(result).isEqualTo(expectedData);
    }

    @Test
    void testDeleteCacheData() {
        // given
        String cacheKey = "commentsInPost-cache::123";

        // when
        commentCacheRepository.deleteCacheData(cacheKey);

        // then
        verify(redisTemplate, times(1)).delete(cacheKey);
    }
}
