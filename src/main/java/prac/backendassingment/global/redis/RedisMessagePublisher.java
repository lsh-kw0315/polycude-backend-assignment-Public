package prac.backendassingment.global.redis;

import prac.backendassingment.application.dto.ChatMessageResponse;

/**
 * Redis Pub/Sub 에 웹소켓으로 들어온 채팅 메시지를 Publish
 */
public interface RedisMessagePublisher {
    void publish(ChatMessageResponse message);
}
