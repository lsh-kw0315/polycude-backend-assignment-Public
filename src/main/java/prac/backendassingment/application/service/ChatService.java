package prac.backendassingment.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import prac.backendassingment.application.dto.ChatMessageResponse;
import prac.backendassingment.application.dto.ChatRoomCreateRequest;
import prac.backendassingment.domain.model.ChatMessage;
import prac.backendassingment.domain.model.ChatRoom;
import prac.backendassingment.domain.model.Member;
import prac.backendassingment.domain.repository.ChatMessageRepository;
import prac.backendassingment.domain.repository.ChatRoomRepository;
import prac.backendassingment.domain.repository.MemberRepository;
import prac.backendassingment.global.redis.RedisMessagePublisher;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final MemberRepository memberRepository;
    private final RedisMessagePublisher redisMessagePublisher;

    @Transactional
    public ChatRoom createChatRoom(ChatRoomCreateRequest request, Long creatorId) {
        // Validate creator
        memberRepository.findById(creatorId)
                .orElseThrow(() -> new IllegalArgumentException("채팅방 생성 요청 유저가 존재하지 않습니다."));

        ChatRoom newChatRoom = ChatRoom.builder()
                .name(request.getName())
                .memberIds(Collections.singletonList(creatorId)) // Creator is the first member
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        return chatRoomRepository.save(newChatRoom);
    }

    @Transactional
    public void joinChatRoom(Long chatRoomId, Long memberId) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new IllegalArgumentException("채팅방이 존재하지 않습니다."));
        memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("참여 요청 유저가 존재하지 않습니다."));

        if (!chatRoom.getMemberIds().contains(memberId)) {
            chatRoom.getMemberIds().add(memberId);
            chatRoomRepository.join(chatRoom); // Update chat room with new member
        } else {
            log.warn("Member {} is already in chat room {}", memberId, chatRoomId);
        }
    }

    @Transactional
    public void leaveChatRoom(Long chatRoomId, Long memberId) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new IllegalArgumentException("채팅방이 존재하지 않습니다."));
        memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("나가기 요청 유저가 존재하지 않습니다."));

        if (!chatRoom.getMemberIds().contains(memberId)) {
            log.warn("Member {} is not in chat room {}", memberId, chatRoomId);
            return;
        }

        chatRoom.getMemberIds().remove(memberId);
        chatRoomRepository.leave(chatRoom); // Update chat room

    }

    @Transactional
    public ChatMessageResponse sendChatMessage(Long chatRoomId, Long senderId, String messageContent) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new IllegalArgumentException("채팅방이 존재하지 않습니다."));
        Member sender = memberRepository.findById(senderId)
                .orElseThrow(() -> new IllegalArgumentException("메시지 전송 유저가 존재하지 않습니다."));

        if (!chatRoom.getMemberIds().contains(senderId)) {
            throw new IllegalArgumentException("메시지 전송 유저는 채팅방에 참여하고 있지 않습니다.");
        }

        ChatMessage chatMessage = ChatMessage.builder()
                .chatRoomId(chatRoomId)
                .senderId(senderId)
                .senderUsername(sender.getUsername())
                .senderProfileUrl(sender.getProfileUrl())
                .message(messageContent)
                .createdAt(LocalDateTime.now())
                .build();

        ChatMessage savedMessage = chatMessageRepository.save(chatMessage);

        ChatMessageResponse response = ChatMessageResponse.builder()
                .id(savedMessage.getId())
                .chatRoomId(savedMessage.getChatRoomId())
                .senderId(savedMessage.getSenderId())
                .senderUsername(sender.getUsername())
                .senderProfileUrl(sender.getProfileUrl())
                .message(savedMessage.getMessage())
                .createdAt(savedMessage.getCreatedAt())
                .build();

        redisMessagePublisher.publish(response); // Publish message to Redis
        return response;
    }

    @Transactional(readOnly = true)
    public List<ChatMessageResponse> getChatHistory(Long chatRoomId, int page, int size) {
        chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new IllegalArgumentException("채팅방이 존재하지 않습니다."));
        Pageable pageable = PageRequest.of(page, size);
        return chatMessageRepository.findByChatRoomId(chatRoomId, pageable).stream()
                .map(message -> {
                    return ChatMessageResponse.builder()
                            .id(message.getId())
                            .chatRoomId(message.getChatRoomId())
                            .senderId(message.getSenderId())
                            .senderUsername(message.getSenderUsername())
                            .senderProfileUrl(message.getSenderProfileUrl())
                            .message(message.getMessage())
                            .createdAt(message.getCreatedAt())
                            .build();
                })
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ChatRoom> getAllChatRooms(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return chatRoomRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public ChatRoom getChatRoomDetails(Long chatRoomId) {
        return chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new IllegalArgumentException("채팅방이 존재하지 않습니다."));
    }
}
