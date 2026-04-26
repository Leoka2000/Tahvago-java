package pt.tahvago.dto.Interactions;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InteractionRequest {
    private Long senderId;
    private Long receiverId;
    private String type; // INVITATION, PARTNERSHIP, MESSAGE
    private String title;
    private String content;
}

