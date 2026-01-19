package pt.tahvago.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterUserDto {
    private String fullName;
    private String username;
    private String email;
    private String password;
    private String phone;
    private String taxId;
    private String countryCode;
    private boolean hasProfessionalRegistration;
    private String professionalOrder;
    private String professionalIdNumber;
    private boolean acceptedTerms;
}