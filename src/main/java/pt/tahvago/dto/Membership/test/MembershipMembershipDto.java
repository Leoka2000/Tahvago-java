
package pt.tahvago.dto.Membership.test;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
public class MembershipMembershipDto {

    private Long id;
    private String tierName;
    private Integer tierLevel;
    private String status;
    private Double monthlyPrice;
    private Integer monthlyCreditAllotment;

    private LocalDateTime startDate;
    private LocalDateTime nextBillingDate;

    // Only reference, not full user
    private Long userId;
}