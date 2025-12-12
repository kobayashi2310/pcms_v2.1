package njb.pcms.repository;

import njb.pcms.model.Reservation;
import njb.pcms.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * 予約のリポジトリ
 * 
 * @author kobayashi
 */
@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    /**
     * 指定されたPC、日付、時限IDで予約が存在するかどうかを確認します。
     * 
     * @param pcId     PCのID
     * @param date     日付
     * @param periodId 時限のID
     * @return 予約が存在すればtrue
     */
    boolean existsByPc_IdAndDateAndPeriod_Period(Long pcId, LocalDate date, Byte periodId);

    /**
     * 指定された日付の予約を時限の昇順で検索します。
     * 
     * @param date 検索する日付
     * @return 予約のリスト
     */
    List<Reservation> findByDateOrderByPc_IdAscUser_IdAscPeriod_PeriodAsc(LocalDate date);

    /**
     * 指定されたユーザーの予約を日付降順、時限昇順で検索します。
     * 
     * @param user 検索するユーザー
     * @return 予約のリスト
     */
    List<Reservation> findByUserOrderByDateDescPeriod_PeriodAsc(User user);

    /**
     * 指定されたステータスの予約を日付昇順、時限昇順で検索します。
     * 
     * @param status 検索するステータス
     * @return 予約のリスト
     */
    List<Reservation> findByStatusOrderByDateAscPeriod_PeriodAsc(Reservation.ReservationStatus status);

    /**
     * 指定されたステータスの予約を返却日時降順で検索します。
     * 
     * @param status 検索するステータス
     * @return 予約のリスト
     */
    List<Reservation> findByStatusOrderByRetractedAtDesc(Reservation.ReservationStatus status);

}
