package pt.tahvago.dto.GetAllUsersStartups;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class GetAllUsersStartupsDto {
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StartupDetailsDto {
        private Long id;
        private String name;
        private String description;
        private String website;
        private String industry;
        private String stage;
        private String foundingYear; 
        private Integer teamSize;
        private String country;
        private Integer creditBalance;
        private Boolean onEvaluation;
        private Boolean accepted;
        private String evaluationStage;
        private Long userId;
        private String companyLogo; 
    }
}