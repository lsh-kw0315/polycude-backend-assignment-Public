package prac.backendassingment.global.redis;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;
import prac.backendassingment.application.dto.ChatMessageResponse;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisMessagePublisherImpl implements RedisMessagePublisher {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ChannelTopic channelTopic;

    @Override
    public void publish(ChatMessageResponse message) {
        try {
           //만약 redis 설정에서 별도의 직렬화 설정을 하지 않았다면, 여기서 JSON 형식으로 직렬화를 해줘야 한다.
            //ChatMessageResponse 객체를 publish 한 경우, 지정한 토픽으로 전송
            redisTemplate.convertAndSend(channelTopic.getTopic(), message);
            log.info("Message published to Redis topic {}: {}", channelTopic.getTopic(), message);
        } catch (Exception e) {
            log.error("Error for Redis: {}", e.getMessage());
        }
    }
}
