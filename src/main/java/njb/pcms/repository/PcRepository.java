package njb.pcms.repository;

import njb.pcms.model.Pc;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * PCのリポジトリ
 */
@Repository
public interface PcRepository extends JpaRepository<Pc, Long> {
}
