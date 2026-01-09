package njb.pcms.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class StudentRegistrationDto {

    @NotNull
    @Min(2000)
    @Max(2100)
    private Integer admissionYear;

    @NotBlank
    private String name;

    @NotBlank
    private String kana;
}
