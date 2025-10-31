package njb.pcms.dto.pcms.admin;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TransportRequestDto {

    @NotNull(message = "PCは必須です")
    private Long pcId;

    @NotNull(message = "学生は必須です")
    private Long userId;

    @NotBlank(message = "持ち出し先は必須です")
    private String destination;

    @NotBlank(message = "理由は必須です")
    private String reason;

}
