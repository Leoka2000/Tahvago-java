package pt.tahvago.dto.Interactions;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pt.tahvago.dto.StartupResponse;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InteractionResponse {
    private Long id;
    private String type;
    private String title;
    private String content;
    private String status;
    private java.time.LocalDateTime sentAt;
    private StartupResponse sender;
    private StartupResponse receiver;
}