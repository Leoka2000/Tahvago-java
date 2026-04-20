package pt.tahvago.service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import pt.tahvago.dto.CreateUserDto;
import pt.tahvago.model.AppUser;
import pt.tahvago.repository.UserRepository;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final String uploadDir = "uploads/profiles/";



    public UserService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<AppUser> allUsers() {
        List<AppUser> users = new ArrayList<>();
        userRepository.findAll().forEach(users::add);
        return users;
    }

    @Transactional
    public AppUser updateProfilePicture(Long userId, MultipartFile file) {
        AppUser user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        try {
            Path root = Paths.get(uploadDir);
            if (!Files.exists(root)) {
                Files.createDirectories(root);
            }

            if (user.getProfilePictureUrl() != null) {
                try {
                    String oldFileName = user.getProfilePictureUrl().replace("/uploads/profiles/", "");
                    Path oldFilePath = root.resolve(oldFileName);
                    Files.deleteIfExists(oldFilePath);
                } catch (Exception e) {
                    System.err.println("Failed to delete old profile picture: " + e.getMessage());
                }
            }

            String filename = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
            Files.copy(file.getInputStream(), root.resolve(filename), StandardCopyOption.REPLACE_EXISTING);

            String fileUrl = "/uploads/profiles/" + filename;
            user.setProfilePictureUrl(fileUrl);
            return userRepository.save(user);
        } catch (Exception e) {
            throw new RuntimeException("Could not store the file. Error: " + e.getMessage());
        }
    }

    @Transactional
    public AppUser updateUser(Long userId, String fullName, String email) {
        AppUser user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (fullName != null && !fullName.isBlank()) {
            user.setFullName(fullName);
        }

        if (email != null && !email.isBlank()) {
            Optional<AppUser> existingUser = userRepository.findByEmail(email);
            if (existingUser.isPresent() && !existingUser.get().getId().equals(userId)) {
                throw new RuntimeException("Email already taken");
            }
            user.setEmail(email);
        }

        return userRepository.save(user);
    }

    @Transactional
    public void changePassword(Long userId, String currentPassword, String newPassword, String confirmPassword) {
        AppUser user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new RuntimeException("Current password is incorrect");
        }

        if (!newPassword.equals(confirmPassword)) {
            throw new RuntimeException("New password and confirmation do not match");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    public void requestPasswordReset(String email) {
        AppUser user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Email not found"));

        String resetCode = generateRandomString(16);
        user.setVerificationCode(resetCode);
        user.setVerificationCodeExpiresAt(LocalDateTime.now().plusMinutes(30));
        userRepository.save(user);

        sendResetEmail(user.getEmail(), resetCode);
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

    private void sendResetEmail(String email, String code) {
        String resetLink = "http://localhost:4200/reset-password/" + code;
        System.out.println("Sending email to: " + email);
        System.out.println("Reset Link: " + resetLink);
    }


    @Transactional
public AppUser deleteProfilePicture(Long userId) {
    AppUser user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));

    if (user.getProfilePictureUrl() != null) {
        try {
            Path root = Paths.get(uploadDir);
            String filename = user.getProfilePictureUrl().replace("/uploads/profiles/", "");
            Path filePath = root.resolve(filename);
            
            Files.deleteIfExists(filePath);
        } catch (Exception e) {
            System.err.println("Failed to delete file: " + e.getMessage());
        }

        user.setProfilePictureUrl(null);
        return userRepository.save(user);
    }
    
    return user;
}


@Transactional
public void updateBulkStatus(List<Long> userIds, String status) {
    Iterable<AppUser> users = userRepository.findAllById(userIds);
    users.forEach(user -> {
        user.setStartupStatus(status.toLowerCase());
    });
    userRepository.saveAll(users);
}

@Transactional
public void updateBulkRole(List<Long> userIds, String role) {
    Iterable<AppUser> users = userRepository.findAllById(userIds);
    users.forEach(user -> {
        user.setRole(role.toLowerCase());
    });
    userRepository.saveAll(users);
}

@Transactional
public void deleteBulkUsers(List<Long> userIds) {
    userRepository.deleteAllById(userIds);
}

@Transactional
public AppUser updateStartupStatus(Long userId, String status) {
    AppUser user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));

    user.setStartupStatus(status.toLowerCase());
    return userRepository.save(user);
}


@Transactional
public AppUser adminCreateUser(CreateUserDto dto) {
    if (userRepository.findByEmail(dto.getEmail()).isPresent()) {
        throw new RuntimeException("User with this email already exists.");
    }

    AppUser user = new AppUser();
    user.setFirstName(dto.getFirstName());
    user.setLastName(dto.getLastName());
    user.setFullName(dto.getFirstName() + " " + dto.getLastName());
    user.setEmail(dto.getEmail());
    user.setPassword(passwordEncoder.encode(dto.getPassword()));
    user.setPhone(dto.getPhone());
    user.setTaxId(dto.getTaxId());
    user.setRole("user");
    user.setStartupStatus("pending");
    user.setEnabled(true);
    user.setAcceptedTerms(true);

    return userRepository.save(user);
}
}


