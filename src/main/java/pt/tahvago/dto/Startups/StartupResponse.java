package pt.tahvago.dto.Startups;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StartupResponse {
    private Long id;
    private String name;
    private String description;
    private String website;
    private String industry;
    private String stage;
    private String foundiyngYear;
    private String companyLogo;
    private Integer teamSize;
    private String country;
    private Integer creditBalance;
    private Boolean onEvaluation;
    private Boolean accepted;
    private String evaluationStage;
    private Long userId;
}