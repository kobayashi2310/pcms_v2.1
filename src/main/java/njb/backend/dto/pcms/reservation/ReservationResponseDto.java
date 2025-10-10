package njb.backend.dto.pcms.reservation;

import lombok.Data;
import lombok.NoArgsConstructor;
import njb.backend.model.Reservation;

import java.time.format.DateTimeFormatter;

@Data
@NoArgsConstructor
public class ReservationResponseDto {
    private Long reservationId;
    private String studentName;
    private String pcSerialNumber;
    private String date;
    private String periodName;
    private String status;

    /**
     * ReservationエンティティからReservationResponseDtoを生成するコンストラクタです。
     * @param reservation 変換元のReservationエンティティ
     */
    public ReservationResponseDto(Reservation reservation) {
        this.reservationId = reservation.getId();
        this.studentName = reservation.getUser().getName();
        this.pcSerialNumber = reservation.getPc().getSerialNumber();
        this.date = reservation.getDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        this.periodName = reservation.getPeriod().getName();
        this.status = reservation.getStatus().toString();
    }

}
