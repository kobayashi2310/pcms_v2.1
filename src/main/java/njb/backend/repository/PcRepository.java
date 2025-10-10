package njb.backend.repository;

import njb.backend.model.Pc;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PcRepository extends JpaRepository<Pc, Long> {
}
