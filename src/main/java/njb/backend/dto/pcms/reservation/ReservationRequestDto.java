package njb.backend.dto.pcms.reservation;

import jakarta.validation.constraints.*;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.List;

@Data
public class ReservationRequestDto {

    @NotNull(message = "PCを選択してください。")
    private Long pcId;

    @NotNull(message = "利用日を選択してください。")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @FutureOrPresent(message = "利用日は本日以降を選択してください。")
    private LocalDate date;

    @NotEmpty(message = "利用時間帯を1つ以上選択してください。")
    private List<Byte> periodIds;

    @NotEmpty(message = "利用目的を入力してください。")
    @Size(max = 200, message = "利用目的は200文字以内で入力してください。")
    private String reason;
}
