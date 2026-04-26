package pt.tahvago.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

import pt.tahvago.dto.Interactions.InteractionRequest;
import pt.tahvago.dto.Interactions.InteractionResponse;
import pt.tahvago.dto.StartupResponse;
import pt.tahvago.model.Notification;
import pt.tahvago.model.Startup;
import pt.tahvago.model.StartupInteraction;
import pt.tahvago.repository.NotificationRepository;
import pt.tahvago.repository.StartupInteractionRepository;
import pt.tahvago.repository.StartupRepository;

@Service
@RequiredArgsConstructor
public class StartupInteractionService {

    private final StartupInteractionRepository interactionRepository;
    private final StartupRepository startupRepository;
    private final NotificationRepository notificationRepository;

    @Transactional
    public InteractionResponse sendInteraction(InteractionRequest request) {
        Startup sender = startupRepository.findById(request.getSenderId())
                .orElseThrow(() -> new RuntimeException("Sender not found"));
        Startup receiver = startupRepository.findById(request.getReceiverId())
                .orElseThrow(() -> new RuntimeException("Receiver not found"));

        StartupInteraction interaction = new StartupInteraction();
        interaction.setSender(sender);
        interaction.setReceiver(receiver);
        interaction.setType(request.getType());
        interaction.setTitle(request.getTitle());
        interaction.setContent(request.getContent());
        interaction.setStatus("sent");

        StartupInteraction saved = interactionRepository.save(interaction);

        Notification notification = new Notification();
        notification.setRecipient(receiver.getOwner());
        notification.setMessage("New " + request.getType() + " from " + sender.getName());
        notification.setRead(false);
        notification.setRelatedInteraction(saved);
        notificationRepository.save(notification);

        Startup senderWithDetails = startupRepository.findById(request.getSenderId())
                .orElseThrow(() -> new RuntimeException("Refresh failed for sender"));
        Startup receiverWithDetails = startupRepository.findById(request.getReceiverId())
                .orElseThrow(() -> new RuntimeException("Refresh failed for receiver"));

        return InteractionResponse.builder()
                .id(saved.getId())
                .type(saved.getType())
                .title(saved.getTitle())
                .content(saved.getContent())
                .status(saved.getStatus())
                .sentAt(saved.getSentAt())
                .sender(mapStartupToResponse(senderWithDetails))
                .receiver(mapStartupToResponse(receiverWithDetails))
                .build();
    }

    private StartupResponse mapStartupToResponse(Startup startup) {
        if (startup == null)
            return null;

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
                .userId(startup.getOwner() != null ? startup.getOwner().getId() : null)
                .build();
    }
}