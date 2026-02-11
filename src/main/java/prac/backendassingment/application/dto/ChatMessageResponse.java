package prac.backendassingment.application.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageResponse {
    private String id;
    private Long chatRoomId;
    private Long senderId;
    private String senderUsername;
    private String senderProfileUrl;
    private String message;
    private LocalDateTime createdAt;
}
