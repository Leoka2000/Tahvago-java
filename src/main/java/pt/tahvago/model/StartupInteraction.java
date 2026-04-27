package pt.tahvago.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "startup_interactions")
@Getter
@Setter
@NoArgsConstructor
public class StartupInteraction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "sender_startup_id", nullable = false)
    private Startup sender;

    @ManyToOne
    @JoinColumn(name = "receiver_startup_id", nullable = false)
    private Startup receiver;

    @Column(nullable = true)
    private String type; 

    @Column(nullable = false)
    private String title; 

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false)
    private String status; // PENDING, ACCEPTED, REJECTED

    @Column(nullable = false)
    private LocalDateTime sentAt = LocalDateTime.now();
}