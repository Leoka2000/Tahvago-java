package pt.tahvago.dto.User;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PatchUserDto {
    private Long userId;
    private Map<String, Object> updates;
} 






