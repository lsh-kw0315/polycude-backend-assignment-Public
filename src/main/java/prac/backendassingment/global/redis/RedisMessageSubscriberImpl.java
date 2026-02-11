package prac.backendassingment.global.redis;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;
import prac.backendassingment.application.dto.ChatMessageResponse;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisMessageSubscriberImpl implements RedisMessageSubscriber {

    private final ObjectMapper objectMapper;
    private final SimpMessageSendingOperations messagingTemplate; // For sending messages to WebSocket clients

    @Override
    public void handleMessage(String message) {
        log.info("Received message from Redis: {}", message);
        try {
            ChatMessageResponse chatMessage = objectMapper.readValue(message, ChatMessageResponse.class);
            // Send to WebSocket clients
            // 메시지를 레디스 pub/sub 으로부터 받았을 때, 해당 채팅방 구독 엔드포인트가 있으면 메시지가 전송된다.
            // 만약 없다면 전송되지 않는다.
            messagingTemplate.convertAndSend("/sub/chat/room/" + chatMessage.getChatRoomId(), chatMessage);
        } catch (JsonProcessingException e) {
            log.error("Error deserializing message from Redis: {}", e.getMessage());
        }
    }
}
