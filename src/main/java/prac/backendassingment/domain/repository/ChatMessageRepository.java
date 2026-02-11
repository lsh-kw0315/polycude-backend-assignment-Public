package prac.backendassingment.domain.repository;

import org.springframework.data.domain.Pageable;
import prac.backendassingment.domain.model.ChatMessage;

import java.util.List;
import java.util.Optional;

public interface ChatMessageRepository {
    ChatMessage save(ChatMessage chatMessage);
    Optional<ChatMessage> findById(String id);
    List<ChatMessage> findByChatRoomId(Long chatRoomId, Pageable pageable);
}
