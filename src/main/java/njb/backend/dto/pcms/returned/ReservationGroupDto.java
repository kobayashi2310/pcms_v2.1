package njb.backend.dto.pcms.returned;

import lombok.Data;
import njb.backend.model.Reservation;

import java.util.List;

@Data
public class ReservationGroupDto {
    private String date;
    private String pcSerialNumber;
    private String startPeriodName;
    private String endPeriodName;
    private Reservation.ReservationStatus status;
    private List<Long> reservationIds;

    public String getPeriodRange() {
        if (startPeriodName.equals(endPeriodName)) {
            return startPeriodName;
        }
        return startPeriodName + " - " + endPeriodName;
    }
}
