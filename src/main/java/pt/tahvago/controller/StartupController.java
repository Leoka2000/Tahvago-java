package pt.tahvago.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import pt.tahvago.dto.EvaluationStageRequest;
import pt.tahvago.dto.StartupCreateRequest;
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
        List<StartupResponse> startups = startupService.getStartupsByUserId(request.getUserId());
        return ResponseEntity.ok(startups);
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
}