package prac.backendassingment.infra.persistence;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import prac.backendassingment.domain.model.ChatRoom;
import prac.backendassingment.domain.repository.ChatRoomRepository;
import prac.backendassingment.infra.persistence.entity.ChatRoomEntity;
import prac.backendassingment.infra.persistence.entity.ChatRoomMemberEntity;
import prac.backendassingment.infra.persistence.entity.MemberEntity;
import prac.backendassingment.infra.persistence.repository.ChatRoomJpaRepository;
import prac.backendassingment.infra.persistence.repository.ChatRoomMemberJpaRepository;
import prac.backendassingment.infra.persistence.repository.MemberJpaRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class ChatRoomRepositoryImpl implements ChatRoomRepository {

    private final ChatRoomJpaRepository chatRoomJpaRepository;
    private final MemberJpaRepository memberJpaRepository;
    private final ChatRoomMemberJpaRepository chatRoomMemberJpaRepository;

    @Override
    public ChatRoom save(ChatRoom chatRoom) {
        ChatRoomEntity chatRoomEntity = toEntity(chatRoom);
        ChatRoomEntity savedEntity = chatRoomJpaRepository.save(chatRoomEntity);

        ChatRoomMemberEntity chatRoomMember = ChatRoomMemberEntity.builder()
                .chatRoom(chatRoomEntity)
                .member(memberJpaRepository.getReferenceById(chatRoom.getMemberIds().get(0)))
                .build();
        chatRoomMemberJpaRepository.save(chatRoomMember);

        return toDomain(savedEntity);
    }

    @Override
    public Optional<ChatRoom> findById(Long id) {
        return chatRoomJpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public void deleteById(Long id) {
        chatRoomJpaRepository.deleteById(id);
    }

    @Override
    public java.util.List<ChatRoom> findAll() {
        return chatRoomJpaRepository.findAll().stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public java.util.List<ChatRoom> findAll(Pageable pageable) {
        return chatRoomJpaRepository.findAll(pageable).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void join(ChatRoom chatRoom) {
        List<ChatRoomMemberEntity> chatRoomMemberEntities = chatRoomMemberJpaRepository.findByChatRoom(
                chatRoomJpaRepository.getReferenceById(chatRoom.getId())
        );

        List<Long> exUsers = chatRoomMemberEntities.stream().map(
                chatRoomMemberEntity -> chatRoomMemberEntity.getMember().getId()
        ).toList();

        List<Long> newComers = chatRoom.getMemberIds().stream()
                .filter(memberId -> !exUsers.contains(memberId))
                .toList();

        for(Long user : newComers){
            ChatRoomMemberEntity chatRoomMember = ChatRoomMemberEntity.builder()
                    .chatRoom(chatRoomJpaRepository.getReferenceById(chatRoom.getId()))
                    .member(memberJpaRepository.getReferenceById(user))
                    .build();
            chatRoomMemberJpaRepository.save(chatRoomMember);
        }

    }

    @Override
    public void leave(ChatRoom chatRoom) {
        ChatRoomEntity proxy = chatRoomJpaRepository.getReferenceById(chatRoom.getId());

        List<ChatRoomMemberEntity> chatRoomMemberEntities = chatRoomMemberJpaRepository.findByChatRoom(proxy);

        List<Long> exUsers = chatRoomMemberEntities.stream().map(
                chatRoomMemberEntity -> chatRoomMemberEntity.getMember().getId()
        ).toList();

        List<MemberEntity> outsider = exUsers.stream()
                .filter(memberId -> !chatRoom.getMemberIds().contains(memberId))
                .map(memberJpaRepository::getReferenceById)
                .toList();


        chatRoomMemberJpaRepository.deleteByChatRoomAndMemberIn(proxy,outsider);

        List<ChatRoomMemberEntity> result = chatRoomMemberJpaRepository.findByChatRoom(proxy);

        if(result.isEmpty()){
            deleteById(chatRoom.getId());
        }

    }

    private ChatRoom toDomain(ChatRoomEntity entity) {
        List<ChatRoomMemberEntity> chatRoomMemberEntities = chatRoomMemberJpaRepository.findByChatRoom(
                chatRoomJpaRepository.getReferenceById(entity.getId())
        );
        return ChatRoom.builder()
                .id(entity.getId())
                .name(entity.getName())
                .memberIds(
                        chatRoomMemberEntities.stream().map(
                        chatRoomMemberEntity -> chatRoomMemberEntity.getMember().getId()).collect(Collectors.toList())
                )
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    private ChatRoomEntity toEntity(ChatRoom domain) {
        return ChatRoomEntity.builder()
                .id(domain.getId())
                .name(domain.getName())
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt())
                .build();
    }
}
