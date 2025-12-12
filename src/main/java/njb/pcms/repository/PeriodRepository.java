package njb.pcms.repository;

import njb.pcms.model.Period;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * 時限のリポジトリ
 */
@Repository
public interface PeriodRepository extends JpaRepository<Period, Byte> {
}
