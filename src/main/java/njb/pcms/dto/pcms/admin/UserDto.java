package njb.pcms.dto.pcms.admin;

import lombok.Data;
import njb.pcms.model.User;

@Data
public class UserDto {
    private Long id;
    private String studentId;
    private String name;
    private String kana;

    public UserDto(User user) {
        this.id = user.getId();
        this.studentId = user.getStudentId();
        this.name = user.getName();
        this.kana = user.getKana();
    }

}
