
package pt.tahvago.dto.Membership.test;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
public class AppUserMembershipDto {
    private Long id;
    private String firstName;
    private String lastName;
    private String fullName;
    private String email;
    private String username;
    private String role;
    private String profilePictureUrl;
    private String startupStatus;
    private Boolean acceptedTerms;
    private Boolean enabled;
    private LocalDateTime createdAt;
}