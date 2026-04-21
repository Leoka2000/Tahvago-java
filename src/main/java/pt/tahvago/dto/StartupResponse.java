package pt.tahvago.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StartupResponse {
    private Long id;
    private String name;
    private String description;
    private String website;
    private String industry;
    private String stage;
    private Integer foundingYear;
    private Integer teamSize;
    private String country;
    private Long userId; 
    private Boolean onEvaluation;
    private Boolean accepted;
}