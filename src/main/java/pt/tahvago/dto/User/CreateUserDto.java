package pt.tahvago.dto.User;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateUserDto {
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String phone;
    private String taxId;
}