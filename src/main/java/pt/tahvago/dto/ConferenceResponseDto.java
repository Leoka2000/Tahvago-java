package pt.tahvago.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConferenceResponseDto {
    private Long id;
    private String name;
    private String location;
    private LocalDateTime eventDate;
}