package pt.tahvago.dto.Notification;

import java.time.LocalDateTime;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class NotificationResponseDto {
    private Long id;
    private String message;
    private boolean isRead;
    private LocalDateTime createdAt;

    public NotificationResponseDto(Long id, String message, boolean isRead, LocalDateTime createdAt) {
        this.id = id;
        this.message = message;
        this.isRead = isRead;
        this.createdAt = createdAt;
    }
}