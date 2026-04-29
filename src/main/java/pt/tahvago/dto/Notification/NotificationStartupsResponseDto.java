package pt.tahvago.dto.Notification;

import java.util.List;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import pt.tahvago.dto.Startups.StartupResponse;

@Builder
@Getter
@Setter
public class NotificationStartupsResponseDto {

    private Long notificationId;
    private Long recipientId;
    private Long receiverStartupId; 
    private List<StartupResponse> startups;
    private List<StartupResponse> recipientStartups;
}