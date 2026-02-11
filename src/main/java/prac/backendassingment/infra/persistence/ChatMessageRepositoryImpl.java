package prac.backendassingment.infra.persistence;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import prac.backendassingment.domain.model.ChatMessage;
import prac.backendassingment.domain.repository.ChatMessageRepository;
import prac.backendassingment.infra.persistence.entity.ChatMessageDocument;
import prac.backendassingment.infra.persistence.repository.ChatMessageMongoRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class ChatMessageRepositoryImpl implements ChatMessageRepository {

    private final ChatMessageMongoRepository chatMessageMongoRepository;

    @Override
    public ChatMessage save(ChatMessage chatMessage) {
        ChatMessageDocument document = toDocument(chatMessage);
        ChatMessageDocument savedDocument = chatMessageMongoRepository.save(document);
        return toDomain(savedDocument);
    }

    @Override
    public Optional<ChatMessage> findById(String id) {
        return chatMessageMongoRepository.findById(id).map(this::toDomain);
    }

    @Override
    public List<ChatMessage> findByChatRoomId(Long chatRoomId, Pageable pageable) {
        return chatMessageMongoRepository.findByChatRoomIdOrderByCreatedAtDesc(chatRoomId, pageable)
                .stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    private ChatMessage toDomain(ChatMessageDocument document) {
        return ChatMessage.builder()
                .id(document.getId())
                .chatRoomId(document.getChatRoomId())
                .senderId(document.getSenderId())
                .senderUsername(document.getSenderUsername())
                .senderProfileUrl(document.getSenderProfileUrl())
                .message(document.getMessage())
                .createdAt(document.getCreatedAt())
                .build();
    }

    private ChatMessageDocument toDocument(ChatMessage domain) {
        return ChatMessageDocument.builder()
                .id(domain.getId())
                .chatRoomId(domain.getChatRoomId())
                .senderId(domain.getSenderId())
                .senderUsername(domain.getSenderUsername())
                .senderProfileUrl(domain.getSenderProfileUrl())
                .message(domain.getMessage())
                .createdAt(domain.getCreatedAt())
                .build();
    }
}
