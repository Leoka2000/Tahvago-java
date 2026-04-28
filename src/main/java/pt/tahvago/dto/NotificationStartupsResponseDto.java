package pt.tahvago.dto;

import java.util.List;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class NotificationStartupsResponseDto{
    private Long receiverId;
    private List<StartupResponse> startups;
}