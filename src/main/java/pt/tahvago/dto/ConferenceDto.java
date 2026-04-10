package pt.tahvago.dto;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ConferenceDto {
    private Long id;
    private String name;
    private String location;
    private LocalDateTime eventDate;
    private int participantCount;
}