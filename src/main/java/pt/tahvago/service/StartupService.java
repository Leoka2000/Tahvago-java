package pt.tahvago.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import pt.tahvago.dto.StartupCreateRequest;
import pt.tahvago.dto.StartupResponse;
import pt.tahvago.model.AppUser;
import pt.tahvago.model.Startup;
import pt.tahvago.repository.StartupRepository;

@Service
@RequiredArgsConstructor
public class StartupService {

    private final StartupRepository startupRepository;


  

    public StartupResponse getStartupByUserId(Long userId) {
        Startup startup = startupRepository.findByOwnerId(userId)
                .orElseThrow(() -> new RuntimeException("Startup not found for this user"));
        
        return mapToResponse(startup);
    }



    @Transactional
    public StartupResponse createStartup(StartupCreateRequest request, AppUser user) {
        Startup startup = Startup.builder()
                .name(request.getName())
                .description(request.getDescription())
                .website(request.getWebsite())
                .logoUrl(request.getLogoUrl())
                .industry(request.getIndustry())
                .stage(request.getStage())
                .foundingYear(request.getFoundingYear())
                .teamSize(request.getTeamSize())
                .country(request.getCountry())
                .owner(user) 
                .build();

        Startup saved = startupRepository.save(startup);
        return mapToResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<StartupResponse> getAllStartups() {
        return startupRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

  private StartupResponse mapToResponse(Startup startup) {
        return StartupResponse.builder()
                .id(startup.getId())
                .name(startup.getName())
                .description(startup.getDescription())
                .website(startup.getWebsite())
                .logoUrl(startup.getLogoUrl())
                .industry(startup.getIndustry())
                .stage(startup.getStage())
                .foundingYear(startup.getFoundingYear())
                .teamSize(startup.getTeamSize())
                .country(startup.getCountry())
                .build();
    }
}