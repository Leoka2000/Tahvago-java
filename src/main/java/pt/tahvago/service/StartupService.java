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

    @Transactional(readOnly = true)
    public List<StartupResponse> getStartupsByUserId(Long userId) {
        return startupRepository.findAllByOwnerId(userId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public StartupResponse createStartup(StartupCreateRequest request, AppUser user) {
        Integer year = null;
        if (request.getFoundingYear() != null && request.getFoundingYear().contains("-")) {
            year = Integer.parseInt(request.getFoundingYear().split("-")[0]);
        } else if (request.getFoundingYear() != null && !request.getFoundingYear().isEmpty()) {
            year = Integer.parseInt(request.getFoundingYear());
        }

        Startup startup = Startup.builder()
                .name(request.getName())
                .description(request.getDescription())
                .website(request.getWebsite())
                .industry(request.getIndustry())
                .stage(request.getStage())
                .foundingYear(year)
                .teamSize(request.getTeamSize())
                .country(request.getCountry())
                .onEvaluation(true)
                .accepted(false)
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
                .industry(startup.getIndustry())
                .stage(startup.getStage())
                .foundingYear(startup.getFoundingYear())
                .teamSize(startup.getTeamSize())
                .country(startup.getCountry())
                .userId(startup.getOwner().getId())
                .onEvaluation(startup.getOnEvaluation())
                .accepted(startup.getAccepted())
                .build();
    }

    @Transactional(readOnly = true)
    public boolean hasStartups(Long userId) {
        return startupRepository.existsByOwnerId(userId);
    }
}