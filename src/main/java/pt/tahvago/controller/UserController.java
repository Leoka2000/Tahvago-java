package pt.tahvago.controller;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import pt.tahvago.dto.Startups.UpdateStartupStatusDto;
import pt.tahvago.dto.User.BulkDeleteDto;
import pt.tahvago.dto.User.BulkRoleUpdateDto;
import pt.tahvago.dto.User.BulkStatusUpdateDto;
import pt.tahvago.dto.User.ChangePasswordDto;
import pt.tahvago.dto.User.CreateUserDto;
import pt.tahvago.dto.User.PatchUserDto;
import pt.tahvago.dto.User.UpdateUserDto;
import pt.tahvago.dto.User.UserDto;
import pt.tahvago.model.AppUser;
import pt.tahvago.service.JwtService;
import pt.tahvago.service.UserService;

@RequestMapping("/users")
@RestController
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    private final UserService userService;
    private final JwtService jwtService;
    

    public UserController(UserService userService, JwtService jwtService) {
        this.userService = userService;
        this.jwtService = jwtService;
        
    }

    @PatchMapping("/bulk-role")
    public ResponseEntity<?> updateBulkRole(@RequestBody BulkRoleUpdateDto dto) {
        try {
            userService.updateBulkRole(dto.getUserIds(), dto.getNewRole());
            return ResponseEntity.ok(Map.of("message", "Roles updated successfully"));
        } catch (Exception e) {
            logger.error("Error updating bulk roles", e);
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PatchMapping("/bulk-status")
    public ResponseEntity<?> updateBulkStatus(@RequestBody BulkStatusUpdateDto dto) {
        try {
            userService.updateBulkStatus(dto.getUserIds(), dto.getNewStatus());
            return ResponseEntity.ok(Map.of("message", "Statuses updated successfully"));
        } catch (Exception e) {
            logger.error("Error updating bulk status", e);
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/bulk-delete")
    public ResponseEntity<?> deleteBulkUsers(@RequestBody BulkDeleteDto dto) {
        try {
            userService.deleteBulkUsers(dto.getUserIds());
            return ResponseEntity.ok(Map.of("message", "Users deleted successfully"));
        } catch (Exception e) {
            logger.error("Error deleting bulk users", e);
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<List<AppUser>> allUsers() {
        try {
            List<AppUser> users = userService.allUsers();
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            logger.error("Error fetching all users", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @PatchMapping("/startup-status")
    public ResponseEntity<?> updateStartupStatus(@RequestBody UpdateStartupStatusDto dto) {
        try {
            AppUser updated = userService.updateStartupStatus(dto.getUserId(), dto.getStartupStatus());

            return ResponseEntity.ok(Map.of(
                    "message", "Startup status updated successfully",
                    "user", updated));
        } catch (Exception e) {
            logger.error("Error updating startup status", e);
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PatchMapping("/me")
    public ResponseEntity<?> updateCurrentUser(@RequestBody UpdateUserDto updateDto) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.status(401).build();
            }

            AppUser currentUser = (AppUser) authentication.getPrincipal();

            AppUser updatedUser = userService.updateUser(
                    currentUser.getId(),
                    updateDto.getFullName(),
                    updateDto.getEmail());

            String newToken = jwtService.generateToken(updatedUser);

            return ResponseEntity.ok()
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + newToken)
                    .body(Map.of(
                            "user", updatedUser,
                            "token", newToken));
        } catch (ClassCastException e) {
            logger.error("Authentication principal type mismatch", e);
            return ResponseEntity.status(403).build();
        } catch (Exception e) {
            logger.error("Error updating user", e);
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Failed to update user"));
        }
    }

    @GetMapping("/me")
    public ResponseEntity<UserDto> authenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).build();
        }

        try {
            AppUser currentUser = (AppUser) authentication.getPrincipal();

            UserDto dto = new UserDto();
            dto.setId(currentUser.getId());
            dto.setFullName(currentUser.getFullName());
            dto.setEmail(currentUser.getEmail());
            dto.setUsername(currentUser.getUsername());
            dto.setRole(currentUser.getRole());
            dto.setProfilePictureUrl(currentUser.getProfilePictureUrl());
            dto.setStartupStatus(currentUser.getStartupStatus());

            return ResponseEntity.ok(dto);
        } catch (ClassCastException e) {
            logger.error("Principal is not of type AppUser", e);
            return ResponseEntity.status(403).build();
        } catch (Exception e) {
            logger.error("Error fetching authenticated user", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/me/profile-picture")
    public ResponseEntity<?> uploadProfilePicture(@RequestParam("file") MultipartFile file) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            AppUser currentUser = (AppUser) authentication.getPrincipal();
            AppUser updatedUser = userService.updateProfilePicture(currentUser.getId(), file);

            return ResponseEntity.ok(Map.of(
                    "message", "Profile picture updated successfully",
                    "profilePictureUrl", updatedUser.getProfilePictureUrl()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/admin-create")
    public ResponseEntity<?> adminCreateUser(@RequestBody CreateUserDto dto) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            AppUser currentUser = (AppUser) authentication.getPrincipal();

            if (!"admin".equalsIgnoreCase(currentUser.getRole())) {
                return ResponseEntity.status(403).body(Map.of("error", "Only admins can perform this action"));
            }

            AppUser newUser = userService.adminCreateUser(dto);
            return ResponseEntity.ok(Map.of(
                    "message", "User created successfully by admin",
                    "userId", newUser.getId()));
        } catch (Exception e) {
            logger.error("Admin failed to create user", e);
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PatchMapping("/me/password")
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordDto dto) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.status(401).build();
            }

            AppUser currentUser = (AppUser) authentication.getPrincipal();

            userService.changePassword(
                    currentUser.getId(),
                    dto.getCurrentPassword(),
                    dto.getNewPassword(),
                    dto.getConfirmPassword());

            return ResponseEntity.ok(Map.of("message", "Password changed successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            logger.error("Unexpected error during password change", e);
            return ResponseEntity.internalServerError().body(Map.of("error", "Failed to change password"));
        }

    }

    @DeleteMapping("/me/profile-picture")
    public ResponseEntity<?> deleteProfilePicture() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            AppUser currentUser = (AppUser) authentication.getPrincipal();

            userService.deleteProfilePicture(currentUser.getId());

            return ResponseEntity.ok(Map.of("message", "Profile picture deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

    @PatchMapping("/patch")
public ResponseEntity<?> patchUser(@RequestBody PatchUserDto dto) {
    try {
        AppUser updated = userService.patchUser(dto.getUserId(), dto.getUpdates());

        return ResponseEntity.ok(Map.of(
                "message", "User updated successfully",
                "user", updated
        ));
    } catch (Exception e) {
        return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
    }
}

 
}