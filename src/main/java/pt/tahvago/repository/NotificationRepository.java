package pt.tahvago.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import pt.tahvago.model.AppUser;
import pt.tahvago.model.Notification;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByRecipientOrderByCreatedAtDesc(AppUser recipient);

    List<Notification> findAllByRecipientId(Long recipientId);

    
    List<Notification> findByRecipientIdOrderByCreatedAtDesc(Long recipientId);
}