package pt.tahvago.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;
import pt.tahvago.model.Membership;
import pt.tahvago.service.MembershipService;

@RestController
@RequestMapping("/memberships")
@RequiredArgsConstructor
public class MembershipController {

    private final MembershipService membershipService;

    @PostMapping("/assign/{userId}")
    public ResponseEntity<Membership> assignMembership(@PathVariable Long userId, @RequestParam Integer tierLevel) {
        return ResponseEntity.ok(membershipService.assignMembership(userId, tierLevel));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<Membership> getUserMembership(@PathVariable Long userId) {
        return ResponseEntity.ok(membershipService.getMembershipByUserId(userId));
    }
}