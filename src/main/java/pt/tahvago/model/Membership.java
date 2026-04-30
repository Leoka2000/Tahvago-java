package pt.tahvago.model;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;

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
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore // So not infinite recursion
    private AppUser user;
    @Column(nullable = false)
    private Integer tierLevel = 0;

    @Column(nullable = true)
    private String tierName = "Free Tier";

    @Column(nullable = true)
    private Double monthlyPrice = 0.0;

    @Column(nullable = false)
    private Integer monthlyCreditAllotment = 500;

    @Column(nullable = true)
    private LocalDateTime startDate;

    @Column(nullable = true)
    private LocalDateTime nextBillingDate;

    @Column(nullable = true)
    private String status = "active";

    public static Membership createFreeTier(AppUser user) {
        Membership m = new Membership();
        m.setUser(user); // ✅ SET FK
        m.setTierLevel(0);
        m.setTierName("Free Tier");
        m.setMonthlyCreditAllotment(500);
        m.setMonthlyPrice(0.0);
        m.setStatus("active");
        return m;
    }

    @PrePersist
    protected void onCreate() {
        if (startDate == null)
            startDate = LocalDateTime.now();
        if (nextBillingDate == null)
            nextBillingDate = LocalDateTime.now().plusMonths(1);
    }
}