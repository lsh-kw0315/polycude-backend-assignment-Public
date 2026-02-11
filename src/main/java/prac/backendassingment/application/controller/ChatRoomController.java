package prac.backendassingment.application.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import prac.backendassingment.application.dto.ChatRoomCreateRequest;
import prac.backendassingment.domain.model.ChatRoom;
import prac.backendassingment.application.service.ChatService;
import prac.backendassingment.global.filter.CustomUserDetails;

import java.util.List;

@RestController
@RequestMapping("/api/chat/rooms")
@RequiredArgsConstructor
public class ChatRoomController {

    private final ChatService chatService;

    @PostMapping
    public ResponseEntity<ChatRoom> createChatRoom(@RequestBody ChatRoomCreateRequest request,
                                                   @AuthenticationPrincipal CustomUserDetails userDetails) {
        ChatRoom chatRoom = chatService.createChatRoom(request, userDetails.getId());
        return new ResponseEntity<>(chatRoom, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ChatRoom> getChatRoomDetails(@PathVariable("id") Long chatRoomId) {
        ChatRoom chatRoom = chatService.getChatRoomDetails(chatRoomId);
        return ResponseEntity.ok(chatRoom);
    }

    @GetMapping
    public ResponseEntity<List<ChatRoom>> getAllChatRooms(@RequestParam(defaultValue = "0", value = "page") int page,
                                                          @RequestParam(defaultValue = "10", value = "size") int size) {
        List<ChatRoom> chatRooms = chatService.getAllChatRooms(page, size);
        return ResponseEntity.ok(chatRooms);
    }

    @PostMapping("/{id}/join")
    public ResponseEntity<Void> joinChatRoom(@PathVariable("id") Long chatRoomId,
                                             @AuthenticationPrincipal CustomUserDetails userDetails) {
        chatService.joinChatRoom(chatRoomId, userDetails.getId());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/leave")
    public ResponseEntity<Void> leaveChatRoom(@PathVariable("id") Long chatRoomId,
                                              @AuthenticationPrincipal CustomUserDetails userDetails) {
        chatService.leaveChatRoom(chatRoomId, userDetails.getId());
        return ResponseEntity.ok().build();
    }
}
