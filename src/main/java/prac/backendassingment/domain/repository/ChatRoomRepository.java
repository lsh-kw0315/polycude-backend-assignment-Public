package prac.backendassingment.domain.repository;

import org.springframework.data.domain.Pageable;
import prac.backendassingment.domain.model.ChatRoom;

import java.util.List;
import java.util.Optional;

public interface ChatRoomRepository {
    ChatRoom save(ChatRoom chatRoom);
    Optional<ChatRoom> findById(Long id);
    void deleteById(Long id);
    List<ChatRoom> findAll();
    List<ChatRoom> findAll(Pageable pageable); // New method for paging

    void join(ChatRoom chatRoom);

    void leave(ChatRoom chatRoom);
}
