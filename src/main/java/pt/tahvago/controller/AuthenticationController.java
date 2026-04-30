package pt.tahvago.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import pt.tahvago.dto.User.ForgotPasswordDto;
import pt.tahvago.dto.User.LoginResponse;
import pt.tahvago.dto.User.LoginUserDto;
import pt.tahvago.dto.User.RegisterUserDto;
import pt.tahvago.dto.User.ResetPasswordDto;
import pt.tahvago.dto.User.VerifyResponse;
import pt.tahvago.dto.User.VerifyUserDto;
import pt.tahvago.exceptions.RegistrationException;
import pt.tahvago.model.AppUser;
import pt.tahvago.service.AuthenticationService;
import pt.tahvago.service.JwtService;

@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {

    private final JwtService jwtService;
    private final AuthenticationService authenticationService;

    public AuthenticationController(JwtService jwtService, AuthenticationService authenticationService) {
        this.jwtService = jwtService;
        this.authenticationService = authenticationService;
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody ForgotPasswordDto dto) {
        try {
            authenticationService.forgotPassword(dto.getEmail());
            return ResponseEntity.ok(Map.of("message", "Reset email sent successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordDto resetPasswordDto) {
        try {
            authenticationService.resetPassword(resetPasswordDto);
            return ResponseEntity.ok(Map.of("message", "Password has been reset successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/signup")
    public ResponseEntity<?> register(@RequestBody RegisterUserDto registerUserDto) {
        try {
            AppUser registeredUser = authenticationService.signup(registerUserDto);
            return ResponseEntity.ok(registeredUser);
        } catch (RegistrationException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("Registration failed: " + e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticate(@RequestBody LoginUserDto loginUserDto, HttpServletRequest request) {
        try {
            String clientIp = request.getHeader("X-Forwarded-For");
            if (clientIp == null || clientIp.isEmpty()) {
                clientIp = request.getRemoteAddr();
            }

            AppUser authenticatedUser = authenticationService.authenticate(loginUserDto, clientIp);
            String jwtToken = jwtService.generateToken(authenticatedUser);
            LoginResponse loginResponse = new LoginResponse(jwtToken, jwtService.getExpirationTime());

            return ResponseEntity.ok(loginResponse);

        } catch (RuntimeException e) {
            if (e.getMessage() != null && e.getMessage().contains("Too many attempts")) {
                return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(e.getMessage());
            }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verifyUser(@RequestBody VerifyUserDto verifyUserDto) {
        System.out.println("DEBUG -> Verifying Email: " + verifyUserDto.getEmail());
        System.out.println("DEBUG -> Received Code: " + verifyUserDto.getCode());

        try {
            AppUser user = authenticationService.verifyUser(verifyUserDto);
            String jwtToken = jwtService.generateToken(user);

            VerifyResponse response = new VerifyResponse(
                    jwtToken,
                    jwtService.getExpirationTime(),
                    "Account verified successfully");

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/resend")
    public ResponseEntity<?> resendVerificationCode(@RequestParam String email) {
        try {
            authenticationService.resendVerificationCode(email);
            return ResponseEntity.ok("Verification code sent");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout() {
        return ResponseEntity.ok("Logged out successfully");
    }
}