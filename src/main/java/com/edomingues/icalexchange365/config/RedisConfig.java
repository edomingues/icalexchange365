package com.edomingues.icalexchange365.config;

import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericToStringSerializer;
import redis.clients.jedis.JedisPoolConfig;

import java.net.URI;
import java.net.URISyntaxException;

@Configuration
@ComponentScan("com.edomingues.icalexchange365")
public class RedisConfig {

    @Value("${redis.url}")
    private String envRedisUrl;

    @Bean
    public JedisConnectionFactory jedisConnectionFactory() {

        URI redisUrl;
        try {
            redisUrl = new URI(envRedisUrl);
        } catch (URISyntaxException e) {
            e.printStackTrace();
            throw new BeanCreationException(String.format("Failed parsing Redis URL '%s'", envRedisUrl), e);
        }

        JedisPoolConfig poolConfig = new JedisPoolConfig();
        //poolConfig.maxActive = 10;
        poolConfig.setMaxIdle(5);
        poolConfig.setMinIdle(1);
        poolConfig.setTestOnBorrow(true);
        poolConfig.setTestOnReturn(true);
        poolConfig.setTestWhileIdle(true);
        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration(redisUrl.getHost(), redisUrl.getPort());
        redisStandaloneConfiguration.setPassword(RedisPassword.of(redisUrl.getUserInfo().split(":", 2)[1]));
        return new JedisConnectionFactory(redisStandaloneConfiguration, JedisClientConfiguration.builder().usePooling().poolConfig(poolConfig).build());

    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate() {
        final RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(jedisConnectionFactory());
        template.setValueSerializer(new GenericToStringSerializer<>(Object.class));
        return template;
    }
}
