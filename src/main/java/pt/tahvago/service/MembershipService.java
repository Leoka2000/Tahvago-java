package pt.tahvago.service;


import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import pt.tahvago.model.AppUser;
import pt.tahvago.model.Membership;
import pt.tahvago.repository.MembershipRepository;
import pt.tahvago.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class MembershipService {

    private final MembershipRepository membershipRepository;
    private final UserRepository userRepository;

    @Transactional
    public Membership assignMembership(Long userId, Integer tierLevel) {
        AppUser user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Membership membership = membershipRepository.findByUserId(userId)
                .orElse(new Membership());

        membership.setUser(user);
        membership.setTierLevel(tierLevel);
        
        if (tierLevel == 0) {
            membership.setTierName("Free Tier");
            membership.setMonthlyCreditAllotment(500);
            membership.setMonthlyPrice(0.0);
        } else {
            membership.setTierName("Tier " + tierLevel);
            membership.setMonthlyCreditAllotment(1000 * tierLevel); 
        }

        membership.setStatus("active");
        return membershipRepository.save(membership);
    }

    public Membership getMembershipByUserId(Long userId) {
        return membershipRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Membership not found for this user"));
    }
}