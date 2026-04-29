package pt.tahvago.dto.Membership;

import lombok.Builder;
import lombok.Getter;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class MembershipDto {
    private final Long id;
    private final Long userId;
    private final Integer tierLevel;
    private final String tierName;
    private final Double monthlyPrice;
    private final Integer monthlyCreditAllotment;
    private final String description;
    private final LocalDateTime startDate;
    private final LocalDateTime nextBillingDate;
    private final String status;
    private final String stripeSubscriptionId;
}