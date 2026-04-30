package pt.tahvago.dto.Membership;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MembershipTierResponse {
    private Long id;
    private Integer tierLevel;
    private String tierName;
    private Double monthlyPrice;
    private Integer monthlyCreditAllotment;
    private String status;
}