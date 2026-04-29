package pt.tahvago.dto.User;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BulkRoleUpdateDto {
    private List<Long> userIds;
    private String newRole;
}