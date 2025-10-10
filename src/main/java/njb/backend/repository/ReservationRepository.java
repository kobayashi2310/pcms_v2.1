package njb.backend.repository;

import njb.backend.model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    /**
     * 指定されたPC、日付、時限IDで予約が存在するかどうかを確認します。
     * @param pcId PCのID
     * @param date 日付
     * @param periodId 時限のID
     * @return 予約が存在すればtrue
     */
    boolean existsByPc_IdAndDateAndPeriod_Period(Long pcId, LocalDate date, Byte periodId);

    /**
     * 指定された日付の予約を時限の昇順で検索します。
     * @param date 検索する日付
     * @return 予約のリスト
     */
    List<Reservation> findByDateOrderByPeriod_PeriodAsc(LocalDate date);

}
