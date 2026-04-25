package pt.tahvago.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import pt.tahvago.dto.Notification.NotificationResponseDto;
import pt.tahvago.model.AppUser;
import pt.tahvago.model.Notification;
import pt.tahvago.repository.NotificationRepository;
import pt.tahvago.repository.UserRepository;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<NotificationResponseDto>> getUserNotifications(@PathVariable Long userId) {
        AppUser user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<NotificationResponseDto> notifications = notificationRepository
                .findByRecipientOrderByCreatedAtDesc(user)
                .stream()
                .map(n -> new NotificationResponseDto(
                        n.getId(),
                        n.getMessage(),
                        n.isRead(),
                        n.getCreatedAt()
                ))
                .collect(Collectors.toList());

        return ResponseEntity.ok(notifications);
    }

    @PatchMapping("/{id}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable Long id) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notification not found"));
        
        notification.setRead(true);
        notificationRepository.save(notification);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNotification(@PathVariable Long id) {
        notificationRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
}