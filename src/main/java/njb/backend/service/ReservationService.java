package njb.backend.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import njb.backend.dto.pcms.reservation.ReservationRequestDto;
import njb.backend.dto.pcms.reservation.ReservationResponseDto;
import njb.backend.dto.pcms.returned.ReservationGroupDto;
import njb.backend.dto.pcms.returned.ReturnReportDto;
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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
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
     * 指定された日付の予約済みPCと時限のマップを取得します。
     * @param date 取得する日付
     * @return Key: PC ID, Value: 予約済みの時限IDのセット
     */
    public Map<Long, Set<Byte>> getBookedPcsAndPeriodsForDate(LocalDate date) {
        List<Reservation> reservations = reservationRepository.findByDateOrderByPeriod_PeriodAsc(date);
        return reservations.stream()
                .collect(Collectors.groupingBy(
                        reservation -> reservation.getPc().getId(),
                        Collectors.mapping(reservation -> reservation
                                .getPeriod().getPeriod(),
                                Collectors.toSet()
                        )
                ));
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

    /**
     * 指定された学生の予約リストを取得します。
     * @param studentId 検索する学生の学籍番号
     * @return 予約情報のDTOリスト
     */
    public List<ReservationGroupDto> findGroupedReservationsByStudentId(String studentId) {
        User user = userRepository.findByStudentId(studentId)
                .orElseThrow(() -> new IllegalArgumentException("指定された学生IDのユーザーが見つかりません。"));

        List<Reservation> userReservations = reservationRepository.findByUserOrderByDateDescPeriod_PeriodAsc(user);

        List<ReservationGroupDto> groupedList = new ArrayList<>();
        if (userReservations.isEmpty()) {
            return groupedList;
        }

        List<Reservation> currentGroupList = new ArrayList<>();
        for (Reservation currentReservation : userReservations) {
            if (currentGroupList.isEmpty()) {
                currentGroupList.add(currentReservation);
            } else {
                boolean isConsecutive = isIsConsecutive(currentReservation, currentGroupList);

                if (isConsecutive) {
                    currentGroupList.add(currentReservation);
                } else {
                    groupedList.add(convertListToGroupDto(currentGroupList));
                    currentGroupList.clear();
                    currentGroupList.add(currentReservation);
                }
            }
        }

        groupedList.add(convertListToGroupDto(currentGroupList));

        return groupedList;
    }

    private boolean isIsConsecutive(Reservation currentReservation, List<Reservation> currentGroupList) {
        Reservation lastReservationInGroup = currentGroupList.getLast();

        return lastReservationInGroup.getDate().equals(currentReservation.getDate()) &&
                lastReservationInGroup.getPc().getId().equals(currentReservation.getPc().getId()) &&
                lastReservationInGroup.getStatus() == currentReservation.getStatus() &&
                (lastReservationInGroup.getPeriod().getPeriod() + 1 == currentReservation.getPeriod().getPeriod());
    }

    private ReservationGroupDto convertListToGroupDto(List<Reservation> groupList) {
        if (groupList == null || groupList.isEmpty()) {
            return null;
        }
        Reservation first = groupList.getFirst();
        Reservation last = groupList.getLast();

        ReservationGroupDto dto = new ReservationGroupDto();
        dto.setDate(first.getDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        dto.setPcSerialNumber(first.getPc().getSerialNumber());
        dto.setStartPeriodName(first.getPeriod().getName());
        dto.setEndPeriodName(last.getPeriod().getName());
        dto.setStatus(first.getStatus());
        dto.setReservationIds(groupList.stream().map(Reservation::getId).collect(Collectors.toList()));
        return dto;
    }


    /**
     * 学生が返却報告します。
     * @param reservationId 返却する予約のID
     * @param studentId 返却報告を行う学生の学籍番号
     * @param dto 返却報告の内容（返却理由など）
     */
    public void reportReturnByStudent(Long reservationId, String studentId, ReturnReportDto dto) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("指定された予約が見つかりません。ID: " + reservationId));

        if (!reservation.getUser().getStudentId().equals(studentId)) {
            throw new SecurityException("他人の予約を操作することはできません");
        }

        if (reservation.getStatus() != Reservation.ReservationStatus.APPROVED) {
            throw new IllegalStateException("承認済みの予約のみ返却可能です");
        }

        reservation.setStatus(Reservation.ReservationStatus.RETRACTED);
        reservation.setRetractedAt(LocalDateTime.now());
        reservation.setRetractionReason(dto.getRetractionReason());
        reservationRepository.save(reservation);
    }

    public void cancelReservationsByStudent(List<Long> reservationIds, String studentId) {
        if (reservationIds == null || reservationIds.isEmpty()) {
            throw new IllegalArgumentException("キャンセル対象の予約が指定されていません");
        }

        List<Reservation> reservationsToDelete = new ArrayList<>();
        for (Long reservationId : reservationIds) {
            Reservation reservation = reservationRepository.findById(reservationId)
                    .orElseThrow(() -> new IllegalArgumentException("指定された予約が見つかりません。ID:" + reservationId));

            if (!reservation.getUser().getStudentId().equals(studentId)) {
                throw new SecurityException("他人の予約をキャンセルすることはできません");
            }

            if (reservation.getStatus() != Reservation.ReservationStatus.PENDING_APPROVAL) {
                throw new IllegalStateException("予約ID:" + reservationId + "は承認待ちではないため、キャンセルできません");
            }

            reservationsToDelete.add(reservation);
        }

        reservationRepository.deleteAll(reservationsToDelete);
    }

}
