package pt.tahvago.dto.Membership;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateMembershipDto {
    private Integer tierLevel;
    private Integer credits;
    private String tierName;
}