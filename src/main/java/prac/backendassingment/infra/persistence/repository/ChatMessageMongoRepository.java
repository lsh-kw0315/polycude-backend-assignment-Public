package prac.backendassingment.infra.persistence.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import prac.backendassingment.infra.persistence.entity.ChatMessageDocument;

import java.util.List;

public interface ChatMessageMongoRepository extends MongoRepository<ChatMessageDocument, String> {
    List<ChatMessageDocument> findByChatRoomIdOrderByCreatedAtDesc(Long chatRoomId, Pageable pageable);
}
