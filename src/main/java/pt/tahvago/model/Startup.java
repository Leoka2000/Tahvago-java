package pt.tahvago.model;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "startup")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Startup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String website;
    private String industry;
    private String stage;

    @Column(name = "founding_year")
    private String foundingYear;
    @Column(name = "company_logo", nullable = true)
    private String companyLogo;

    @Column(name = "team_size")
    private Integer teamSize;

    private String country;

    @Column(nullable = false)
    @Builder.Default
    private Integer creditBalance = 500;

    @OneToMany(mappedBy = "startup", cascade = CascadeType.ALL)
    private List<CreditTransaction> transactions;

    @OneToMany(mappedBy = "receiver")
    private List<StartupInteraction> receivedInteractions;

    @Column(name = "on_evaluation")
    private Boolean onEvaluation;

    private Boolean accepted;

    @Column(name = "evaluation_stage")
    private String evaluationStage;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private AppUser owner;

    @OneToMany(mappedBy = "relatedStartup", cascade = CascadeType.ALL)
    private List<Notification> notifications;

    @ManyToMany(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JoinTable(name = "startup_conferences", joinColumns = @JoinColumn(name = "startup_id"), inverseJoinColumns = @JoinColumn(name = "conference_id"))
    @Builder.Default
    private Set<Conference> attendedConferences = new HashSet<>();

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}