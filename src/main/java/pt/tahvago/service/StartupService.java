package pt.tahvago.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import pt.tahvago.dto.StartupCreateRequest;
import pt.tahvago.dto.StartupPatchRequest;
import pt.tahvago.dto.StartupResponse;
import pt.tahvago.model.AppUser;
import pt.tahvago.model.Startup;
import pt.tahvago.repository.StartupRepository;

@Service
@RequiredArgsConstructor
public class StartupService {

    private final StartupRepository startupRepository;
    private final NotificationService notificationService;

    @Transactional(readOnly = true)
    public List<StartupResponse> getStartupsByUserId(Long userId) {
        return startupRepository.findAllByOwnerId(userId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public StartupResponse createStartup(StartupCreateRequest request, AppUser user) {
        Startup startup = Startup.builder()
                .name(request.getName())
                .description(request.getDescription())
                .website(request.getWebsite())
                .industry(request.getIndustry())
                .stage(request.getStage())
                .foundingYear(request.getFoundingYear())
                .teamSize(request.getTeamSize())
                .country(request.getCountry())
                .onEvaluation(true)
                .accepted(false)
                .evaluationStage("registered")
                .owner(user)
                .build();

        Startup saved = startupRepository.save(startup);

        notificationService.createStageNotification(user, "registered");

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
                .evaluationStage(startup.getEvaluationStage())
                .build();
    }

    @Transactional(readOnly = true)
    public boolean hasStartups(Long userId) {
        return startupRepository.existsByOwnerId(userId);
    }

    @Transactional
    public StartupResponse updateEvaluationStage(Long id, String newStage) {
        Startup startup = startupRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Startup not found with id: " + id));

        startup.setEvaluationStage(newStage);
        Startup saved = startupRepository.save(startup);

        notificationService.createStageNotification(startup.getOwner(), newStage);

        return mapToResponse(saved);
    }

    @Transactional
    public void deleteStartup(Long startupId, AppUser currentUser) {
        Startup startup = startupRepository.findById(startupId)
                .orElseThrow(() -> new RuntimeException("Startup not found with id: " + startupId));

        if (!startup.getOwner().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("You do not have permission to delete this startup");
        }

        startupRepository.delete(startup);
    }

    @Transactional
    public StartupResponse patchStartup(Long id, StartupPatchRequest request) {
        Startup startup = startupRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Startup not found with id: " + id));

        if (request.getEvaluationStage() != null) {
            String newStage = request.getEvaluationStage();
            startup.setEvaluationStage(newStage);
            notificationService.createStageNotification(startup.getOwner(), newStage);
        }

        return mapToResponse(startupRepository.save(startup));
    }
}