package prac.backendassingment.application.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import prac.backendassingment.application.dto.ChatMessageResponse;
import prac.backendassingment.application.service.ChatService;
import prac.backendassingment.global.filter.CustomUserDetails;

import java.security.Principal;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chat/room/messages")
public class ChatMessageController {

    private final ChatService chatService;

    // /pub/chat/message/{chatRoomId}
    @MessageMapping("/chat/message/{chatRoomId}")
    public void sendMessage(@DestinationVariable("chatRoomId") Long chatRoomId,
                            @Payload String messageContent,
                            SimpMessageHeaderAccessor headerAccessor) {
        log.info("채팅 메시지를 보내고 있습니다.");
        Principal principal = headerAccessor.getUser();
        if (principal == null) {
            log.warn("Unauthorized WebSocket message received: no principal found.");
            return;
        }

        // CustomUserDetails is set in StompInterceptor
        CustomUserDetails userDetails = (CustomUserDetails) ((org.springframework.security.authentication.UsernamePasswordAuthenticationToken) principal).getPrincipal();
        Long senderId = userDetails.getId();

        log.info("Received message from user {} in chat room {}: {}", senderId, chatRoomId, messageContent);
        chatService.sendChatMessage(chatRoomId, senderId, messageContent);
    }

    @GetMapping("/{id}")
    public List<ChatMessageResponse> getHistory(@RequestParam(value = "page",defaultValue = "0") int page,
                                                @RequestParam(value = "size", defaultValue = "10") int size,
                                                @PathVariable("id") Long roomId){
        return chatService.getChatHistory(roomId, page, size);
    }
}
