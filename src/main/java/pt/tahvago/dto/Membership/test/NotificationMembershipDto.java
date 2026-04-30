package pt.tahvago.dto.Membership.test;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
public class NotificationMembershipDto {

    private Long id;
    private String message;
    private Boolean isRead;
    private LocalDateTime createdAt;

    //  Only IDs instead of full objects
    private Long recipientId;
    private Long startupId;
    private Long interactionId;
}