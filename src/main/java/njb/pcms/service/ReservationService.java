package njb.pcms.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import njb.pcms.dto.pcms.reservation.ReservationGroupDto;
import njb.pcms.dto.pcms.reservation.ReservationRequestDto;
import njb.pcms.dto.pcms.returned.ReturnReportDto;
import njb.pcms.model.*;
import njb.pcms.repository.*;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static njb.pcms.model.Reservation.ReservationStatus.*;

@Service
@Transactional
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final UserRepository userRepository;
    private final PcRepository pcRepository;
    private final PeriodRepository periodRepository;
    private final TransportRepository transportRepository;

    /**
     * 指定された日付の予約リストを、連続した時限でグループ化して取得します。
     * @param date 取得する日付
     * @return グループ化された予約情報のDTOリスト
     */
    public List<ReservationGroupDto> getGroupedReservationsByDate(LocalDate date) {
        List<Reservation> reservations = reservationRepository.findByDateOrderByPc_IdAscUser_IdAscPeriod_PeriodAsc(date);
        return groupReservations(reservations);
    }

    /**
     * 指定された日付の予約済み PC ID とそれに対応する予約済み期間のセットのマッピングを取得します。
     *
     * @param date 予約された PC と期間を取得する必要がある特定の日付
     * @return キーがLong型のPC ID、値が指定された日付に予約されている期間（Byte型）のセットであるマップ
     */
    public Map<Long, Set<Byte>> getBookedPcsAndPeriodsForDate(LocalDate date) {
        var reservations = reservationRepository.findByDateOrderByPc_IdAscUser_IdAscPeriod_PeriodAsc(date);
        Map<Long, Set<Byte>> bookedMap = reservations.stream()
                .collect(Collectors.groupingBy(
                   reservation -> reservation.getPc().getId(),
                   Collectors.mapping(reservation -> reservation
                           .getPeriod().getPeriod(),
                           Collectors.toSet()
                   )
                ));

        List<Transport> activeTransportsOnDate = transportRepository.findByStatusAndCreatedAtLessThanEqualAndExpectedReturnDateGreaterThanEqual(
                Transport.TransportStatus.IN_PROGRESS,
                LocalDateTime.parse(date + "T00:00:00", DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                date
        );

        Set<Byte> allPeriods = Set.of((byte)1, (byte)2, (byte)4);
        for (Transport transport : activeTransportsOnDate) {
            bookedMap.put(transport.getPc().getId(), allPeriods);
        }

        return bookedMap;
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

        boolean isTransported = transportRepository.existsByPc_IdAndStatusAndCreatedAtLessThanEqualAndExpectedReturnDateGreaterThanEqual(
                pc.getId(),
                Transport.TransportStatus.IN_PROGRESS,
                LocalDateTime.parse(dto.getDate() + "T00:00:00", DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                dto.getDate()
        );
        if (isTransported) {
            throw new IllegalArgumentException(
                    String.format(
                            "このPCは %s にメンテナンス期間または持ち出し中のため予約できません。",
                            dto.getDate()
                    )
            );
        }

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
     * 指定された学生の予約を、連続した時限でグループ化して取得します。
     * @param studentId 検索する学生の学籍番号
     * @return グループ化された予約情報のDTOリスト
     */
    public List<ReservationGroupDto> findGroupedReservationsByStudentId(String studentId) {
        User user = userRepository.findByStudentId(studentId)
                .orElseThrow(() -> new UsernameNotFoundException("指定された学生IDのユーザーが見つかりません"));

        List<Reservation> userReservations = reservationRepository.findByUserOrderByDateDescPeriod_PeriodAsc(user);
        return groupReservations(userReservations);
    }

    /**
     * 予約オブジェクトのリストを予約グループ オブジェクトに変換します
     *
     * @param groupList 変換する予約オブジェクトのリスト。null または空にすることはできません。
     * @return リストから集約された情報を含むReservationGroupDtoオブジェクト。
     * 入力リストがnullまたは空の場合はnull。
     */
    private ReservationGroupDto convertListToGroupDto(List<Reservation> groupList) {
        if (groupList == null || groupList.isEmpty()) {
            return null;
        }
        Reservation first = groupList.getFirst();
        Reservation last = groupList.getLast();

        ReservationGroupDto dto = new ReservationGroupDto();
        dto.setDate(first.getDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        dto.setStudentName(first.getUser().getName());
        dto.setPcSerialNumber(first.getPc().getSerialNumber());
        dto.setStartPeriodName(first.getPeriod().getName());
        dto.setEndPeriodName(last.getPeriod().getName());
        dto.setStatus(first.getStatus());
        dto.setReservationIds(
                groupList.stream()
                        .map(Reservation::getId)
                        .collect(Collectors.toList())
        );
        dto.setReason(first.getReason());
        dto.setCreatedAt(first.getCreatedAt());
        return dto;
    }

    /**
     * 特定の基準に基づいて、予約オブジェクトのリストを ReservationGroupDto オブジェクトのリストにグループ化します。
     *
     * @param reservations グループ化する予約のリスト
     * @return 各項目が連続する予約のグループを表すオブジェクトへの予約グループのリスト
     */
    private List<ReservationGroupDto> groupReservations(List<Reservation> reservations) {
        List<ReservationGroupDto> groupedList = new ArrayList<>();
        if (reservations.isEmpty()) {
            return groupedList;
        }

        List<Reservation> currentGroupList = new ArrayList<>();
        for (Reservation currentReservation : reservations) {
            if (currentGroupList.isEmpty()) {
                currentGroupList.add(currentReservation);
            } else {
                boolean isConsecutive = isConsecutive(currentReservation, currentGroupList);

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

    /**
     * 指定された予約が、指定されたグループ リスト内の最後の予約に連続しているかどうかを確認します。
     *
     * @param currentReservation 連続性を確認する予約
     * @param currentGroupList 現在のグループを表す予約リスト
     * @return 指定された予約がグループリストの最後の予約に連続している場合は true、そうでない場合は false
     */
    private boolean isConsecutive(Reservation currentReservation, List<Reservation> currentGroupList) {
        Reservation lastReservationInGroup = currentGroupList.getLast();

        return lastReservationInGroup.getUser().getId().equals(currentReservation.getUser().getId()) &&
                lastReservationInGroup.getPc().getId().equals(currentReservation.getPc().getId()) &&
                lastReservationInGroup.getStatus() == currentReservation.getStatus() &&
                lastReservationInGroup.getDate().equals(currentReservation.getDate()) &&
                (lastReservationInGroup.getPeriod().getPeriod() + 1 == currentReservation.getPeriod().getPeriod());
    }

    /**
     * 学生が自身の予約（複数可）をまとめて返却報告します。
     * @param dto 返却報告の内容（予約IDリストと返却理由）
     * @param studentId 返却報告を行う学生の学籍番号
     */
    public void reportReturnByStudent(ReturnReportDto dto, String studentId) {
        if (dto.getReservationIds() == null || dto.getReservationIds().isEmpty()) {
            throw new IllegalArgumentException("返却対象の予約が指定されていません");
        }

        List<Reservation> reservationsToUpdate = new ArrayList<>();
        for (Long reservationId : dto.getReservationIds()) {
            Reservation reservation = reservationRepository.findById(reservationId)
                    .orElseThrow(() -> new IllegalArgumentException("指定された予約が見つかりません。ID:" + reservationId));

            if (!reservation.getUser().getStudentId().equals(studentId)) {
                throw new SecurityException("あなた以外の予約を操作することはできません");
            }
            if (reservation.getStatus() != Reservation.ReservationStatus.APPROVED) {
                throw new IllegalStateException("予約ID:" + reservationId + "は承認済みの予約ではないため、返却できません");
            }

            reservation.setStatus(Reservation.ReservationStatus.RETRACTED);
            reservation.setRetractedAt(LocalDateTime.now());
            reservation.setRetractionReason(dto.getRetractionReason());
            reservationsToUpdate.add(reservation);
        }
        reservationRepository.saveAll(reservationsToUpdate);
    }

    /**
     * 学生が自身の予約（複数可）をまとめてキャンセルします。
     * @param reservationIds キャンセルする予約IDのリスト
     * @param studentId 操作を行う学生の学籍番号
     */
    public void cancelReservationsByStudent(List<Long> reservationIds, String studentId) {
        if (reservationIds == null || reservationIds.isEmpty()) {
            throw new IllegalArgumentException("キャンセル対象の予約が指定されていません");
        }

        List<Reservation> reservationsToDelete = new ArrayList<>();
        for (Long reservationId : reservationIds) {
            Reservation reservation = reservationRepository.findById(reservationId)
                    .orElseThrow(() -> new IllegalArgumentException("指定された予約が見つかりません"));

            if (!reservation.getUser().getStudentId().equals(studentId)) {
                throw new UsernameNotFoundException("自分以外の予約をキャンセルすることはできません");
            }
            if (reservation.getStatus() != Reservation.ReservationStatus.PENDING_APPROVAL) {
                throw new IllegalStateException("予約ID:" + reservationId + "は承認待ちではないため、キャンセルできません");
            }
            reservationsToDelete.add(reservation);
        }
        reservationRepository.deleteAll(reservationsToDelete);
    }

    public List<ReservationGroupDto> getPendingReservations() {
        List<Reservation> pending = reservationRepository.findByStatusOrderByDateAscPeriod_PeriodAsc(PENDING_APPROVAL);
        return groupReservations(pending);
    }

    /**
     * 予約を承認します（管理者機能）
     * @param reservationIds 承認する予約IDのリスト
     */
    public void approveReservations(List<Long> reservationIds) {
        if (reservationIds == null || reservationIds.isEmpty()) {
            throw new IllegalArgumentException("承認対象の予約が指定されていません");
        }
        List<Reservation> reservationsToApprove = new ArrayList<>();
        for (Long id : reservationIds) {
            Reservation reservation = reservationRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("指定された予約が見つかりません。ID: " + id));

            if (reservation.getStatus() != PENDING_APPROVAL) {
                throw new IllegalStateException("予約ID: " + id + " は承認待ちではありません。");
            }
            reservation.setStatus(APPROVED); // 承認済みに変更
            reservation.setApprovedAt(LocalDateTime.now());
            reservationsToApprove.add(reservation); // リストに追加
        }
        reservationRepository.saveAll(reservationsToApprove); // ループの外で一括保存
    }

    /**
     * 予約を否認（削除）します（管理者機能）
     * @param reservationIds 否認する予約IDのリスト
     */
    public void denyReservations(List<Long> reservationIds) {
        if (reservationIds == null || reservationIds.isEmpty()) {
            throw new IllegalArgumentException("否認対象の予約が指定されていません。");
        }
        List<Reservation> reservationsToDeny = new ArrayList<>();
        for (Long id : reservationIds) {
            Reservation reservation = reservationRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("指定された予約が見つかりません。ID: " + id));

            if (reservation.getStatus() != PENDING_APPROVAL) {
                throw new IllegalStateException("予約ID: " + id + " は承認待ちではありません。");
            }
            reservationsToDeny.add(reservation);
        }
        reservationRepository.deleteAll(reservationsToDeny);
    }
}
