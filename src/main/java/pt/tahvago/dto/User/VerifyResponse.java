package pt.tahvago.dto.User;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class VerifyResponse {
    private String token;
    private long expiresIn;
    private String message;
}