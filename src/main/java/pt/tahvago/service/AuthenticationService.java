package pt.tahvago.service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import pt.tahvago.dto.User.LoginUserDto;
import pt.tahvago.dto.User.RegisterUserDto;
import pt.tahvago.dto.User.ResetPasswordDto;
import pt.tahvago.dto.User.VerifyUserDto;
import pt.tahvago.exceptions.RegistrationException;
import pt.tahvago.model.AppUser;
import pt.tahvago.model.Membership;
import pt.tahvago.repository.MembershipRepository;
import pt.tahvago.repository.UserRepository;

@Service
public class AuthenticationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final MembershipRepository membershipRepository;

    private final EmailService emailService;

    private final Map<String, LockoutInfo> lockoutCache = new ConcurrentHashMap<>();
    private final int MAX_ATTEMPTS = 2;
    private final int LOCKOUT_DURATION_MINUTES = 1;

    public AuthenticationService(
            UserRepository userRepository,
            MembershipRepository membershipRepository,
            AuthenticationManager authenticationManager,
            PasswordEncoder passwordEncoder,
            EmailService emailService) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
        this.membershipRepository = membershipRepository;
    }

    public AppUser signup(RegisterUserDto input) {
        if (userRepository.findByEmail(input.getEmail()).isPresent()) {
            throw new RegistrationException("Email already exists");
        }

        AppUser user = new AppUser();
        // Set individual fields
        user.setFirstName(input.getFirstName());
        user.setLastName(input.getLastName());

        // Construct fullName so the DB column isn't null
        user.setFullName(input.getFirstName() + " " + input.getLastName());

        user.setEmail(input.getEmail());
        user.setPassword(passwordEncoder.encode(input.getPassword()));

        user.setUsername(input.getEmail());
        user.setEnabled(false);
        user.setVerificationCode(generateVerificationCode());
        user.setVerificationCodeExpiresAt(LocalDateTime.now().plusMinutes(15));

        sendVerificationEmail(user);
        return userRepository.save(user);
    }

    public void forgotPassword(String email) {
        AppUser user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with this email"));

        String resetString = generateRandomString(16);
        user.setVerificationCode(resetString);
        user.setVerificationCodeExpiresAt(LocalDateTime.now().plusMinutes(30));
        userRepository.save(user);

        sendResetPasswordEmail(user.getEmail(), resetString);
    }

    private String generateRandomString(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }

    private void sendResetPasswordEmail(String email, String resetString) {
        String resetLink = "http://localhost:4200/reset-password/" + resetString;
        String subject = "Password Reset Request";
        String htmlMessage = "<html><body>"
                + "<h3>Password Reset</h3>"
                + "<p>Click the link below to reset your password. This link expires in 30 minutes:</p>"
                + "<a href=\"" + resetLink + "\">Reset Password</a>"
                + "</body></html>";

        try {
            emailService.sendVerificationEmail(email, subject, htmlMessage);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    public AppUser authenticate(LoginUserDto input, String clientIp) {
        if (isBlocked(clientIp)) {
            throw new RuntimeException("Too many attempts. Please wait 1 minute.");
        }

        try {
            AppUser user = userRepository.findByEmail(input.getEmail())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            if (!user.isEnabled()) {
                throw new RuntimeException("Account not verified. Please verify your account.");
            }

            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            input.getEmail(),
                            input.getPassword()));

            lockoutCache.remove(clientIp);
            return user;

        } catch (Exception e) {
            registerFailedAttempt(clientIp);
            throw e;
        }
    }

    private boolean isBlocked(String ip) {
        LockoutInfo info = lockoutCache.get(ip);
        if (info == null)
            return false;
        if (info.attempts >= MAX_ATTEMPTS) {
            if (LocalDateTime.now().isBefore(info.lockoutEndTime))
                return true;
            lockoutCache.remove(ip);
        }
        return false;
    }

    private void registerFailedAttempt(String ip) {
        LockoutInfo info = lockoutCache.getOrDefault(ip, new LockoutInfo());
        info.attempts++;
        if (info.attempts >= MAX_ATTEMPTS) {
            info.lockoutEndTime = LocalDateTime.now().plusMinutes(LOCKOUT_DURATION_MINUTES);
        }
        lockoutCache.put(ip, info);
    }

    private static class LockoutInfo {
        int attempts = 0;
        LocalDateTime lockoutEndTime = LocalDateTime.now();
    }

    public AppUser verifyUser(VerifyUserDto dto) {
    if (dto.getEmail() == null || dto.getEmail().trim().isEmpty()) {
        throw new RuntimeException("Email is required for verification");
    }
    
    if (dto.getCode() == null || dto.getCode().trim().isEmpty()) {
        throw new RuntimeException("Verification code cannot be empty");
    }

    AppUser user = userRepository.findByEmail(dto.getEmail())
            .orElseThrow(() -> new RuntimeException("User not found with email: " + dto.getEmail()));

    if (user.getVerificationCode() == null || !user.getVerificationCode().equals(dto.getCode())) {
        throw new RuntimeException("Invalid verification code");
    }

    if (user.getVerificationCodeExpiresAt() == null ||
            user.getVerificationCodeExpiresAt().isBefore(LocalDateTime.now())) {
        throw new RuntimeException("Verification code expired");
    }

    user.setEnabled(true);
    user.setVerificationCode(null);
    user.setVerificationCodeExpiresAt(null);
    
    user = userRepository.save(user);

    if (membershipRepository.findByUserId(user.getId()).isEmpty()) {
        Membership membership = Membership.createFreeTier(user);
        membership.setUser(user);
        membershipRepository.save(membership);
        user.setMembership(membership);
    }

    return user;
}
    private String generateVerificationCodeString() {
        return String.valueOf(new Random().nextInt(900000) + 100000);
    }

    public void resendVerificationCode(String email) {
        Optional<AppUser> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isPresent()) {
            AppUser user = optionalUser.get();
            if (user.isEnabled()) {
                throw new RuntimeException("Account is already verified");
            }
            user.setVerificationCode(generateVerificationCode());
            user.setVerificationCodeExpiresAt(LocalDateTime.now().plusHours(1));
            sendVerificationEmail(user);
            userRepository.save(user);
        } else {
            throw new RuntimeException("User not found");
        }
    }

    private void sendVerificationEmail(AppUser user) {
        String subject = "Account Verification";
        String verificationCode = user.getVerificationCode();
        String htmlMessage = "<html>"
                + "<body style=\"font-family: Arial, sans-serif;\">"
                + "<div style=\"background-color: #f5f5f5; padding: 20px;\">"
                + "<h2 style=\"color: #333;\">Welcome!</h2>"
                + "<p>Please enter the code below to verify your account:</p>"
                + "<div style=\"background-color: #fff; padding: 20px; border-radius: 5px;\">"
                + "<h3 style=\"color: #007bff;\">" + verificationCode + "</h3>"
                + "</div>"
                + "</div>"
                + "</body>"
                + "</html>";

        try {
            emailService.sendVerificationEmail(user.getEmail(), subject, htmlMessage);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    private String generateVerificationCode() {
        Random random = new Random();
        return String.valueOf(random.nextInt(900000) + 100000);
    }

    public void resetPassword(ResetPasswordDto input) {
        AppUser user = userRepository.findByVerificationCode(input.getCode())
                .orElseThrow(() -> new RuntimeException("Invalid reset code"));

        if (user.getVerificationCodeExpiresAt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Reset code has expired");
        }

        user.setPassword(passwordEncoder.encode(input.getNewPassword()));
        user.setVerificationCode(null);
        user.setVerificationCodeExpiresAt(null);
        userRepository.save(user);
    }

}