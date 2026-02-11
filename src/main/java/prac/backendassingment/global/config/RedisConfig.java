package prac.backendassingment.global.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.lettuce.core.ClientOptions;
import io.lettuce.core.protocol.ProtocolVersion;
import org.springframework.boot.autoconfigure.data.redis.LettuceClientConfigurationBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import prac.backendassingment.global.redis.RedisMessagePublisher;
import prac.backendassingment.global.redis.RedisMessagePublisherImpl;
import prac.backendassingment.global.redis.RedisMessageSubscriber;

@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        // Jackson2JsonRedisSerializer에 커스텀 ObjectMapper 주입
        // Redis에 데이터가 넘어갈 때 여기서 설정한대로 직렬화를 수행
        // Redis에 데이터를 넘길 때 직렬화를 해서 넘기면 이중 직렬화 문제 발생
        Jackson2JsonRedisSerializer<Object> serializer = new Jackson2JsonRedisSerializer<>(objectMapper(), Object.class);

        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(serializer); // Use Jackson for JSON serialization
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(serializer); // Use Jackson for JSON serialization
        return template;
    }

    @Bean
    public RedisMessageListenerContainer redisMessageListenerContainer(
            RedisConnectionFactory connectionFactory,
            MessageListenerAdapter listenerAdapter,
            ChannelTopic channelTopic) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.addMessageListener(listenerAdapter, channelTopic);
        return container;
    }

    @Bean
    public MessageListenerAdapter listenerAdapter(RedisMessageSubscriber subscriber) {
        MessageListenerAdapter adapter = new MessageListenerAdapter(subscriber, "handleMessage");
        // 이 설정을 추가해야 Redis Pub/Sub에서 온 byte 배열 데이터를 String으로 변환한 형태로 handleMessage에 전달합니다.
        // 위와 같은 JSON String을 Object Mapper로 MessageSubscriber(리스너)가 직접 변환할 수 있게 함.
        adapter.setSerializer(new StringRedisSerializer());
        return adapter;
    }

    @Bean
    public ChannelTopic channelTopic() {
        return new ChannelTopic("chat-topic"); // Define your Redis channel topic
    }

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.registerModule(new JavaTimeModule()); // Register JavaTimeModule for LocalDateTime serialization
        return objectMapper;
    }
}
