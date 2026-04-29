package pt.tahvago.model;

import java.time.LocalDateTime;
import jakarta.persistence.*;
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

    @Column(nullable = false)
    private Integer tierLevel = 0; 

    @Column(nullable = true)
    private String tierName = "Free Tier";

    @Column(nullable = true)
    private Double monthlyPrice = 0.0;

    @Column(nullable = false)
    private Integer monthlyCreditAllotment = 500;

    @Column(length = 500, nullable = true)
    private String description;

    @Column(nullable = true)
    private LocalDateTime startDate;

    @Column(nullable = true)
    private LocalDateTime nextBillingDate;

    @Column(nullable = true)
    private String status = "active";

    @Column(nullable = true)
    private String stripeSubscriptionId;

    public static Membership createFreeTier(AppUser user) {
        Membership membership = new Membership();
        membership.setUser(user);
        membership.setTierLevel(0);
        membership.setTierName("Free Tier");
        membership.setMonthlyCreditAllotment(500);
        membership.setMonthlyPrice(0.0);
        membership.setStatus("active");
        return membership;
    }

    @PrePersist
    protected void onCreate() {
        if (this.startDate == null) {
            this.startDate = LocalDateTime.now();
        }
        if (this.nextBillingDate == null) {
            this.nextBillingDate = LocalDateTime.now().plusMonths(1);
        }
    }
}