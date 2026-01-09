package njb.pcms.dto.pcms.admin;

import lombok.Data;
import njb.pcms.model.User;

import java.time.LocalDateTime;

@Data
public class AdminUserResponseDto {
    private Long id;
    private String studentId;
    private String name;
    private String kana;
    private String role;
    private LocalDateTime createdAt;

    public static AdminUserResponseDto fromEntity(User user) {
        AdminUserResponseDto dto = new AdminUserResponseDto();
        dto.setId(user.getId());
        dto.setStudentId(user.getStudentId());
        dto.setName(user.getName());
        dto.setKana(user.getKana());
        dto.setRole(user.getRole().name());
        dto.setCreatedAt(user.getCreatedAt());
        return dto;
    }
}
