package pt.tahvago.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import pt.tahvago.dto.ConferenceDto;
import pt.tahvago.model.Conference;
import pt.tahvago.repository.ConferenceRepository;

@Service
public class ConferenceService {
    private final ConferenceRepository conferenceRepository;

    public ConferenceService(ConferenceRepository conferenceRepository) {
        this.conferenceRepository = conferenceRepository;
    }

    public List<ConferenceDto> getAllConferences() {
        List<Conference> conferences = new ArrayList<>();
        conferenceRepository.findAll().forEach(conferences::add);
        
        return conferences.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    private ConferenceDto convertToDto(Conference conference) {
        ConferenceDto dto = new ConferenceDto();
        dto.setId(conference.getId());
        dto.setName(conference.getName());
        dto.setLocation(conference.getLocation());
        dto.setEventDate(conference.getEventDate());
        dto.setParticipantCount(conference.getParticipants().size());
        return dto;
    }
}