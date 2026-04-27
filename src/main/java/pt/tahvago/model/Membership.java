package pt.tahvago.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "memberships")
@Getter
@Setter
@NoArgsConstructor
public class Membership {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private AppUser user;

    @Column(nullable = true)
    private Integer tierLevel;

    @Column(nullable = true)
    private String tierName;

    @Column(nullable = true)
    private Double monthlyPrice;

    @Column(nullable = true)
    private Integer monthlyCreditAllotment;

    @Column(length = 500, nullable = true)
    private String description;

    @Column(nullable = true)
    private LocalDateTime startDate;

    @Column(nullable = true)
    private LocalDateTime nextBillingDate;

    @Column(nullable = true)
    private String status;

    @Column(nullable = true)
    private String stripeSubscriptionId;
}