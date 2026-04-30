package pt.tahvago.dto.Membership.test;

import lombok.Builder;
import lombok.Getter;
import java.util.List;

@Getter
@Builder
public class FullUserProfileResponse {

    private AppUserMembershipDto user;
    private MembershipMembershipDto membership;
    private List<StartupMembershipDto> startups;
   
}