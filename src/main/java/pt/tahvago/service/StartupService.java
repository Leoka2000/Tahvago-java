package pt.tahvago.service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import pt.tahvago.dto.GetAllUsersStartups.GetAllUsersStartupsDto;
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
    private final String UPLOAD_DIR = "uploads/";

    @Transactional(readOnly = true)
    public List<StartupResponse> getStartupsByUserId(Long userId) {
        return startupRepository.findAllByOwnerId(userId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public StartupResponse getSingleStartupByUserId(Long userId) {
        Startup startup = startupRepository.findByOwnerId(userId)
                .orElseThrow(() -> new RuntimeException("Startup not found for this user"));
        return mapToResponse(startup);
    }

    @Transactional
    public StartupResponse createStartup(StartupCreateRequest request, AppUser user) {
        String yearStr = request.getFoundingYear();
        if (yearStr != null && yearStr.contains("-")) {
            yearStr = yearStr.split("-")[0];
        }

        Startup startup = Startup.builder()
                .name(request.getName())
                .description(request.getDescription())
                .website(request.getWebsite())
                .industry(request.getIndustry())
                .stage(request.getStage())
                .foundingYear(yearStr)
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

    @Transactional
    public StartupResponse updateStartupLogo(Long startupId, MultipartFile file, AppUser currentUser) {
        Startup startup = startupRepository.findById(startupId)
                .orElseThrow(() -> new RuntimeException("Startup not found"));

        if (!startup.getOwner().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("You do not own this startup");
        }

        try {
            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
            Path filePath = uploadPath.resolve(fileName);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            String fileUrl = "/uploads/" + fileName;
            startup.setCompanyLogo(fileUrl);

            return mapToResponse(startupRepository.save(startup));
        } catch (Exception e) {
            throw new RuntimeException("Could not store file: " + e.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public List<StartupResponse> getAllStartups() {
        return startupRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<GetAllUsersStartupsDto.StartupDetailsDto> getAllStartupsWithUser() {
        return startupRepository.findAll()
                .stream()
                .map(this::mapToDetailsDto)
                .collect(Collectors.toList());
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
                .companyLogo(startup.getCompanyLogo())
                .build();
    }

    private GetAllUsersStartupsDto.StartupDetailsDto mapToDetailsDto(Startup startup) {
        return GetAllUsersStartupsDto.StartupDetailsDto.builder()
                .id(startup.getId())
                .name(startup.getName())
                .description(startup.getDescription())
                .website(startup.getWebsite())
                .industry(startup.getIndustry())
                .stage(startup.getStage())
                .foundingYear(startup.getFoundingYear())
                .teamSize(startup.getTeamSize())
                .country(startup.getCountry())
                .creditBalance(startup.getCreditBalance())
                .onEvaluation(startup.getOnEvaluation())
                .accepted(startup.getAccepted())
                .evaluationStage(startup.getEvaluationStage())
                .userId(startup.getOwner() != null ? startup.getOwner().getId() : null)
                .companyLogo(startup.getCompanyLogo())
                .build();
    }

    @Transactional
    public StartupResponse deleteStartupLogo(Long startupId, AppUser currentUser) {
        Startup startup = startupRepository.findById(startupId)
                .orElseThrow(() -> new RuntimeException("Startup not found"));

        if (!startup.getOwner().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("You do not own this startup");
        }

        String logoPath = startup.getCompanyLogo();
        if (logoPath != null) {
            try {
                Path filePath = Paths.get(logoPath.substring(1));
                Files.deleteIfExists(filePath);
            } catch (Exception e) {
                System.err.println("Failed to delete physical file: " + e.getMessage());
            }
        }

        startup.setCompanyLogo(null);
        return mapToResponse(startupRepository.save(startup));
    }



    public StartupResponse getStartupById(Long id) {
    Startup startup = startupRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Startup not found with id: " + id));
    
    return convertToResponse(startup);
}

private StartupResponse convertToResponse(Startup startup) {
    return StartupResponse.builder()
            .id(startup.getId())
            .name(startup.getName())
            .description(startup.getDescription())
            .website(startup.getWebsite())
            .industry(startup.getIndustry())
            .stage(startup.getStage())
            .foundingYear(startup.getFoundingYear())
            .companyLogo(startup.getCompanyLogo())
            .teamSize(startup.getTeamSize())
            .country(startup.getCountry())
            .creditBalance(startup.getCreditBalance())
            .onEvaluation(startup.getOnEvaluation())
            .accepted(startup.getAccepted())
            .evaluationStage(startup.getEvaluationStage())
            .userId(startup.getOwner().getId())
            .build();
}
}