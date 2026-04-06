package pt.tahvago.dto;

import lombok.Data;

@Data
public class StartupCreateRequest {
    private String name;
    private String description;
    private String website;
    private String logoUrl;
    private String industry;
    private String stage;
    private Integer foundingYear;
    private Integer teamSize;
    private String country;
}