package com.edomingues.icalexchange365.repository;

import com.edomingues.icalexchange365.model.AuthorizationState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.util.concurrent.TimeUnit;

@Repository
public class RedisAuthorizationStateRepository implements AuthorizationStateRepository {

    private static final String KEY_AUTHORIZATION_STATE = "authorization-state";

    private RedisTemplate<String, Object> redisTemplate;
    private HashOperations<String, String, AuthorizationState> hashOperations;

    @Autowired
    public RedisAuthorizationStateRepository(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @PostConstruct
    private void init() {
        hashOperations = redisTemplate.opsForHash();
    }


    @Override
    public AuthorizationState load(String state) {
        return this.hashOperations.get(KEY_AUTHORIZATION_STATE, state);
    }

    @Override
    public void save(AuthorizationState authorizationState) {
        this.hashOperations.put(KEY_AUTHORIZATION_STATE, authorizationState.state, authorizationState);
        this.redisTemplate.expire(KEY_AUTHORIZATION_STATE, 1, TimeUnit.HOURS);
    }
}
