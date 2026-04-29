package pt.tahvago.dto.User;

import lombok.Builder;
import lombok.Getter;
import java.util.List;

import pt.tahvago.model.Membership;
import pt.tahvago.model.Startup;
import pt.tahvago.model.Notification;
import pt.tahvago.model.AppUser;

@Getter
@Builder
public class FullUserProfileResponse {

    private AppUser user;
    private Membership membership;
    private List<Startup> startups;
    private List<Notification> notifications;
}