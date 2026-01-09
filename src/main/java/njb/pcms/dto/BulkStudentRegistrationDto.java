package njb.pcms.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class BulkStudentRegistrationDto {

    @NotNull
    @Min(2000)
    @Max(2100)
    private Integer admissionYear;

    @NotBlank(message = "学生リストを入力してください")
    private String studentListRaw;
}
