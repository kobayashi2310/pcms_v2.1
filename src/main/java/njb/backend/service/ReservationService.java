package njb.backend.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import njb.backend.dto.pcms.reservation.ReservationRequestDto;
import njb.backend.dto.pcms.reservation.ReservationResponseDto;
import njb.backend.model.Pc;
import njb.backend.model.Period;
import njb.backend.model.Reservation;
import njb.backend.model.User;
import njb.backend.repository.PcRepository;
import njb.backend.repository.PeriodRepository;
import njb.backend.repository.ReservationRepository;
import njb.backend.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final UserRepository userRepository;
    private final PcRepository pcRepository;
    private final PeriodRepository periodRepository;

    /**
     * 指定された日付の予約リストを取得します。
     * @param date 取得する日付
     * @return 予約情報のDTOリスト
     */
    public List<ReservationResponseDto> getReservationsByDate(LocalDate date) {
        return reservationRepository.findByDateOrderByPeriod_PeriodAsc(date).stream()
                .map(ReservationResponseDto::new)
                .collect(Collectors.toList());
    }

    /**
     * 新しい予約を作成します。
     * @param dto 予約リクエスト情報
     * @param studentId 予約者の学籍番号
     */
    public void createReservation(ReservationRequestDto dto, String studentId) {
        User user = userRepository.findByStudentId(studentId)
                .orElseThrow(() -> new IllegalArgumentException("指定された学生IDのユーザーが見つかりません。"));
        Pc pc = pcRepository.findById(dto.getPcId())
                .orElseThrow(() -> new IllegalArgumentException("指定されたPCが見つかりません。"));

        List<Byte> periodIds = dto.getPeriodIds();
        Collections.sort(periodIds);

        if (periodIds.isEmpty()) {
            throw new IllegalArgumentException("時限が選択されていません。");
        }

        for (int i = 0; i < periodIds.size() - 1; i++) {
            if (periodIds.get(i + 1) - periodIds.get(i) != 1) {
                throw new IllegalArgumentException("時限は連続して選択してください。");
            }
        }

        List<Reservation> reservationsToSave = new ArrayList<>();
        for (Byte periodId : periodIds) {
            Period period = periodRepository.findById(periodId)
                    .orElseThrow(() -> new IllegalArgumentException("ID: " + periodId + " の時限が見つかりません。"));

            boolean isAlreadyBooked = reservationRepository.existsByPc_IdAndDateAndPeriod_Period(pc.getId(), dto.getDate(), period.getPeriod());
            if (isAlreadyBooked) {
                throw new IllegalArgumentException(
                        dto.getDate() + " " + pc.getSerialNumber() + " の " + period.getName() + " は既に予約されています。"
                );
            }

            Reservation reservation = new Reservation();
            reservation.setUser(user);
            reservation.setPc(pc);
            reservation.setDate(dto.getDate());
            reservation.setPeriod(period);
            reservation.setReason(dto.getReason());
            reservation.setStatus(Reservation.ReservationStatus.PENDING_APPROVAL);
            reservationsToSave.add(reservation);
        }

        reservationRepository.saveAll(reservationsToSave);
    }
}

