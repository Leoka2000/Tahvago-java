package pt.tahvago.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import pt.tahvago.dto.ConferenceDto;
import pt.tahvago.dto.UserIdRequest;
import pt.tahvago.service.ConferenceService;
import pt.tahvago.service.StartupService;

@RestController
@RequestMapping("/api/conferences")
public class ConferenceController {
    private final ConferenceService conferenceService;
    private final StartupService startupService;

    public ConferenceController(ConferenceService conferenceService, StartupService startupService) {
        this.conferenceService = conferenceService;
        this.startupService = startupService;
    }

    @GetMapping
    public ResponseEntity<List<ConferenceDto>> getAll() {
        return ResponseEntity.ok(conferenceService.getAllConferences());
    }

    @PostMapping("/check-existence")
    public ResponseEntity<Boolean> checkUserHasStartups(@RequestBody UserIdRequest request) {
        boolean exists = startupService.hasStartups(request.getUserId());
        return ResponseEntity.ok(exists);
    }
}