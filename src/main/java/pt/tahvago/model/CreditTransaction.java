package pt.tahvago.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
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
@Table(name = "credit_transactions")
@Getter
@Setter
@NoArgsConstructor
public class CreditTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "startup_id", nullable = false)
    private Startup startup;

    @Column(nullable = false)
    private Integer amount; // Positive for grants, negative for spending

    @Column(nullable = false)
    private String transactionType; // INITIAL_GRANT, MONTHLY_RENEWAL, MESSAGE_SENT

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    private String description;
}