package com.edomingues.icalexchange365.repository;

import com.edomingues.icalexchange365.model.AccessToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;

@Repository
public class RedisAccessTokenRepository implements AccessTokenRepository {

    private static final String KEY_ACCESS_TOKEN = "access-token";

    private RedisTemplate<String, Object> redisTemplate;
    private HashOperations<String, String, AccessToken> hashOperations;

    @Autowired
    public RedisAccessTokenRepository(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @PostConstruct
    private void init() {
        hashOperations = redisTemplate.opsForHash();
    }

    @Override
    public void save(AccessToken accessToken) {
        hashOperations.put(KEY_ACCESS_TOKEN, accessToken.userId, accessToken);
    }

    @Override
    public AccessToken load(String userId) {
        return hashOperations.get(KEY_ACCESS_TOKEN, userId);
    }

    @Override
    public void deleteById(String userId) {
        hashOperations.delete(KEY_ACCESS_TOKEN, userId);
    }
}
