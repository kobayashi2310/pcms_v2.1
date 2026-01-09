package njb.pcms.dto;

import lombok.Data;
import njb.pcms.model.User;

import java.util.ArrayList;
import java.util.List;

@Data
public class StudentRegistrationPreviewDto {
    private List<User> validStudents = new ArrayList<>();
    private List<String> errorMessages = new ArrayList<>();

    public boolean hasErrors() {
        return !errorMessages.isEmpty();
    }
}
