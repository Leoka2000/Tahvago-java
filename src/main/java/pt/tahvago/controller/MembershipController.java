package pt.tahvago.controller;

import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import lombok.RequiredArgsConstructor;
import pt.tahvago.dto.Membership.UserMembershipResponse;
import pt.tahvago.dto.User.FullUserProfileResponse;
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

    @GetMapping("/me/{id}")
    public ResponseEntity<FullUserProfileResponse> getProfileById(@PathVariable Long id) {
        AppUser user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return ResponseEntity.ok(membershipService.getFullProfile(user.getEmail()));
    }

   
    @PostMapping("/assign-me")
    public ResponseEntity<Membership> assignToMe(
            Authentication authentication,
            @RequestBody Map<String, Integer> body) {

        String email = authentication.getName();
        AppUser user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Integer tierLevel = body.get("tierLevel");
        return ResponseEntity.ok(membershipService.assignMembership(user.getId(), tierLevel));
    }
}