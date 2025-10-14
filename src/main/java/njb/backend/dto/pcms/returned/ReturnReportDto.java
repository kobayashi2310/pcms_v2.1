package njb.backend.dto.pcms.returned;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

@Data
public class ReturnReportDto {

    private List<Long> reservationIds;

    @NotBlank(message = "作業内容は必須です")
    private String retractionReason;

}
