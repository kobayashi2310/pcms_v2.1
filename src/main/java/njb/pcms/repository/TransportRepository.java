package njb.pcms.repository;

import njb.pcms.model.Transport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 持ち出しのリポジトリ
 */
@Repository
public interface TransportRepository extends JpaRepository<Transport, Long> {

    /**
     * 指定されたステータスを持つ持ち出しのリストを取得します。
     * 
     * @param status 検索するステータス
     * @return 持ち出しのリスト
     */
    List<Transport> findByStatus(Transport.TransportStatus status);

    /**
     * 作成日時降順で全ての持ち出しを取得します。
     * 
     * @return 持ち出しのリスト
     */
    List<Transport> findAllByOrderByCreatedAtDesc();

    /**
     * 指定されたPC IDとステータスを持つ持ち出しの存在を確認します。
     * (WHERE pc_id = :pcId AND status = :status)
     * 
     * @param pcId   検索するPCのID
     * @param status 検索するステータス
     * @return 持ち出しの存在を表すboolean値
     */
    boolean existsByPc_IdAndStatus(Long pcId, Transport.TransportStatus status);

    /**
     * 指定されたユーザーIDとステータスを持つ持ち出しの存在を確認します。
     * (WHERE user_id = :userId AND status = :status)
     * 
     * @param userId 検索するユーザーのID
     * @param status 検索するステータス
     * @return 持ち出しの存在を表すboolean値
     */
    boolean existsByUser_IdAndStatus(Long userId, Transport.TransportStatus status);

    /**
     * 指定されたPC ID、ステータス、作成日、および予想返却日の条件に該当する輸送レコードが存在するかどうかを確認します。
     * レコードは、指定されたステータスに一致し、指定された作成日以前に作成され、予想返却日が指定された返却日以降である必要があります。
     * (WHERE pc_id = :pcId AND status = :status AND created_at <= :createdAt AND
     * expected_return_date >= :expectedReturnDate)
     * 
     * @param pcId               記録がチェックされているPCのID
     * @param status             トランスポートレコードのステータス
     * @param createdAt          記録の作成日の上限
     * @param expectedReturnDate 記録の予想返却日の下限（その日を含む）
     * @return 指定された条件を満たすレコードが存在する場合は true、そうでない場合は false
     */
    boolean existsByPc_IdAndStatusAndCreatedAtLessThanEqualAndExpectedReturnDateGreaterThanEqual(
            Long pcId,
            Transport.TransportStatus status,
            LocalDateTime createdAt,
            LocalDate expectedReturnDate);

    /**
     * 輸送記録のステータス、作成日、および予想返却日に基づいて、輸送記録のリストを取得します。
     * このメソッドは、以下の条件で記録をフィルタリングします。<br/>
     * - ステータスは指定された値と一致 <br/>
     * - 作成日は指定された日付と等しいかそれ以前 <br/>
     * - 予想される返却日は指定された日付以上 <br/>
     * (WHERE status = :status AND created_at <= :dateAsCreatedAt AND
     * expected_return_date >= :dateAsExpectedReturnDate)
     *
     * @param status                   フィルタリングする記録のステータス
     * @param dateAsCreatedAt          記録の作成日の上限
     * @param dateAsExpectedReturnDate 記録の返却予定日の下限
     * @return 指定された条件を満たす{@link Transport}エンティティのリスト
     */
    List<Transport> findByStatusAndCreatedAtLessThanEqualAndExpectedReturnDateGreaterThanEqual(
            Transport.TransportStatus status,
            LocalDateTime dateAsCreatedAt,
            LocalDate dateAsExpectedReturnDate);

}
