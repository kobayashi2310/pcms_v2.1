package njb.pcms.dto.pcms.admin;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
public class TransportUpdateRequestDto {

    @NotNull
    private Long id;

    @NotNull(message = "返却予定日は必須です")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @FutureOrPresent(message = "返却予定日は本日以降の日付を指定してください")
    private LocalDate expectedReturnDate;

}
