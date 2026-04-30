package pt.tahvago.controller;

import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import lombok.RequiredArgsConstructor;
import pt.tahvago.dto.Membership.MembershipTierUpdateRequest;
import pt.tahvago.dto.Membership.test.FullUserProfileResponse;
import pt.tahvago.model.Membership;
import pt.tahvago.service.MembershipService;
import pt.tahvago.model.AppUser;
import pt.tahvago.repository.UserRepository;

@RestController
@RequestMapping("/memberships")
@RequiredArgsConstructor
public class MembershipController {

    private final MembershipService membershipService;
    private final UserRepository userRepository;

    

    @GetMapping("/me")
public ResponseEntity<FullUserProfileResponse> getMyMembership(Authentication authentication) {
    if (authentication == null || !authentication.isAuthenticated()) {
        return ResponseEntity.status(401).build();
    }

    try {
        AppUser currentUser = (AppUser) authentication.getPrincipal();

        return ResponseEntity.ok(
                membershipService.getFullProfile(currentUser.getEmail())
        );

    } catch (ClassCastException e) {
        return ResponseEntity.status(403).build();
    }
}

    @PostMapping("/assign-me")
    public ResponseEntity<Membership> assignToMe(
            Authentication authentication,
            @RequestBody Map<String, Integer> body) {
        String email = authentication.getName();
        AppUser user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Integer tierLevel = body.get("tierLevel");
        if (tierLevel == null) {
            throw new RuntimeException("tierLevel is required");
        }
        return ResponseEntity.ok(membershipService.assignMembership(user.getId(), tierLevel));
    }

    @PostMapping("/assign/{userId}")
    public ResponseEntity<Membership> assignMembership(
            @PathVariable Long userId,
            @RequestBody Map<String, Integer> body) {
        Integer tierLevel = body.get("tierLevel");
        if (tierLevel == null) {
            throw new RuntimeException("tierLevel is required");
        }
        return ResponseEntity.ok(membershipService.assignMembership(userId, tierLevel));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<Membership> getUserMembership(@PathVariable Long userId) {
        return ResponseEntity.ok(membershipService.getMembershipByUserId(userId));
    }


    @PostMapping("/update-tier")
public ResponseEntity<Membership> updateTier(@RequestBody MembershipTierUpdateRequest request) {

    if (request.getUserId() == null || request.getTierLevel() == null) {
        throw new RuntimeException("userId and tierLevel are required");
    }

    return ResponseEntity.ok(
            membershipService.updateTier(
                    request.getUserId(),
                    request.getTierLevel()
            )
    );
}
}