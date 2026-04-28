package pt.tahvago.dto;

import java.util.List;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Builder
@Setter
public class NotificationStartupsResponseDto {

    private Long notificationId;
    private Long recipientId;
    private List<StartupResponse> startups;
}