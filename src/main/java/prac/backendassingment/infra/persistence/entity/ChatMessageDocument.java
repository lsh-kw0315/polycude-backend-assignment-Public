package prac.backendassingment.infra.persistence.entity;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "chat_messages")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ChatMessageDocument {
    @Id
    private String id;

    private Long chatRoomId;
    private Long senderId;
    private String senderUsername;
    private String senderProfileUrl;
    private String message;

    @CreatedDate
    private LocalDateTime createdAt;
}
