package com.example.blog.configuaration;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@Configuration
@EnableCaching
public class RedisConfig {
    // @Bean
    // public RedisCacheConfiguration cacheConfiguration() {
    //     ObjectMapper mapper = new ObjectMapper();
    //     mapper.registerModule(new JavaTimeModule());
    //     mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    //     return RedisCacheConfiguration.defaultCacheConfig()
    //         .serializeValuesWith(
    //                 RedisSerializationContext.SerializationPair.fromSerializer(
    //                         new GenericJackson2JsonRedisSerializer(mapper)
    //                 )
    //         );
    // }

    // @Bean
    // public ObjectMapper objectMapper() {
    //     ObjectMapper mapper = new ObjectMapper();
    //     mapper.registerModule(new JavaTimeModule());
    //     mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    //     return mapper;
    // }

    @Bean
    public RedisTemplate redisTemplate(RedisConnectionFactory factory){
        RedisTemplate redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(factory);

        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new StringRedisSerializer());

        return redisTemplate;
    }
}
