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

    /* ================= OWNER SIDE ================= */
    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private AppUser user;

    private Integer tierLevel = 0;

    private String tierName = "Free Tier";

    private Double monthlyPrice = 0.0;

    private Integer monthlyCreditAllotment = 500;

    private LocalDateTime startDate;

    private LocalDateTime nextBillingDate;

    private String status = "active";

    /* ================= FACTORY ================= */

    public static Membership createFreeTier(AppUser user) {
        Membership m = new Membership();
        m.setUser(user);
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