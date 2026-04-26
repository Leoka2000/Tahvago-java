package pt.tahvago.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import pt.tahvago.dto.Interactions.InteractionRequest;
import pt.tahvago.dto.Interactions.InteractionResponse;
import pt.tahvago.service.StartupInteractionService;

@RestController
@RequestMapping("/api/interactions")
@RequiredArgsConstructor
public class StartupInteractionController {

    private final StartupInteractionService interactionService;

    @PostMapping("/send")
    public ResponseEntity<InteractionResponse> send(@RequestBody InteractionRequest request) {
        return ResponseEntity.ok(interactionService.sendInteraction(request));
    }
}