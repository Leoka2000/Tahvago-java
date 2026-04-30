package pt.tahvago.dto.Membership.test;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
public class StartupMembershipDto {

    private Long id;
    private String name;
    private String description;
    private String website;
    private String industry;
    private String stage;
    private String foundingYear;
    private String companyLogo;
    private Integer teamSize;
    private String country;
    private Integer creditBalance;

    private Boolean onEvaluation;
    private Boolean accepted;
    private String evaluationStage;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    //  Instead of full AppUser → only ID
    private Long ownerId;

   
}