package pt.tahvago.dto.Startups;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateStartupStatusDto {
    private Long userId;
    private String startupStatus;
}