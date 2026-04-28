package pt.tahvago.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import pt.tahvago.dto.EvaluationStageRequest;

import pt.tahvago.dto.GetAllUsersStartups.GetAllUsersStartupsDto;
import pt.tahvago.dto.Notification.NotificationStartupsRequestDto;
import pt.tahvago.dto.NotificationStartupsResponseDto;
import pt.tahvago.dto.StartupCreateRequest;
import pt.tahvago.dto.StartupIdRequest;
import pt.tahvago.dto.StartupPatchRequest;
import pt.tahvago.dto.StartupResponse;
import pt.tahvago.dto.UserIdRequest;
import pt.tahvago.model.AppUser;
import pt.tahvago.service.StartupService;

@RestController
@RequestMapping("/api/startups")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class StartupController {

    private final StartupService startupService;

    @PostMapping
    public ResponseEntity<StartupResponse> createStartup(@RequestBody StartupCreateRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !(authentication.getPrincipal() instanceof AppUser)) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        AppUser currentUser = (AppUser) authentication.getPrincipal();

        return new ResponseEntity<>(
                startupService.createStartup(request, currentUser),
                HttpStatus.CREATED);
    }

    @GetMapping("/my-startups")
    public ResponseEntity<List<StartupResponse>> getMyStartups() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !(authentication.getPrincipal() instanceof AppUser)) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        AppUser currentUser = (AppUser) authentication.getPrincipal();
        return ResponseEntity.ok(startupService.getStartupsByUserId(currentUser.getId()));
    }

    @GetMapping
    public ResponseEntity<List<StartupResponse>> getAll() {
        return ResponseEntity.ok(startupService.getAllStartups());
    }

    @PostMapping("/by-user")
    public ResponseEntity<List<StartupResponse>> getStartupsByUser(@RequestBody UserIdRequest request) {
        if (request.getUserId() == null) {
            return ResponseEntity.badRequest().build();
        }

        List<StartupResponse> responses = startupService.getStartupsByUserId(request.getUserId());

        return ResponseEntity.ok(responses);
    }

    @PostMapping("/check-existence")
    public ResponseEntity<Boolean> checkExistence(@RequestBody UserIdRequest request) {
        return ResponseEntity.ok(startupService.hasStartups(request.getUserId()));
    }

    @PostMapping("/{id}/evaluation-stage")
    public ResponseEntity<StartupResponse> updateEvaluationStage(
            @PathVariable Long id,
            @RequestBody EvaluationStageRequest request) {
        return ResponseEntity.ok(startupService.updateEvaluationStage(id, request.getEvaluationStage()));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<StartupResponse> patchStartup(
            @PathVariable Long id,
            @RequestBody StartupPatchRequest request) {
        return ResponseEntity.ok(startupService.patchStartup(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStartup(@PathVariable Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !(authentication.getPrincipal() instanceof AppUser)) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        AppUser currentUser = (AppUser) authentication.getPrincipal();
        startupService.deleteStartup(id, currentUser);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/all-details")
    public ResponseEntity<List<GetAllUsersStartupsDto.StartupDetailsDto>> getAllStartupsDetails() {
        return ResponseEntity.ok(startupService.getAllStartupsWithUser());
    }

    @PatchMapping("/{id}/logo")
    public ResponseEntity<?> uploadLogo(@PathVariable Long id, @RequestParam("file") MultipartFile file) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.status(401).build();
            }

            AppUser currentUser = (AppUser) authentication.getPrincipal();

            StartupResponse response = startupService.updateStartupLogo(id, file, currentUser);
            return ResponseEntity.ok(response);

        } catch (AccessDeniedException e) {
            return ResponseEntity.status(403).body(Map.of("error", e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", "Failed to upload logo"));
        }
    }

    @DeleteMapping("/{id}/logo")
    public ResponseEntity<?> deleteLogo(@PathVariable Long id) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            AppUser currentUser = (AppUser) authentication.getPrincipal();

            StartupResponse response = startupService.deleteStartupLogo(id, currentUser);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/details-by-id")
    public ResponseEntity<StartupResponse> getStartupDetails(@RequestBody StartupIdRequest request) {
        if (request.getStartupId() == null) {
            return ResponseEntity.badRequest().build();
        }

        StartupResponse startup = startupService.getStartupById(request.getStartupId());
        return ResponseEntity.ok(startup);
    }

    @GetMapping("/details-by-id/{startupId}")
    public ResponseEntity<StartupResponse> getStartupDetails(@PathVariable Long startupId) {

        if (startupId == null) {
            return ResponseEntity.badRequest().build();
        }

        StartupResponse startup = startupService.getStartupById(startupId);
        return ResponseEntity.ok(startup);
    }


 @PostMapping("/by-notification")
public ResponseEntity<NotificationStartupsResponseDto> getStartupsByNotification(
        @RequestBody NotificationStartupsRequestDto request) {

    if (request.getNotificationId() == null) {
        return ResponseEntity.badRequest().build();
    }

    return ResponseEntity.ok(
            startupService.getStartupsByNotificationId(request.getNotificationId())
    );
}
}