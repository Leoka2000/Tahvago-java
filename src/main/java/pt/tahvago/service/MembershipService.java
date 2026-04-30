package pt.tahvago.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import pt.tahvago.dto.Membership.UserMembershipResponse;
import pt.tahvago.dto.Membership.test.AppUserMembershipDto;
import pt.tahvago.dto.Membership.test.MembershipMembershipDto;
import pt.tahvago.dto.Membership.test.StartupMembershipDto;
import pt.tahvago.dto.Membership.test.*;

import pt.tahvago.model.AppUser;
import pt.tahvago.model.Membership;
import pt.tahvago.model.Startup;

import pt.tahvago.repository.MembershipRepository;
import pt.tahvago.repository.StartupRepository;
import pt.tahvago.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class MembershipService {

    private final UserRepository userRepository;
    private final MembershipRepository membershipRepository;
    private final StartupRepository startupRepository;

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

        // 🔹 USER DTO
        AppUserMembershipDto userDto = new AppUserMembershipDto();
        userDto.setId(user.getId());
        userDto.setFirstName(user.getFirstName());
        userDto.setLastName(user.getLastName());
        userDto.setFullName(user.getFullName());
        userDto.setEmail(user.getEmail());
        userDto.setUsername(user.getUsername());
        userDto.setRole(user.getRole());
        userDto.setProfilePictureUrl(user.getProfilePictureUrl());
        userDto.setStartupStatus(user.getStartupStatus());
        userDto.setAcceptedTerms(user.getAcceptedTerms());
        userDto.setEnabled(user.isEnabled());
        userDto.setCreatedAt(user.getCreatedAt());

        // 🔹 MEMBERSHIP DTO
        MembershipMembershipDto membershipDto = null;
        if (membership != null) {
            membershipDto = new MembershipMembershipDto();
            membershipDto.setId(membership.getId());
            membershipDto.setTierName(membership.getTierName());
            membershipDto.setTierLevel(membership.getTierLevel());
            membershipDto.setStatus(membership.getStatus());
            membershipDto.setMonthlyPrice(membership.getMonthlyPrice());
            membershipDto.setMonthlyCreditAllotment(membership.getMonthlyCreditAllotment());
            membershipDto.setStartDate(membership.getStartDate());
            membershipDto.setNextBillingDate(membership.getNextBillingDate());
            membershipDto.setUserId(user.getId());
        }

        // 🔹 STARTUPS DTO
        List<StartupMembershipDto> startupDtos = startups.stream().map(s -> {
            StartupMembershipDto dto = new StartupMembershipDto();
            dto.setId(s.getId());
            dto.setName(s.getName());
            dto.setDescription(s.getDescription());
            dto.setWebsite(s.getWebsite());
            dto.setIndustry(s.getIndustry());
            dto.setStage(s.getStage());
            dto.setFoundingYear(s.getFoundingYear());
            dto.setCompanyLogo(s.getCompanyLogo());
            dto.setTeamSize(s.getTeamSize());
            dto.setCountry(s.getCountry());
            dto.setCreditBalance(s.getCreditBalance());
            dto.setOnEvaluation(s.getOnEvaluation());
            dto.setAccepted(s.getAccepted());
            dto.setEvaluationStage(s.getEvaluationStage());
            dto.setCreatedAt(s.getCreatedAt());
            dto.setUpdatedAt(s.getUpdatedAt());
            dto.setOwnerId(user.getId());
            return dto;
        }).toList();

        return FullUserProfileResponse.builder()
                .user(userDto)
                .membership(membershipDto)
                .startups(startupDtos)
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
                .user(user) // ⚠️ still entity-based (safe for now, but not ideal)
                .membership(membership)
                .startups(startups)
                .build();
    }
}