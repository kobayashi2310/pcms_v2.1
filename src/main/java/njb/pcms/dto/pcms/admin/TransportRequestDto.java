package njb.pcms.dto.pcms.admin;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

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

    @NotNull(message = "返却予定日は必須です")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @FutureOrPresent(message = "返却予定日は本日以降の日付を指定してください")
    private LocalDate expectedReturnDate;

}
