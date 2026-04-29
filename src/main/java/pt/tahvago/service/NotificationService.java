package pt.tahvago.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import pt.tahvago.dto.Notification.NotificationResponseDto;
import pt.tahvago.model.AppUser;
import pt.tahvago.model.Notification;
import pt.tahvago.repository.NotificationRepository;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationRepository notificationRepository;

    public List<NotificationResponseDto> getNotificationsForUser(AppUser user) {
        return notificationRepository.findByRecipientOrderByCreatedAtDesc(user)
                .stream()
                .map(n -> new NotificationResponseDto(
                        n.getId(),
                        n.getMessage(),
                        n.isRead(),
                        n.getCreatedAt()
                ))
                .collect(Collectors.toList());
    }

    public void createStageNotification(AppUser user, String newStage) {
        String message = switch (newStage.toLowerCase()) {
            case "registered" -> "You successfully registered! Now we are reviewing your application.";
            case "pending" -> "We evaluated your application. Soon you will have our final decision.";
            case "approved" -> "Congratulations! Your startup has been approved.";
            case "rejected" -> "We regret to inform you that your application was not accepted at this time.";
            default -> "Your evaluation status has been updated to: " + newStage;
        };

        Notification notification = new Notification();
        notification.setRecipient(user);
        notification.setMessage(message);
        notificationRepository.save(notification);
    }



    
}