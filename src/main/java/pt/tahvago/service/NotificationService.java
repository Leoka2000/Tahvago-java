package pt.tahvago.service;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import pt.tahvago.model.AppUser;
import pt.tahvago.model.Notification;
import pt.tahvago.repository.NotificationRepository;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationRepository notificationRepository;

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