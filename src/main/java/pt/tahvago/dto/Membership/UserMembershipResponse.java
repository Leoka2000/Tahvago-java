package pt.tahvago.dto.Membership;

import java.util.List;
import lombok.Builder;
import lombok.Data;
import pt.tahvago.model.AppUser;
import pt.tahvago.model.Membership;
import pt.tahvago.model.Startup;

@Data
@Builder
public class UserMembershipResponse {
    private AppUser user;
    private Membership membership;
    private List<Startup> startups;
}