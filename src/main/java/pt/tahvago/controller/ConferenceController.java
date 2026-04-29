package pt.tahvago.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import pt.tahvago.dto.Conference.ConferenceDto;
import pt.tahvago.service.ConferenceService;

@RestController
@RequestMapping("/api/conferences")
@RequiredArgsConstructor
public class ConferenceController {
    
    private final ConferenceService conferenceService;

    @GetMapping
    public ResponseEntity<List<ConferenceDto>> getAll() {
        return ResponseEntity.ok(conferenceService.getAllConferences());
    }
}