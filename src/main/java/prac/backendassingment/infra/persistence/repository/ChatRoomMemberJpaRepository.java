package prac.backendassingment.infra.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import prac.backendassingment.infra.persistence.entity.ChatRoomEntity;
import prac.backendassingment.infra.persistence.entity.ChatRoomMemberEntity;
import prac.backendassingment.infra.persistence.entity.MemberEntity;

import java.util.List;

public interface ChatRoomMemberJpaRepository extends JpaRepository<ChatRoomMemberEntity, Long> {
    List<ChatRoomMemberEntity> findByChatRoom(ChatRoomEntity chatRoom);

    void deleteByChatRoomAndMemberIn(ChatRoomEntity chatRoom, List<MemberEntity> member);
}
