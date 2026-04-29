package pt.tahvago.dto.User;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VerifyUserDto {

    private String email;

    @JsonProperty("verificationCode") // This ensures if frontend sends "verificationCode", it works
    private String code;
}