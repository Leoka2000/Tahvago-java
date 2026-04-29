package pt.tahvago.dto.User;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminCreateUserDto {
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String role;
}