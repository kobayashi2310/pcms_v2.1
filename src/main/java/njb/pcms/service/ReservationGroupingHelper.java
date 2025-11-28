package njb.pcms.service;

import njb.pcms.dto.pcms.reservation.ReservationGroupDto;
import njb.pcms.model.Reservation;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ReservationGroupingHelper {

    /**
     * 特定の基準に基づいて、予約オブジェクトのリストを ReservationGroupDto オブジェクトのリストにグループ化します。
     *
     * @param reservations グループ化する予約のリスト
     * @return 各項目が連続する予約のグループを表すオブジェクトへの予約グループのリスト
     */
    public List<ReservationGroupDto> groupReservations(List<Reservation> reservations) {
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
     * @param currentGroupList   現在のグループを表す予約リスト
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
     * 予約オブジェクトのリストを予約グループ オブジェクトに変換します
     *
     * @param groupList 変換する予約オブジェクトのリスト。null または空にすることはできません。
     * @return リストから集約された情報を含むReservationGroupDtoオブジェクト。
     *         入力リストがnullまたは空の場合はnull。
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
                        .collect(Collectors.toList()));
        dto.setReason(first.getReason());
        dto.setCreatedAt(first.getCreatedAt());
        return dto;
    }
}
