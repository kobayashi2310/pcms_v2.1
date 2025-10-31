package njb.pcms.dto.pcms.reservation;

import lombok.Data;
import njb.pcms.model.Reservation;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ReservationGroupDto {

    private String date;
    private String studentName;
    private String pcSerialNumber;
    private String startPeriodName;
    private String endPeriodName;
    private Reservation.ReservationStatus status;
    private List<Long> reservationIds;
    private String reason;
    private LocalDateTime createdAt;

    public String getPeriodRange() {
        if (startPeriodName.equals(endPeriodName)) {
            return startPeriodName;
        }
        return String.format(
                "%s - %s",
                startPeriodName, endPeriodName
        );
    }

}
