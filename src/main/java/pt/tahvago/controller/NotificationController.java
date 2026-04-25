package pt.tahvago.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
import pt.tahvago.service.NotificationService;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final NotificationRepository notificationRepository;

    @GetMapping("/me")
    public ResponseEntity<List<NotificationResponseDto>> getMyNotifications() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !(authentication.getPrincipal() instanceof AppUser)) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        AppUser currentUser = (AppUser) authentication.getPrincipal();
        return ResponseEntity.ok(notificationService.getNotificationsForUser(currentUser));
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

    @GetMapping("/me/unread-count")
    public ResponseEntity<Long> getUnreadCount() {
        AppUser user = (AppUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        long count = notificationRepository.findByRecipientOrderByCreatedAtDesc(user)
                .stream()
                .filter(n -> !n.isRead())
                .count();
        return ResponseEntity.ok(count);
    }
}