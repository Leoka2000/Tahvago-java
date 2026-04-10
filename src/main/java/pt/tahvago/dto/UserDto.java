package pt.tahvago.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDto {
    private Long id;
    private String fullName;
    private String email;
    private String username;
    private String role;
    private String profilePictureUrl;
    private String startupStatus;

    public UserDto() {}
}