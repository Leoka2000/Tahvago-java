package pt.tahvago.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import pt.tahvago.model.AppUser;
import pt.tahvago.model.Notification;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByRecipientOrderByCreatedAtDesc(AppUser recipient);
}