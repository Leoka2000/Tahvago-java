package pt.tahvago.dto.Membership;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MembershipTierUpdateRequest {
    private Long userId;
    private Integer tierLevel;
}