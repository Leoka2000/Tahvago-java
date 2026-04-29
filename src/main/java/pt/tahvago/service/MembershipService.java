package pt.tahvago.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import pt.tahvago.dto.Membership.UserMembershipResponse;
import pt.tahvago.dto.User.FullUserProfileResponse;
import pt.tahvago.model.AppUser;
import pt.tahvago.model.Membership;
import pt.tahvago.model.Notification;
import pt.tahvago.model.Startup;
import pt.tahvago.repository.MembershipRepository;
import pt.tahvago.repository.NotificationRepository;
import pt.tahvago.repository.StartupRepository;
import pt.tahvago.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class MembershipService {

        private final UserRepository userRepository;
        private final MembershipRepository membershipRepository;
        private final StartupRepository startupRepository;
        private final NotificationRepository notificationRepository;

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

        @Transactional(readOnly = true)
        public Membership getMembershipByUserId(Long userId) {
                return membershipRepository.findByUserId(userId)
                                .orElseThrow(() -> new RuntimeException("Membership not found"));
        }

        @Transactional(readOnly = true)
        public FullUserProfileResponse getFullProfile(String email) {

                AppUser user = userRepository.findByEmail(email)
                                .orElseThrow(() -> new RuntimeException("User not found"));

                Membership membership = membershipRepository.findByUserId(user.getId())
                                .orElse(null);

                List<Startup> startups = startupRepository.findAllByOwnerId(user.getId());

               List<Notification> notifications =
        notificationRepository.findByRecipientOrderByCreatedAtDesc(user);

                System.out.println("USER: " + user.getEmail());
                System.out.println("STARTUPS: " + startups.size());
                System.out.println("NOTIFICATIONS: " + notifications.size());

                return FullUserProfileResponse.builder()
                                .user(user)
                                .membership(membership)
                                .startups(startups)
                                .notifications(notifications)
                                .build();
        }

        @Transactional(readOnly = true)
        public UserMembershipResponse getCurrentUserMembership(String email) {

                AppUser user = userRepository.findByEmail(email)
                                .orElseThrow(() -> new RuntimeException("User not found"));

                Membership membership = membershipRepository.findByUserId(user.getId())
                                .orElseThrow(() -> new RuntimeException("Membership not found"));

                List<Startup> startups = startupRepository.findAllByOwnerId(user.getId());

                return UserMembershipResponse.builder()
                                .user(user)
                                .membership(membership)
                                .startups(startups)
                                .build();
        }
}